package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.OppositeFiniteMonoid
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface OpMonoidRing<
    E : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : OpAlgebra<E, S, V, M>, MonoidRing<E, S, V, M> {

    public companion object {
        public operator fun <
            E : FiniteMonoidElement,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            originalAlgebra: MonoidRing<E, S, V, M>,
        ): OpMonoidRing<E, S, V, M> {
            return OpMonoidRingImpl(originalAlgebra)
        }
    }
}

private class OpMonoidRingImpl<
    E : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val originalAlgebra: MonoidRing<E, S, V, M>,
) : OpMonoidRing<E, S, V, M>,
    VectorSpace<E, S, V> by originalAlgebra {

    override val context: AlgebraContext<E, S, V, M> = AlgebraContext(this)
    override val matrixSpace: MatrixSpace<S, V, M> = originalAlgebra.matrixSpace
    override val multiplication: BilinearMap<E, E, E, S, V, M> by lazy {
        this.originalAlgebra.multiplication.transpose()
    }
    override val unit: Vector<E, S, V> = originalAlgebra.unit
    override val isCommutative: Boolean = originalAlgebra.isCommutative

    override val monoid: FiniteMonoid<E> = OppositeFiniteMonoid(originalAlgebra.monoid)
}
