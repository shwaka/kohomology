package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.resol.module.FreeModuleBasisName
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMap
import com.github.shwaka.kohomology.resol.module.ModuleMapAlongAlgebraMapFromFreeModule
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMapAlongAlgebraMapFromFreeComplex<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ChainMapAlongAlgebraMap<D, BAS, BAT, FreeModuleBasisName<BAS, BVS>, BT, S, V, M> {

    override val source: FreeComplex<D, BAS, BVS, S, V, M>

    public companion object {
        public operator fun <
            D : Degree,
            BAS : BasisName,
            BAT : BasisName,
            BVS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: FreeComplex<D, BAS, BVS, S, V, M>,
            target: Complex<D, BAT, BT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, BT, S, V, M>,
        ): ChainMapAlongAlgebraMapFromFreeComplex<D, BAS, BAT, BVS, BT, S, V, M> {
            return ChainMapAlongAlgebraMapFromFreeComplexImpl(source, target, algebraMap, name, getModuleMap)
        }
    }
}

private class ChainMapAlongAlgebraMapFromFreeComplexImpl<
    D : Degree,
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeComplex<D, BAS, BVS, S, V, M>,
    override val target: Complex<D, BAT, BT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, BT, S, V, M>,
) : ChainMapAlongAlgebraMapFromFreeComplex<D, BAS, BAT, BVS, BT, S, V, M> {
    private val _getModuleMap: (degree: D) -> ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, BT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): ModuleMapAlongAlgebraMap<BAS, BAT, FreeModuleBasisName<BAS, BVS>, BT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val chainMap: ChainMap<D, BAS, FreeModuleBasisName<BAS, BVS>, BT, S, V, M> by lazy {
        val restrictedTarget = RestrictedComplex(this.target, this.algebraMap)
        ChainMap(
            source = this.source,
            target = restrictedTarget,
            name = this.name,
            getModuleMap = { this.getModuleMap(it).moduleMap },
        )
    }
}
