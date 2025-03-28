package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.free.monoid.MonomialListGeneratorAugmented
import com.github.shwaka.kohomology.free.monoid.MonomialListGeneratorBasic
import com.github.shwaka.kohomology.profile.executable.ComputeMonomialList
import com.github.shwaka.kohomology.profile.executable.ComputeReducedRowEchelonFormOfJordanMatrix
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
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SparseMatrixSpaceOverRational, 1000)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeReducedRowEchelonFormOverF2(): String {
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SparseMatrixSpaceOverF2, 1000)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeReducedRowEchelonFormWithSetMatrix(): String {
        val executable = ComputeReducedRowEchelonFormOfJordanMatrix(SetMatrixSpaceOverF2Boolean, 1000)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeMonomialListBasic(): String {
        val executable = ComputeMonomialList(30, ::MonomialListGeneratorBasic)
        executable.setup()
        return executable.main()
    }

    @Benchmark
    fun computeMonomialListAugmented(): String {
        val executable = ComputeMonomialList(30, ::MonomialListGeneratorAugmented)
        executable.setup()
        return executable.main()
    }
}
