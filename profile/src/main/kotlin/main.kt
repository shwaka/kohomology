import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    print("Press ENTER to continue!!!")
    readLine()
    cohomologyOfFreeLoopSpace()
}

fun cohomologyOfFreeLoopSpace(): String {
    val sphereDim = 2
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
    val freeLoopSpace = FreeLoopSpace(sphere)

    var result = ""
    for (degree in 0 until 150) {
        result += freeLoopSpace.cohomology[degree].toString() + "\n"
    }
    return result
}
