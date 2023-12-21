package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.resol.module.ModuleMapFromFreeModule
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMapFromFreeComplex<
    D : Degree,
    BA : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMap<D, BA, FreeModuleBasisName<BA, BVS>, BT, S, V, M> {

    override val source: FreeComplex<D, BA, BVS, S, V, M>

    public companion object {
        public operator fun <
            D : Degree,
            BA : BasisName,
            BVS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: FreeComplex<D, BA, BVS, S, V, M>,
            target: Complex<D, BA, BT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> ModuleMapFromFreeModule<BA, BVS, BT, S, V, M>,
        ): ChainMapFromFreeComplex<D, BA, BVS, BT, S, V, M> {
            return ChainMapFromFreeComplexImpl(source, target, name, getModuleMap)
        }
    }
}

private class ChainMapFromFreeComplexImpl<
    D : Degree,
    BA : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeComplex<D, BA, BVS, S, V, M>,
    override val target: Complex<D, BA, BT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> ModuleMapFromFreeModule<BA, BVS, BT, S, V, M>,
) : ChainMapFromFreeComplex<D, BA, BVS, BT, S, V, M> {
    private val _getModuleMap: (degree: D) -> ModuleMapFromFreeModule<BA, BVS, BT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): ModuleMap<BA, FreeModuleBasisName<BA, BVS>, BT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val underlyingDGLinearMap: DGLinearMap<D, FreeModuleBasisName<BA, BVS>, BT, S, V, M> by lazy {
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
