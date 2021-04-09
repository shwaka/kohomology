package test

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    @Benchmark
    fun cohomologyOfFreeLoopSpace(): String {
        val sphereDim = 2
        val indeterminateList = listOf(
            Indeterminate("x", sphereDim),
            Indeterminate("y", sphereDim * 2 - 1)
        )
        val matrixSpace = DenseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)

        var result = ""
        for (degree in 0 until 50) {
            result += freeLoopSpace.cohomology[degree].toString() + "\n"
        }
        return result
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        val n = 5
        val indeterminateList = listOf(
            Indeterminate("c", 2),
            Indeterminate("x", 2 * n + 1)
        )
        val sphere = FreeDGAlgebra(DenseMatrixSpaceOverBigRational, indeterminateList) { (c, _) ->
            listOf(zeroGVector, c.pow(n + 1))
        }
        val freePathSpace = FreePathSpace(sphere)

        val cohomologyInclusion1 = freePathSpace.inclusion1.inducedMapOnCohomology()
        var result = ""
        for (degree in 0 until 35) {
            result += cohomologyInclusion1[degree].isIsomorphism().toString()
        }
        return result
    }
}
