package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
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
