package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMapAlongAlgebraMap<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > {

    public val name: String
    public val source: Complex<D, BAS, BS, S, V, M>
    public val target: Complex<D, BAT, BT, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>
    public val chainMap: ChainMap<D, BAS, BS, BT, S, V, M>

    public fun getModuleMap(degree: D): ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>

    public val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M>
        get() = this.chainMap.underlyingDGLinearMap

    public operator fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        return this.underlyingDGLinearMap(gVector)
    }

    public fun checkChainMapAxioms(degreeList: List<D>) {
        this.chainMap.checkChainMapAxioms(degreeList)
    }

    public fun checkChainMapAxioms(minDegree: Int, maxDegree: Int) {
        this.chainMap.checkChainMapAxioms(minDegree, maxDegree)
    }

    public companion object {
        public operator fun <
            D : Degree,
            BAS : BasisName,
            BAT : BasisName,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: Complex<D, BAS, BS, S, V, M>,
            target: Complex<D, BAT, BT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
        ): ChainMapAlongAlgebraMap<D, BAS, BAT, BS, BT, S, V, M> {
            return ChainMapAlongAlgebraMapImpl(source, target, algebraMap, name, getModuleMap)
        }
    }
}

private class ChainMapAlongAlgebraMapImpl<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: Complex<D, BAS, BS, S, V, M>,
    override val target: Complex<D, BAT, BT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M>,
) : ChainMapAlongAlgebraMap<D, BAS, BAT, BS, BT, S, V, M> {
    private val _getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val chainMap: ChainMap<D, BAS, BS, BT, S, V, M> by lazy {
        val restrictedTarget = RestrictedComplex(this.target, this.algebraMap)
        ChainMap(
            source = this.source,
            target = restrictedTarget,
            name = this.name,
            getModuleMap = { this.getModuleMap(it).moduleMap },
        )
    }
}
