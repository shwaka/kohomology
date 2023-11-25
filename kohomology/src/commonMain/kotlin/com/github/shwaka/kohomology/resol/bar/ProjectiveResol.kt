package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.ComplexOfFreeModules
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName

public data class ProjectiveResolBasisName(val degree: Int, val index: Int) : BasisName {
    init {
        require(degree <= 0) {
            "degree for ProjectiveResolBasisName must be non-positive, but $degree was given"
        }
    }
}

private class ProjectiveResolFactory<BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val coeffAlgebra: Algebra<BA, S, V, M>,
    private val module: Module<BA, BV, S, V, M>,
) {
    val complexOfFreeModules: ComplexOfFreeModules<IntDegree, BA, ProjectiveResolBasisName, S, V, M> =
        ComplexOfFreeModules(
            matrixSpace = coeffAlgebra.matrixSpace,
            degreeGroup = IntDegreeGroup,
            name = "ProjResol($module)",
            getModule = { this.getModule(it.value) },
            getDifferential = { this.getDifferential(it.value) }
        )

    private val moduleCache: MutableMap<Int, FreeModule<BA, ProjectiveResolBasisName, S, V, M>> = mutableMapOf()
    private val differentialCache: MutableMap<
        Int,
        FreeModuleMap<BA, ProjectiveResolBasisName, ProjectiveResolBasisName, S, V, M>
        > = mutableMapOf()

    private fun compute(degree: Int) {
        when {
            (degree > 0) -> {
                this.moduleCache[degree] = FreeModule(this.coeffAlgebra, emptyList())
                this.differentialCache[degree] = TODO()
            }
            (degree == 0) -> {
                // surjection to module
                this.moduleCache[degree] = TODO()
                this.differentialCache[degree] = TODO()
            }
            (degree < 0) -> {
                this.compute(degree + 1)
                val kernel = this.getDifferential(degree + 1).kernel()
                val incl = kernel.inclusion
                val differentialTargets = kernel.findSmallGenerator().map { incl(it) }
                TODO()
            }
        }
    }

    private fun getModule(degree: Int): FreeModule<BA, ProjectiveResolBasisName, S, V, M> {
        this.moduleCache[degree]?.let { return it }
        TODO()
    }

    private fun getDifferential(degree: Int): FreeModuleMap<BA, ProjectiveResolBasisName, ProjectiveResolBasisName, S, V, M> {
        this.differentialCache[degree]?.let { return it }
        TODO()
    }
}

public class ProjectiveResol<BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    coeffAlgebra: Algebra<BA, S, V, M>,
    module: Module<BA, BV, S, V, M>,
) : ComplexOfFreeModules<IntDegree, BA, ProjectiveResolBasisName, S, V, M> by ProjectiveResolFactory(
    coeffAlgebra,
    module,
).complexOfFreeModules
