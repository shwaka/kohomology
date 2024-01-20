package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface ModuleMapAlongAlgebraMap<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > {

    public val source: Module<BAS, BS, S, V, M>
    public val target: Module<BAT, BT, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>
    public val moduleMap: ModuleMap<BAS, BS, BT, S, V, M>
    public val underlyingLinearMap: LinearMap<BS, BT, S, V, M>
        get() = moduleMap.underlyingLinearMap

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        return this.moduleMap(vector)
    }

    public fun kernel(): SubModule<BAS, BS, S, V, M> {
        return this.moduleMap.kernel()
    }

    public operator fun <BVS0 : BasisName> times(
        other: ModuleMapFromFreeModule<BAS, BVS0, BS, S, V, M>
    ): ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS0, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot compose $this and $other: the source of $this and the target of $other are different"
        }
        val values = other.source.getGeneratingBasis().map { vector ->
            this(other(vector))
        }
        return ModuleMapAlongAlgebraMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = other.source,
            target = this.target,
            algebraMap = this.algebraMap,
            values = values,
        )
    }

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
            source: Module<BAS, BS, S, V, M>,
            target: Module<BAT, BT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>
        ): ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M> {
            return ModuleMapAlongAlgebraMapImpl(source, target, algebraMap, underlyingLinearMap)
        }
    }
}

private class ModuleMapAlongAlgebraMapImpl<
    BAS : BasisName,
    BAT : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: Module<BAS, BS, S, V, M>,
    override val target: Module<BAT, BT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    underlyingLinearMap: LinearMap<BS, BT, S, V, M>
) : ModuleMapAlongAlgebraMap<BAS, BAT, BS, BT, S, V, M> {
    override val moduleMap: ModuleMap<BAS, BS, BT, S, V, M> =
        ModuleMap(
            source = source,
            target = RestrictedModule(target, algebraMap),
            underlyingLinearMap = underlyingLinearMap,
        )
}
