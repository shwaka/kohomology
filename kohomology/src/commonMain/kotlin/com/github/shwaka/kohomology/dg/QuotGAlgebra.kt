package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.QuotBasis

public interface QuotGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GAlgebra<D, QuotBasis<B, S, V>, S, V, M>,
    QuotGMagma<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
            multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M>,
            unit: GVector<D, QuotBasis<B, S, V>, S, V>,
            isCommutative: Boolean = false,
        ): QuotGAlgebra<D, B, S, V, M> {
            return QuotGAlgebraImpl(matrixSpace, quotGVectorSpace, multiplication, unit, isCommutative)
        }
    }
}

private class QuotGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M>,
    override val unit: GVector<D, QuotBasis<B, S, V>, S, V>,
    override val isCommutative: Boolean,
) : QuotGAlgebra<D, B, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> by quotGVectorSpace {
    override val context: GAlgebraContext<D, QuotBasis<B, S, V>, S, V, M> = GAlgebraContextImpl(this)

    override fun getIdentity(): GAlgebraMap<D, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M> {
        // If this method is implemented in the interface QuotGAlgebra,
        // a type error is thrown.
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    override val underlyingGAlgebra: GAlgebra<D, QuotBasis<B, S, V>, S, V, M> = this
}
