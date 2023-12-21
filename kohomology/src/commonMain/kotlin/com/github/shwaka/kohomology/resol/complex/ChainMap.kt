package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMap<
    D : Degree,
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>> {

    public val name: String
    public val source: Complex<D, BA, BS, S, V, M>
    public val target: Complex<D, BA, BT, S, V, M>

    public fun getModuleMap(degree: D): ModuleMap<BA, BS, BT, S, V, M>

    public val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M>

    public operator fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        return this.underlyingDGLinearMap(gVector)
    }

    public companion object {
        public operator fun <
            D : Degree,
            BA : BasisName,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: Complex<D, BA, BS, S, V, M>,
            target: Complex<D, BA, BT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M>,
        ): ChainMap<D, BA, BS, BT, S, V, M> {
            return ChainMapImpl(source, target, name, getModuleMap)
        }
    }
}

private class ChainMapImpl<
    D : Degree,
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: Complex<D, BA, BS, S, V, M>,
    override val target: Complex<D, BA, BT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M>,
) : ChainMap<D, BA, BS, BT, S, V, M> {
    private val _getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): ModuleMap<BA, BS, BT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M> by lazy {
        DGLinearMap(
            source = this.source.underlyingDGVectorSpace,
            target = this.target.underlyingDGVectorSpace,
            degree = this.source.degreeGroup.zero,
            matrixSpace = this.source.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getModuleMap(degree).underlyingLinearMap
        }
    }
}
