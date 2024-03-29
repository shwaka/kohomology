package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpace
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegree
import com.github.shwaka.kohomology.profile.executable.CohomologyOfFreeLoopSpaceWithMultiDegreeWithShiftDegree
import com.github.shwaka.kohomology.profile.executable.ComputeReducedRowEchelonFormOfJordanMatrix
import com.github.shwaka.kohomology.profile.executable.IsomorphismToCohomologyOfFreePathSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF7
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.f2.SetMatrixSpaceOverF2Boolean
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
@Suppress("UNUSED") // Used from the task benchmark
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

    val matrixSize = 1500

    @Benchmark
    fun reducedRowEchelonFormOverRational(): String {
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SparseMatrixSpaceOverRational, this.matrixSize)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun reducedRowEchelonFormOverF2(): String {
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SetMatrixSpaceOverF2Boolean, this.matrixSize)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun reducedRowEchelonFormOverF7(): String {
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SparseMatrixSpaceOverF7, this.matrixSize)
        executable.setup()
        return executable.main()
    }
}
