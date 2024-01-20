package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface ModuleMapAlongAlgebraMapFromFreeModule<
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMapAlongAlgebraMap<BAS, BAT, FreeModuleBasisName<BAS, BVS>, BT, S, V, M> {

    override val source: FreeModule<BAS, BVS, S, V, M>

    public fun <B : BasisName> liftAlong(
        moduleMap: ModuleMap<BAT, B, BT, S, V, M>
    ): ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, B, S, V, M> {
        require(moduleMap.target == this.target) {
            "The target modules of module maps $this and $moduleMap must be same"
        }
        val values: List<Vector<B, S, V>> = this.source.getGeneratingBasis().map { vector ->
            moduleMap.underlyingLinearMap.findPreimage(this(vector))
                ?: throw IllegalArgumentException(
                    "$vector is not contained in the image of the module map $moduleMap"
                )
        }
        return ModuleMapAlongAlgebraMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = this.source,
            target = moduleMap.source,
            algebraMap = this.algebraMap,
            values = values,
        )
    }

    public fun <BV : BasisName> liftAlong(
        moduleMap: ModuleMapFromFreeModule<BAT, BV, BT, S, V, M>
    ): FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BV, S, V, M> {
        require(moduleMap.target == this.target) {
            "The target modules of module maps $this and $moduleMap must be same"
        }
        val values: List<Vector<FreeModuleBasisName<BAT, BV>, S, V>> = this.source.getGeneratingBasis().map { vector ->
            moduleMap.underlyingLinearMap.findPreimage(this(vector))
                ?: throw IllegalArgumentException(
                    "$vector is not contained in the image of the module map $moduleMap"
                )
        }
        return FreeModuleMapAlongAlgebraMap.fromValuesOnGeneratingBasis(
            source = this.source,
            target = moduleMap.source,
            algebraMap = this.algebraMap,
            values = values,
        )
    }

    public companion object {
        public fun <
            BAS : BasisName,
            BAT : BasisName,
            BVS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > fromValuesOnGeneratingBasis(
            source: FreeModule<BAS, BVS, S, V, M>,
            target: Module<BAT, BT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            values: List<Vector<BT, S, V>>,
        ): ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, BT, S, V, M> {
            val restrictedTarget = RestrictedModule(target, algebraMap)
            val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(source, restrictedTarget, values)
            return ModuleMapAlongAlgebraMapFromFreeModuleImpl(
                source = source,
                target = target,
                algebraMap = algebraMap,
                moduleMap = moduleMap,
            )
        }
    }
}

private class ModuleMapAlongAlgebraMapFromFreeModuleImpl<
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeModule<BAS, BVS, S, V, M>,
    override val target: Module<BAT, BT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    override val moduleMap: ModuleMap<BAS, FreeModuleBasisName<BAS, BVS>, BT, S, V, M>,
) : ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, BT, S, V, M> {
    override val underlyingLinearMap: LinearMap<FreeModuleBasisName<BAS, BVS>, BT, S, V, M>
        get() = moduleMap.underlyingLinearMap
}
