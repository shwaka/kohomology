package com.github.shwaka.kohomology.profile

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    private val degreeLimit: Int = 50

    @Benchmark
    fun cohomologyOfFreeLoopSpace(): String {
        val executable = CohomologyOfFreeLoopSpace(this.degreeLimit)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegree(): String {
        val executable = CohomologyOfFreeLoopSpaceWithMultiDegree(this.degreeLimit)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(): String {
        val executable = CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(this.degreeLimit)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        val executable = IsomorphismToCohomologyOfFreePathSpace(n = 5, degreeLimit = 35)
        executable.setup()
        return executable.main()
    }
}
