package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface OpAlgebra<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> : Algebra<B, S, V, M> {
    public val originalAlgebra: Algebra<B, S, V, M>

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            originalAlgebra: Algebra<B, S, V, M>,
        ): OpAlgebra<B, S, V, M> {
            return OpAlgebraImpl(originalAlgebra)
        }
    }
}

private class OpAlgebraImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val originalAlgebra: Algebra<B, S, V, M>,
) : OpAlgebra<B, S, V, M>,
    VectorSpace<B, S, V> by originalAlgebra {

    override val context: AlgebraContext<B, S, V, M> = AlgebraContext(this)
    override val matrixSpace: MatrixSpace<S, V, M> = originalAlgebra.matrixSpace
    override val multiplication: BilinearMap<B, B, B, S, V, M> by lazy {
        this.originalAlgebra.multiplication.transpose()
    }
    override val unit: Vector<B, S, V> = originalAlgebra.unit
    override val isCommutative: Boolean = originalAlgebra.isCommutative
}
