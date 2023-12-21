package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.AlgebraMap
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.FreeModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface FreeChainMapAlongAlgebraMap<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMapAlongAlgebraMapFromFreeComplex<D, BAS, BAT, BVS, FreeModuleBasisName<BAT, BVT>, S, V, M> {

    override val target: FreeComplex<D, BAT, BVT, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
        get() = source.matrixSpace
    public val tensorWithBaseField: DGLinearMap<D, BVS, BVT, S, V, M>

    override fun getModuleMap(degree: D): FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M>

    public companion object {
        public operator fun <
            D : Degree,
            BAS : BasisName,
            BAT : BasisName,
            BVS : BasisName,
            BVT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: FreeComplex<D, BAS, BVS, S, V, M>,
            target: FreeComplex<D, BAT, BVT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M>,
        ): FreeChainMapAlongAlgebraMap<D, BAS, BAT, BVS, BVT, S, V, M> {
            return FreeChainMapAlongAlgebraMapImpl(source, target, algebraMap, name, getModuleMap)
        }
    }
}

private class FreeChainMapAlongAlgebraMapImpl<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeComplex<D, BAS, BVS, S, V, M>,
    override val target: FreeComplex<D, BAT, BVT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M>,
) : FreeChainMapAlongAlgebraMap<D, BAS, BAT, BVS, BVT, S, V, M> {
    private val _getModuleMap: (degree: D) -> FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val chainMap: ChainMap<D, BAS, FreeModuleBasisName<BAS, BVS>, FreeModuleBasisName<BAT, BVT>, S, V, M> by lazy {
        val restrictedTarget = RestrictedComplex(this.target, this.algebraMap)
        ChainMap(
            source = this.source,
            target = restrictedTarget,
            name = this.name,
            getModuleMap = { this.getModuleMap(it).moduleMap },
        )
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
