package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    val executableList: List<Executable> = listOf(
        CohomologyOfFreeLoopSpace(150),
        CohomologyOfFreeLoopSpaceWithLinearDegree(150, 200),
        ComputeRowEchelonForm(SparseMatrixSpaceOverBigRational)
    )
    println("Select script to profile: (default = 0)")
    executableList.mapIndexed { index, script ->
        println("$index: ${script.description}")
    }
    val index: Int = readLine()?.toIntOrNull() ?: 0
    val executable = executableList[index]
    println("Selected $index: ${executable.description}")
    executable.setup()
    // print("Press ENTER to continue!!!") // index 選択の際に待てるのでそれで十分
    readLine()
    executable.main()
}
