package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.log.MatrixSpaceWithLog
import com.github.shwaka.kohomology.linalg.log.withLog
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpace
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegree
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree
import com.github.shwaka.kohomology.profile.executable.ComputeReducedRowEchelonFormOfJordanMatrix
import com.github.shwaka.kohomology.profile.executable.ComputeRowEchelonFormOfDifferential
import com.github.shwaka.kohomology.profile.executable.Executable
import com.github.shwaka.kohomology.profile.executable.IsomorphismToCohomologyOfFreePathSpace
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.f2.SetMatrixSpaceOverF2Boolean
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private class MeasurementTarget(
    val description: String,
    val executable: Executable,
    val matrixSpaceWithLog: MatrixSpaceWithLog<*, *, *>,
)

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> measurementTarget(
    matrixSpace: MatrixSpace<S, V, M>,
    createExecutable: (MatrixSpaceWithLog<S, V, M>) -> Executable,
): MeasurementTarget {
    val matrixSpaceWithLog = matrixSpace.withLog()
    val executable = createExecutable(matrixSpaceWithLog)
    return MeasurementTarget(
        description = executable.description,
        executable = executable,
        matrixSpaceWithLog = matrixSpaceWithLog,
    )
}

private fun measurementTargetList(): List<MeasurementTarget> {
    return listOf(
        measurementTarget(SparseMatrixSpaceOverRational) { matrixSpace ->
            CohomologyOfFreeLoopSpace(matrixSpace, 150)
        },
        measurementTarget(SparseMatrixSpaceOverRational) { matrixSpace ->
            CohomologyOfFreeLoopSpaceWithMultiDegree(matrixSpace, 150)
        },
        measurementTarget(SparseMatrixSpaceOverRational) { matrixSpace ->
            CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(matrixSpace, 100)
        },
        measurementTarget(SparseMatrixSpaceOverRational) { matrixSpace ->
            IsomorphismToCohomologyOfFreePathSpace(matrixSpace, n = 5, degreeLimit = 70)
        },
        measurementTarget(SparseMatrixSpaceOverRational) { matrixSpace ->
            ComputeRowEchelonFormOfDifferential(matrixSpace)
        },
        measurementTarget(DecomposedSparseMatrixSpaceOverRational) { matrixSpace ->
            ComputeRowEchelonFormOfDifferential(matrixSpace)
        },
        measurementTarget(SetMatrixSpaceOverF2Boolean) { matrixSpace ->
            ComputeReducedRowEchelonFormOfJordanMatrix(matrixSpace, 5000)
        },
    )
}

private fun selectTarget(targetList: List<MeasurementTarget>): MeasurementTarget {
    val defaultChoice = 0
    println("Select script to export operation measurements: (default = $defaultChoice)")
    targetList.forEachIndexed { index, target ->
        println("$index: ${target.description}")
    }
    val index = System.getProperty("measurementTarget")?.toIntOrNull()
        ?: readLine()?.toIntOrNull()
        ?: defaultChoice
    return targetList[index].also {
        println("Selected $index: ${it.description}")
    }
}

private fun File.writeCSV(csv: String) {
    this.parentFile.mkdirs()
    this.writeText(csv)
    println("Wrote ${this.path}")
}

private fun outputDir(): File {
    return File(System.getProperty("measurementOutputDir") ?: "build/kohomology/operation-measurements")
}

@ExperimentalTime
fun main() {
    val target = selectTarget(measurementTargetList())
    measureTimedValue {
        target.executable.setup()
    }.let { setupTime ->
        val seconds = "%.1f".format(setupTime.duration.toDouble(DurationUnit.SECONDS))
        println("Setup finished in ${seconds}s")
    }
    measureTimedValue {
        target.executable.main()
    }.let { mainTime ->
        val seconds = "%.1f".format(mainTime.duration.toDouble(DurationUnit.SECONDS))
        println("main() finished in ${seconds}s")
    }

    val outputDir = outputDir()
    File(outputDir, "matrix-operations.csv").writeCSV(target.matrixSpaceWithLog.logger.getMeasurementsCSV())
    File(outputDir, "ref-operations.csv").writeCSV(target.matrixSpaceWithLog.refLogger.getMeasurementsCSV())
    File(outputDir, "summaries.txt").writeText(target.matrixSpaceWithLog.getFormattedSummaries())
}
