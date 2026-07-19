package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.echeloncalc.SparseRowEchelonFormAlgorithm
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpace
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceOfArkowitzLupton
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegree
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree
import com.github.shwaka.kohomology.profile.executable.ComputeReducedRowEchelonFormOfJordanMatrix
import com.github.shwaka.kohomology.profile.executable.ComputeReducedTransformationOfDifferential
import com.github.shwaka.kohomology.profile.executable.ComputeRowEchelonFormOfDifferential
import com.github.shwaka.kohomology.profile.executable.Executable
import com.github.shwaka.kohomology.profile.executable.IsomorphismToCohomologyOfFreePathSpace
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.specific.f2.SetMatrixSpaceOverF2Boolean
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    val arDegreeLimit = 300
    val executableList: List<Executable> = listOf(
        CohomologyOfFreeLoopSpace(SparseMatrixSpaceOverRational, 150),
        CohomologyOfFreeLoopSpaceWithMultiDegree(SparseMatrixSpaceOverRational, 150),
        CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(SparseMatrixSpaceOverRational, 100),
        IsomorphismToCohomologyOfFreePathSpace(SparseMatrixSpaceOverRational, n = 5, degreeLimit = 70),
        ComputeRowEchelonFormOfDifferential(SparseMatrixSpaceOverRational, label = "default"),
        ComputeRowEchelonFormOfDifferential(
            SparseMatrixSpace.from(
                numVectorSpace = SparseNumVectorSpaceOverRational,
                rowEchelonAlgorithm = SparseRowEchelonFormAlgorithm.Markowitz,
            ),
            label = "markowitz",
        ),
        ComputeReducedTransformationOfDifferential(SparseMatrixSpaceOverRational, label = "default"),
        ComputeReducedTransformationOfDifferential(
            SparseMatrixSpace.from(
                numVectorSpace = SparseNumVectorSpaceOverRational,
                rowEchelonAlgorithm = SparseRowEchelonFormAlgorithm.Markowitz,
            ),
            label = "markowitz",
        ),
        ComputeRowEchelonFormOfDifferential(DecomposedSparseMatrixSpaceOverRational),
        ComputeReducedRowEchelonFormOfJordanMatrix(SetMatrixSpaceOverF2Boolean, 5000),
        CohomologyOfFreeLoopSpaceOfArkowitzLupton(SparseMatrixSpaceOverRational, 250),
        CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree(
            SparseMatrixSpaceOverRational,
            arDegreeLimit,
            label = "sequential",
        ),
        CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree(
            SparseMatrixSpace.from(
                numVectorSpace = SparseNumVectorSpaceOverRational,
                rowEchelonAlgorithm = SparseRowEchelonFormAlgorithm.Parallel(
                    parallelMinSize = 128,
                    parallelChunkSize = 16,
                    parallelism = 8,
                ),
            ),
            arDegreeLimit,
            label = "parallel",
        ),
        CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree(
            SparseMatrixSpace.from(
                numVectorSpace = SparseNumVectorSpaceOverRational,
                rowEchelonAlgorithm = SparseRowEchelonFormAlgorithm.Indexed,
            ),
            arDegreeLimit,
            label = "indexed",
        ),
        CohomologyOfFreeLoopSpaceOfArkowitzLuptonWithShiftDegree(
            SparseMatrixSpace.from(
                numVectorSpace = SparseNumVectorSpaceOverRational,
                rowEchelonAlgorithm = SparseRowEchelonFormAlgorithm.Markowitz,
            ),
            arDegreeLimit,
            label = "markowitz",
        ),
    )
    val defaultChoice = 0
    println("Select script to profile: (default = $defaultChoice)")
    executableList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index: Int = readLine()?.toIntOrNull() ?: defaultChoice
    val executable = executableList[index]
    println("Selected $index: ${executable.description}")
    measureTimedValue {
        executable.setup()
    }.let { setupTime ->
        val seconds = "%.1f".format(setupTime.duration.toDouble(DurationUnit.SECONDS))
        println("Setup finished in ${seconds}s")
    }
    // print("Press ENTER to continue!!!")
    // readLine() // index 選択の際に待てるのでそれで十分
    measureTimedValue {
        executable.main()
    }.let { mainTime ->
        val seconds = "%.1f".format(mainTime.duration.toDouble(DurationUnit.SECONDS))
        println("main() finished in ${seconds}s")
    }
}
