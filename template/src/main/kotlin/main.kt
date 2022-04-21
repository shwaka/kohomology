import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    val sphereDim = 4
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
        listOf(zeroGVector, x.pow(2))
    }
    val freeLoopSpace = FreeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

    for (degree in 0 until 25) {
        val basis = freeLoopSpace.cohomology[degree].getBasis()
        println("H^$degree = Q$basis")
    }

    freeLoopSpace.context.run {
        println(d(sy))
        val n = 3
        val alpha = x.pow(2) * sx * sy.pow(n) // = d(y * sx * sy.pow(n))
        val classOfAlpha = freeLoopSpace.cohomologyClassOf(alpha)
        println("[alpha] = $classOfAlpha")
    }
}
