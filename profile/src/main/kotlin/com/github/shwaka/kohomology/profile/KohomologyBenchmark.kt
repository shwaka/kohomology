package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class KohomologyBenchmark {
    private val degreeLimit: Int = 50

    @Benchmark
    fun cohomologyOfFreeLoopSpace(): String {
        val executable = CohomologyOfFreeLoopSpace(SparseMatrixSpaceOverRational, this.degreeLimit)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegree(): String {
        val executable = CohomologyOfFreeLoopSpaceWithMultiDegree(
            SparseMatrixSpaceOverRational,
            this.degreeLimit,
        )
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun cohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(): String {
        val executable = CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree(
            SparseMatrixSpaceOverRational,
            this.degreeLimit,
        )
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun isomorphismToCohomologyOfFreePathSpace(): String {
        val executable = IsomorphismToCohomologyOfFreePathSpace(
            SparseMatrixSpaceOverRational,
            n = 5,
            degreeLimit = 35
        )
        executable.setup()
        return executable.main()
    }
}
