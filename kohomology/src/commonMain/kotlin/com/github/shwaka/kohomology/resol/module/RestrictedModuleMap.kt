package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface RestrictedModuleMap<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMap<BAS, BS, BT, S, V, M> {

    override val source: RestrictedModule<BAS, BAT, BS, S, V, M>
    override val target: RestrictedModule<BAS, BAT, BT, S, V, M>
    public val originalModuleMap: ModuleMap<BAT, BS, BT, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>

    public companion object {
        public operator fun <
            BAS : BasisName,
            BAT : BasisName,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: RestrictedModule<BAS, BAT, BS, S, V, M>,
            target: RestrictedModule<BAS, BAT, BT, S, V, M>,
            originalModuleMap: ModuleMap<BAT, BS, BT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
        ): RestrictedModuleMap<BAS, BAT, BS, BT, S, V, M> {
            return RestrictedModuleMapImpl(source, target, originalModuleMap, algebraMap)
        }
    }
}

private class RestrictedModuleMapImpl<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: RestrictedModule<BAS, BAT, BS, S, V, M>,
    override val target: RestrictedModule<BAS, BAT, BT, S, V, M>,
    override val originalModuleMap: ModuleMap<BAT, BS, BT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
) : RestrictedModuleMap<BAS, BAT, BS, BT, S, V, M> {
    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M> = originalModuleMap.underlyingLinearMap
}
