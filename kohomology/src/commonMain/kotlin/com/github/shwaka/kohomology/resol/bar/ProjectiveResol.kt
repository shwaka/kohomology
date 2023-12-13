package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.complex.ComplexOfFreeModules
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.resol.module.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

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
    private lateinit var augmentation: ModuleMap<BA, FreeModuleBasisName<BA, ProjectiveResolBasisName>, BV, S, V, M>

    private val zeroFreeModule = FreeModule(this.coeffAlgebra, emptyList<ProjectiveResolBasisName>())
    private val zeroFreeModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = zeroFreeModule,
        target = zeroFreeModule,
        values = emptyList(),
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
                val generatingBasisNames = differentialTargets.indices.map {
                    ProjectiveResolBasisName(degree = degree, index = it)
                }
                val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
                this.augmentation = ModuleMap.fromVectors(
                    source = freeModule,
                    target = this.module,
                    vectors = freeModule.underlyingVectorSpace.basisNames.map { freeModuleBasisName ->
                        val coeff = coeffAlgebra.fromBasisName(freeModuleBasisName.algebraBasisName)
                        val index: Int = freeModule.generatingBasisNames.indexOf(freeModuleBasisName.generatingBasisName)
                        this.module.context.run {
                            coeff * differentialTargets[index]
                        }
                    }
                )
                this.moduleCache[degree] = freeModule
                this.differentialCache[degree] = FreeModuleMap.fromValuesOnGeneratingBasis(
                    source = freeModule,
                    target = this.zeroFreeModule,
                    values = List(freeModule.generatingBasisNames.size) {
                        this.zeroFreeModule.underlyingVectorSpace.zeroVector
                    },
                )
            }
            (degree < 0) -> {
                this.compute(degree + 1)
                val diffOrAug = when {
                    (degree == -1) -> this.augmentation
                    (degree < -1) -> this.getDifferential(degree + 1)
                    else -> throw Exception("This can't happen!")
                }
                val kernel = diffOrAug.kernel()
                val incl = kernel.inclusion
                val differentialTargets = kernel.findSmallGenerator().map { incl(it) }
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
        return FreeModuleMap.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = targetModule,
            values = targetVectors,
        )
    }

    private fun getModule(degree: Int): FreeModule<BA, ProjectiveResolBasisName, S, V, M> {
        this.moduleCache[degree]?.let { return it }
        this.compute(degree)
        return this.moduleCache[degree] ?: throw Exception("This can't happen!")
    }

    private fun getDifferential(degree: Int): FreeModuleMap<BA, ProjectiveResolBasisName, ProjectiveResolBasisName, S, V, M> {
        this.differentialCache[degree]?.let { return it }
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
).complexOfFreeModules {
    public companion object {
        public operator fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            coeffAlgebra: MonoidRing<E, S, V, M>,
        ): ProjectiveResol<E, StringBasisName, S, V, M> {
            val vectorSpace = VectorSpace(coeffAlgebra.numVectorSpace, listOf("x"))
            val module = coeffAlgebra.getModuleWithTrivialAction(vectorSpace)
            return ProjectiveResol(coeffAlgebra, module)
        }
    }
}
