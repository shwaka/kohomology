package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.resol.complex.FreeChainMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.FreeModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName

private class FreeResolMapFactory<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    AlgS : Algebra<BAS, S, V, M>,
    AlgT : Algebra<BAT, S, V, M>,
    >(
    val source: FreeResol<BAS, BS, S, V, M, AlgS>,
    val target: FreeResol<BAT, BT, S, V, M, AlgT>,
    val moduleMap: ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
) {
    init {
        require(source.module == moduleMap.source) {
            "Invalid arguments for FreeResolMap: source.module and moduleMap.source are different"
        }
        require(target.module == moduleMap.target) {
            "Invalid arguments for FreeResolMap: target.module and moduleMap.target are different"
        }
    }

    val freeChainMap: FreeChainMapAlongAlgebraMap<
        IntDegree,
        BAS,
        BAT,
        FreeResolBasisName,
        FreeResolBasisName,
        S,
        V,
        M,
        > = FreeChainMapAlongAlgebraMap(
        source = source,
        target = target,
        algebraMap = moduleMap.algebraMap,
        name = "$moduleMap^*",
        getModuleMap = { this.getModuleMap(it.value) }
    )

    private val algebraMap: AlgebraMap<BAS, BAT, S, V, M> = moduleMap.algebraMap

    private val moduleMapCache: MutableMap<Int, FreeModuleMapAlongAlgebraMap<BAS, BAT, FreeResolBasisName, FreeResolBasisName, S, V, M>> =
        mutableMapOf()

    private fun compute(degree: Int) {
        check(!this.moduleMapCache.containsKey(degree)) {
            "Attempted to compute moduleMapCache[$degree] twice, which should not happen!"
        }
        when {
            (degree > 0) -> {
                val moduleMap = FreeModuleMapAlongAlgebraMap.fromValuesOnGeneratingBasis(
                    source = this.source.getModule(degree),
                    target = this.target.getModule(degree),
                    algebraMap = this.algebraMap,
                    values = emptyList(),
                )
                this.moduleMapCache[degree] = moduleMap
            }
            (degree == 0) -> {
                val composed = this.moduleMap * this.source.augmentation
                val lift = composed.liftAlong(this.target.augmentation)
                this.moduleMapCache[0] = lift
            }
            (degree < 0) -> {
                val composed = this.getModuleMap(degree + 1) * this.source.getDifferential(degree)
                val lift = composed.liftAlong(this.target.getDifferential(degree))
                this.moduleMapCache[degree] = lift
            }
        }
    }

    private fun getModuleMap(degree: Int): FreeModuleMapAlongAlgebraMap<BAS, BAT, FreeResolBasisName, FreeResolBasisName, S, V, M> {
        this.moduleMapCache[degree]?.let { return it }
        this.compute(degree)
        return this.moduleMapCache[degree] ?: throw Exception("This can't happen!")
    }
}

public class FreeResolMap<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    AlgS : Algebra<BAS, S, V, M>,
    AlgT : Algebra<BAT, S, V, M>,
    > private constructor(
    private val freeResolMapFactory: FreeResolMapFactory<BAS, BAT, BS, BT, S, V, M, AlgS, AlgT>
) : FreeChainMapAlongAlgebraMap<
    IntDegree,
    BAS,
    BAT,
    FreeResolBasisName,
    FreeResolBasisName,
    S,
    V,
    M,
    > by freeResolMapFactory.freeChainMap {

    public constructor(
        source: FreeResol<BAS, BS, S, V, M, AlgS>,
        target: FreeResol<BAT, BT, S, V, M, AlgT>,
        moduleMap: ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
    ) : this(
        FreeResolMapFactory(source, target, moduleMap)
    )

    override val source: FreeResol<BAS, BS, S, V, M, AlgS> = freeResolMapFactory.source
    override val target: FreeResol<BAT, BT, S, V, M, AlgT> = freeResolMapFactory.target
}
