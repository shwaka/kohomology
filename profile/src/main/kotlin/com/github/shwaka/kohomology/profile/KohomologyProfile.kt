package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    val executableList: List<Executable> = listOf(
        CohomologyOfFreeLoopSpace(150),
        CohomologyOfFreeLoopSpaceWithMultiDegree(150),
        CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(100),
        IsomorphismToCohomologyOfFreePathSpace(n = 5, degreeLimit = 70),
        ComputeRowEchelonForm(SparseMatrixSpaceOverRational),
        ComputeRowEchelonForm(DecomposedSparseMatrixSpaceOverRational),
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
        val seconds = "%.1f".format(setupTime.duration.inSeconds)
        println("Setup finished in ${seconds}s")
    }
    // print("Press ENTER to continue!!!")
    // readLine() // index 選択の際に待てるのでそれで十分
    measureTimedValue {
        executable.main()
    }.let { mainTime ->
        val seconds = "%.1f".format(mainTime.duration.inSeconds)
        println("main() finished in ${seconds}s")
    }
}
