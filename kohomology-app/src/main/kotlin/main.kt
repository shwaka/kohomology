import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.model.freeLoopSpace
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational

fun main() {
    val sphereDim = 4
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = DenseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
        listOf(zeroGVector, x.pow(2))
    }
    val freeLoopSpace = freeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

    for (degree in 0 until 20) {
        val basis = freeLoopSpace.cohomology[degree].getBasis()
        println("H^$degree = Q$basis")
    }

    freeLoopSpace.withDGAlgebraContext {
        println(d(sy))
        val n = 3
        val alpha = x.pow(2) * sx * sy.pow(n) // = d(y * sx * sy.pow(n))
        val classOfAlpha = freeLoopSpace.cohomologyClassOf(alpha)
        println("[alpha] = $classOfAlpha")
    }
}
