package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.StringBasisName

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

        public fun <
            E : FiniteMonoidElement,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > baseField(
            coeffAlgebra: MonoidRing<E, S, V, M>,
        ): TrivialModuleMap<E, StringBasisName, StringBasisName, S, V, M> {
            val module = TrivialModule.baseField(coeffAlgebra)
            val underlyingMap = module.underlyingVectorSpace.getIdentity(coeffAlgebra.matrixSpace)
            return TrivialModuleMap(
                underlyingMap,
                source = module,
                target = module,
            )
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
