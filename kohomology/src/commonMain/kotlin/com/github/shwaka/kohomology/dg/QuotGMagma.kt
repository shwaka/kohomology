package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.QuotBasis

public interface QuotGMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagma<D, QuotBasis<B, S, V>, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
            multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M>,
        ): QuotGMagma<D, B, S, V, M> {
            return QuotGMagmaImpl(matrixSpace, quotGVectorSpace, multiplication)
        }
    }
}

private class QuotGMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M>,
) : QuotGMagma<D, B, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> by quotGVectorSpace {
    override val context: GMagmaContext<D, QuotBasis<B, S, V>, S, V, M> = GMagmaContext(this)
}
