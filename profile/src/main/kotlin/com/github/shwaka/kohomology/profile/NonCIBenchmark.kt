package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.profile.executable.ComputeReducedRowEchelonForm
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.f2.SetMatrixSpaceOverF2Boolean
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class NonCIBenchmark {
    @Benchmark
    fun computeReducedRowEchelonFormOverRational(): String {
        val executable = ComputeReducedRowEchelonForm(SparseMatrixSpaceOverRational, 1000)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeReducedRowEchelonFormOverF2(): String {
        val executable = ComputeReducedRowEchelonForm(SparseMatrixSpaceOverF2, 1000)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeReducedRowEchelonFormWithSetMatrix(): String {
        val executable = ComputeReducedRowEchelonForm(SetMatrixSpaceOverF2Boolean, 1000)
        executable.setup()
        return executable.main()
    }
}
