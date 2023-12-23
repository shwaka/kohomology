package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface TrivialModuleMap<
    E : FiniteMonoidElement,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMap<E, BS, BT, S, V, M> {
    override val source: TrivialModule<E, BS, S, V, M>
    override val target: TrivialModule<E, BT, S, V, M>

    public companion object {
        public operator fun <
            E : FiniteMonoidElement,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
            source: TrivialModule<E, BS, S, V, M>,
            target: TrivialModule<E, BT, S, V, M>,
        ): TrivialModuleMap<E, BS, BT, S, V, M> {
            return TrivialModuleMapImpl(underlyingLinearMap, source, target)
        }
    }
}

private class TrivialModuleMapImpl<
    E : FiniteMonoidElement,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
    override val source: TrivialModule<E, BS, S, V, M>,
    override val target: TrivialModule<E, BT, S, V, M>,
) : TrivialModuleMap<E, BS, BT, S, V, M>
