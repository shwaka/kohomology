package com.github.shwaka.kohomology.profile

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    private val degreeLimit: Int = 50

    @Benchmark
    fun cohomologyOfFreeLoopSpace(): String {
        return CohomologyOfFreeLoopSpace(this.degreeLimit).main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegree(): String {
        return CohomologyOfFreeLoopSpaceWithMultiDegree(this.degreeLimit).main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(): String {
        return CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(this.degreeLimit).main()
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        return IsomorphismToCohomologyOfFreePathSpace(n = 5, degreeLimit = 35).main()
    }
}
