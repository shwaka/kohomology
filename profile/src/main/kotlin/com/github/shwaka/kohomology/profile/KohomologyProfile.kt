package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    val executableList: List<Executable> = listOf(
        CohomologyOfFreeLoopSpace(150),
        CohomologyOfFreeLoopSpaceWithMultiDegree(150),
        CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(100),
        IsomorphismToCohomologyOfFreePathSpace(n = 5, degreeLimit = 70),
        ComputeRowEchelonForm(SparseMatrixSpaceOverBigRational),
        ComputeRowEchelonForm(DecomposedSparseMatrixSpaceOverBigRational),
    )
    println("Select script to profile: (default = 0)")
    executableList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index: Int = readLine()?.toIntOrNull() ?: 0
    val executable = executableList[index]
    println("Selected $index: ${executable.description}")
    executable.setup()
    // print("Press ENTER to continue!!!")
    // readLine() // index 選択の際に待てるのでそれで十分
    val timedValue = measureTimedValue {
        executable.main()
    }
    val seconds = "%.1f".format(timedValue.duration.inSeconds)
    println("  $seconds seconds")
}
