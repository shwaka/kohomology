package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface Augmentation<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : AlgebraMap<B, TrivialAlgebraBasisName, S, V, M> {

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: Algebra<B, S, V, M>,
            target: Algebra<TrivialAlgebraBasisName, S, V, M>,
            underlyingLinearMap: LinearMap<B, TrivialAlgebraBasisName, S, V, M>,
        ): Augmentation<B, S, V, M> {
            return AugmentationImpl(source, target, underlyingLinearMap)
        }
    }
}

private class AugmentationImpl<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    source: Algebra<B, S, V, M>,
    target: Algebra<TrivialAlgebraBasisName, S, V, M>,
    underlyingLinearMap: LinearMap<B, TrivialAlgebraBasisName, S, V, M>,
) : Augmentation<B, S, V, M>,
    AlgebraMap<B, TrivialAlgebraBasisName, S, V, M> by AlgebraMap(source, target, underlyingLinearMap)
