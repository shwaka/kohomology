package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface FreeChainMap<
    D : Degree,
    BA : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMapFromFreeComplex<D, BA, BVS, FreeModuleBasisName<BA, BVT>, S, V, M> {

    override val target: FreeComplex<D, BA, BVT, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
        get() = source.matrixSpace
    public val tensorWithBaseField: DGLinearMap<D, BVS, BVT, S, V, M>

    override fun getModuleMap(degree: D): FreeModuleMap<BA, BVS, BVT, S, V, M>

    public companion object {
        public operator fun <
            D : Degree,
            BA : BasisName,
            BVS : BasisName,
            BVT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: FreeComplex<D, BA, BVS, S, V, M>,
            target: FreeComplex<D, BA, BVT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> FreeModuleMap<BA, BVS, BVT, S, V, M>,
        ): FreeChainMap<D, BA, BVS, BVT, S, V, M> {
            return FreeChainMapImpl(source, target, name, getModuleMap)
        }
    }
}

private class FreeChainMapImpl<
    D : Degree,
    BA : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeComplex<D, BA, BVS, S, V, M>,
    override val target: FreeComplex<D, BA, BVT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> FreeModuleMap<BA, BVS, BVT, S, V, M>,
) : FreeChainMap<D, BA, BVS, BVT, S, V, M> {
    private val _getModuleMap: (degree: D) -> FreeModuleMap<BA, BVS, BVT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): FreeModuleMap<BA, BVS, BVT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val underlyingDGLinearMap: DGLinearMap<D, FreeModuleBasisName<BA, BVS>, FreeModuleBasisName<BA, BVT>, S, V, M> by lazy {
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

    override val tensorWithBaseField: DGLinearMap<D, BVS, BVT, S, V, M> by lazy {
        DGLinearMap(
            source = this.source.tensorWithBaseField,
            target = this.target.tensorWithBaseField,
            degree = this.source.degreeGroup.zero,
            matrixSpace = this.source.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getModuleMap(degree).tensorWithBaseField
        }
    }
}
