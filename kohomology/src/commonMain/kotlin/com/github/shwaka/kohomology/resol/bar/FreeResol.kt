package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.complex.FreeComplex
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.ModuleMapFromFreeModule
import com.github.shwaka.kohomology.resol.module.MonoidRing
import com.github.shwaka.kohomology.resol.module.finder.SmallGeneratorFinder
import com.github.shwaka.kohomology.resol.module.TrivialModule
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import kotlin.math.min

public data class FreeResolBasisName(val degree: Int, val index: Int) : BasisName {
    init {
        require(degree <= 0) {
            "degree for FreeResolBasisName must be non-positive, but $degree was given"
        }
    }
}

private class FreeResolFactory<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val coeffAlgebra: Algebra<BA, S, V, M>,
    val module: Module<BA, B, S, V, M>,
    private val finder: SmallGeneratorFinder<BA, S, V, M, Algebra<BA, S, V, M>>,
) {
    val freeComplex: FreeComplex<IntDegree, BA, FreeResolBasisName, S, V, M> =
        FreeComplex(
            matrixSpace = coeffAlgebra.matrixSpace,
            degreeGroup = IntDegreeGroup,
            name = "FreeResol($module)",
            getModule = { this.getModule(it.value) },
            getDifferential = { this.getDifferential(it.value) }
        )

    private val moduleCache: MutableMap<Int, FreeModule<BA, FreeResolBasisName, S, V, M>> = mutableMapOf()
    private val differentialCache: MutableMap<
        Int,
        FreeModuleMap<BA, FreeResolBasisName, FreeResolBasisName, S, V, M>
        > = mutableMapOf()
    private var _augmentation: ModuleMapFromFreeModule<BA, FreeResolBasisName, B, S, V, M>? = null
    val augmentation: ModuleMapFromFreeModule<BA, FreeResolBasisName, B, S, V, M>
        get() {
            if (this._augmentation == null) {
                this.compute(0)
            }
            return this._augmentation ?: throw Exception("This can't happen!")
        }
    val minDegreeComputedAlready: Int
        get() {
            val minKey: Int? = moduleCache.keys.minOrNull()
            return if (minKey == null) {
                1
            } else {
                min(minKey, 1)
            }
        }

    private val zeroFreeModule = FreeModule(this.coeffAlgebra, emptyList<FreeResolBasisName>())
    private val zeroFreeModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
        source = zeroFreeModule,
        target = zeroFreeModule,
        values = emptyList(),
    )

    private fun compute(degree: Int) {
        check(!this.moduleCache.containsKey(degree)) {
            "Attempted to compute moduleCache[$degree] twice, which should not happen!"
        }
        check(!this.differentialCache.containsKey(degree)) {
            "Attempted to compute differentialCache[$degree] twice, which should not happen!"
        }
        when {
            (degree > 0) -> {
                this.moduleCache[degree] = this.zeroFreeModule
                this.differentialCache[degree] = this.zeroFreeModuleMap
            }
            (degree == 0) -> {
                // surjection to this.module
                val differentialTargets = this.module.findSmallGenerator(finder = this.finder)
                val generatingBasisNames = differentialTargets.indices.map {
                    FreeResolBasisName(degree = degree, index = it)
                }
                val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
                this._augmentation = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
                    source = freeModule,
                    target = this.module,
                    values = differentialTargets,
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
                val diffOrAug = when {
                    (degree == -1) -> this.augmentation
                    (degree < -1) -> this.getDifferential(degree + 1)
                    else -> throw Exception("This can't happen!")
                }
                val kernel = diffOrAug.kernel()
                val incl = kernel.inclusion
                val differentialTargets = kernel.findSmallGenerator(finder = this.finder).map { incl(it) }
                val differential = this.hitVectors(degree, this.getModule(degree + 1), differentialTargets)
                this.moduleCache[degree] = differential.source
                this.differentialCache[degree] = differential
            }
        }
    }

    private fun hitVectors(
        degree: Int,
        targetModule: FreeModule<BA, FreeResolBasisName, S, V, M>,
        targetVectors: List<Vector<FreeModuleBasisName<BA, FreeResolBasisName>, S, V>>,
    ): FreeModuleMap<BA, FreeResolBasisName, FreeResolBasisName, S, V, M> {
        val generatingBasisNames = targetVectors.indices.map {
            FreeResolBasisName(degree = degree, index = it)
        }
        val freeModule = FreeModule(this.coeffAlgebra, generatingBasisNames)
        return FreeModuleMap.fromValuesOnGeneratingBasis(
            source = freeModule,
            target = targetModule,
            values = targetVectors,
        )
    }

    private fun getModule(degree: Int): FreeModule<BA, FreeResolBasisName, S, V, M> {
        this.moduleCache[degree]?.let { return it }
        this.compute(degree)
        return this.moduleCache[degree] ?: throw Exception("This can't happen!")
    }

    private fun getDifferential(degree: Int): FreeModuleMap<BA, FreeResolBasisName, FreeResolBasisName, S, V, M> {
        this.differentialCache[degree]?.let { return it }
        this.compute(degree)
        return this.differentialCache[degree] ?: throw Exception("This can't happen!")
    }
}

public class FreeResol<BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val freeResolFactory: FreeResolFactory<BA, B, S, V, M>
) : FreeComplex<IntDegree, BA, FreeResolBasisName, S, V, M> by freeResolFactory.freeComplex {
    public constructor(
        coeffAlgebra: Algebra<BA, S, V, M>,
        module: Module<BA, B, S, V, M>,
        finder: SmallGeneratorFinder<BA, S, V, M, Algebra<BA, S, V, M>> =
            SmallGeneratorFinder.getDefaultFor(coeffAlgebra),
    ) : this(
        FreeResolFactory(
            coeffAlgebra,
            module,
            finder,
        )
    )

    public val module: Module<BA, B, S, V, M>
        get() = this.freeResolFactory.module

    public val minDegreeComputedAlready: Int
        get() = freeResolFactory.minDegreeComputedAlready

    public val augmentation: ModuleMapFromFreeModule<BA, FreeResolBasisName, B, S, V, M>
        get() = freeResolFactory.augmentation

    public companion object {
        public operator fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            coeffAlgebra: MonoidRing<E, S, V, M>,
            finder: SmallGeneratorFinder<E, S, V, M, Algebra<E, S, V, M>> =
                SmallGeneratorFinder.getDefaultFor(coeffAlgebra),
        ): FreeResol<E, StringBasisName, S, V, M> {
            val module = TrivialModule.baseField(coeffAlgebra)
            return FreeResol(coeffAlgebra, module, finder)
        }
    }
}
