package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.GeneralizedIndeterminate
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    @Benchmark
    fun cohomologyOfFreeLoopSpace(): String {
        return CohomologyOfFreeLoopSpace(50).main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithLinearDegree(): String {
        return CohomologyOfFreeLoopSpaceWithLinearDegree(50, 0).main()
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        val n = 5
        val indeterminateList = listOf(
            GeneralizedIndeterminate("c", 2),
            GeneralizedIndeterminate("x", 2 * n + 1)
        )
        val sphere = FreeDGAlgebra(SparseMatrixSpaceOverBigRational, indeterminateList) { (c, _) ->
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
