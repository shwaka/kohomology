package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public interface Ideal<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val totalAlgebra: Algebra<B, S, V, M>
    public val underlyingVectorSpace: SubVectorSpace<B, S, V, M>

    public fun subspaceContains(vector: Vector<B, S, V>): Boolean

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            totalAlgebra: Algebra<B, S, V, M>,
            underlyingVectorSpace: SubVectorSpace<B, S, V, M>,
        ): Ideal<B, S, V, M> {
            return IdealImpl(totalAlgebra, underlyingVectorSpace)
        }
    }
}

private class IdealImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val totalAlgebra: Algebra<B, S, V, M>,
    override val underlyingVectorSpace: SubVectorSpace<B, S, V, M>,
) : Ideal<B, S, V, M> {
    override fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.underlyingVectorSpace.subspaceContains(vector)
    }
}
