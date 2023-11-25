package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.ComplexOfFreeModules
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

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

    private val zeroFreeModule = FreeModule(this.coeffAlgebra, emptyList<ProjectiveResolBasisName>())
    private val zeroFreeModuleMap = FreeModuleMap(
        source = zeroFreeModule,
        target = zeroFreeModule,
        underlyingLinearMap = LinearMap.fromVectors(
            source = zeroFreeModule.underlyingVectorSpace,
            target = zeroFreeModule.underlyingVectorSpace,
            matrixSpace = coeffAlgebra.matrixSpace,
            vectors = emptyList(),
        )
    )

    private fun compute(degree: Int) {
        when {
            (degree > 0) -> {
                this.moduleCache[degree] = this.zeroFreeModule
                this.differentialCache[degree] = this.zeroFreeModuleMap
            }
            (degree == 0) -> {
                // surjection to this.module
                val differentialTargets = this.module.findSmallGenerator()
                val (freeModule, _) = this.hitVectors(0, this.module, differentialTargets)
                this.moduleCache[degree] = freeModule
                this.differentialCache[degree] = FreeModuleMap(
                    source = freeModule,
                    target = this.zeroFreeModule,
                    underlyingLinearMap = LinearMap.fromVectors(
                        source = freeModule.underlyingVectorSpace,
                        target = this.zeroFreeModule.underlyingVectorSpace,
                        matrixSpace = this.coeffAlgebra.matrixSpace,
                        vectors = List(freeModule.underlyingVectorSpace.dim) {
                            this.zeroFreeModule.underlyingVectorSpace.zeroVector
                        },
                    )
                )
            }
            (degree < 0) -> {
                this.compute(degree + 1)
                val kernel = this.getDifferential(degree + 1).kernel()
                val incl = kernel.inclusion
                val differentialTargets = kernel.findSmallGenerator().map { incl(it) }
                // val generatingBasisNames = differentialTargets.indices.map {
                //     ProjectiveResolBasisName(degree = degree, index = it)
                // }
                // val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
                // val freeModuleMap = FreeModuleMap(
                //     source = freeModule,
                //     target = this.getModule(degree + 1),
                //     underlyingLinearMap = LinearMap.fromVectors(
                //         source = freeModule.underlyingVectorSpace,
                //         target = this.getModule(degree + 1).underlyingVectorSpace,
                //         matrixSpace = this.coeffAlgebra.matrixSpace,
                //         vectors = differentialTargets,
                //     )
                // )
                val differential = this.hitVectors(degree, this.getModule(degree + 1), differentialTargets)
                this.moduleCache[degree] = differential.source
                this.differentialCache[degree] = differential
            }
        }
    }

    private fun hitVectors(
        degree: Int,
        targetModule: FreeModule<BA, ProjectiveResolBasisName, S, V, M>,
        targetVectors: List<Vector<FreeModuleBasisName<BA, ProjectiveResolBasisName>, S, V>>,
    ): FreeModuleMap<BA, ProjectiveResolBasisName, ProjectiveResolBasisName, S, V, M> {
        val generatingBasisNames = targetVectors.indices.map {
            ProjectiveResolBasisName(degree = degree, index = it)
        }
        val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
        return FreeModuleMap(
            source = freeModule,
            target = targetModule,
            underlyingLinearMap = LinearMap.fromVectors(
                source = freeModule.underlyingVectorSpace,
                target = targetModule.underlyingVectorSpace,
                matrixSpace = this.coeffAlgebra.matrixSpace,
                vectors = targetVectors,
            )
        )
    }

    private fun <B : BasisName> hitVectors(
        degree: Int,
        targetModule: Module<BA, B, S, V, M>,
        targetVectors: List<Vector<B, S, V>>,
    ): Pair<
        FreeModule<BA, ProjectiveResolBasisName, S, V, M>,
        ModuleMap<BA, FreeModuleBasisName<BA, ProjectiveResolBasisName>, B, S, V, M>
        > {
        // for the surjection to this.module
        val generatingBasisNames = targetVectors.indices.map {
            ProjectiveResolBasisName(degree = degree, index = it)
        }
        val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
        val moduleMap = ModuleMap(
            source = freeModule,
            target = targetModule,
            underlyingLinearMap = LinearMap.fromVectors(
                source = freeModule.underlyingVectorSpace,
                target = targetModule.underlyingVectorSpace,
                matrixSpace = this.coeffAlgebra.matrixSpace,
                vectors = targetVectors,
            )
        )
        return Pair(freeModule, moduleMap)
    }

    private fun getModule(degree: Int): FreeModule<BA, ProjectiveResolBasisName, S, V, M> {
        this.compute(degree)
        return this.moduleCache[degree] ?: throw Exception("This can't happen!")
    }

    private fun getDifferential(degree: Int): FreeModuleMap<BA, ProjectiveResolBasisName, ProjectiveResolBasisName, S, V, M> {
        this.compute(degree)
        return this.differentialCache[degree] ?: throw Exception("This can't happen!")
    }
}

public class ProjectiveResol<BA : BasisName, BV : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    coeffAlgebra: Algebra<BA, S, V, M>,
    module: Module<BA, BV, S, V, M>,
) : ComplexOfFreeModules<IntDegree, BA, ProjectiveResolBasisName, S, V, M> by ProjectiveResolFactory(
    coeffAlgebra,
    module,
).complexOfFreeModules
