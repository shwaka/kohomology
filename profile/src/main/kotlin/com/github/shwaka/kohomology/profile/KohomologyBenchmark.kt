package com.github.shwaka.kohomology.profile

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
    fun cohomologyOfFreeLoopSpaceWithMultiDegree(): String {
        return CohomologyOfFreeLoopSpaceWithMultiDegree(50).main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(): String {
        return CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(50).main()
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        return IsomorphismToCohomologyOfFreePathSpace(n = 5, degreeLimit = 35).main()
    }
}
