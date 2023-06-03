package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.QuotBasis

public interface QuotDGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorSpace<D, QuotBasis<B, S, V>, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> {
    override val totalGVectorSpace: DGVectorSpace<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            totalDGVectorSpace: DGVectorSpace<D, B, S, V, M>,
            quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
        ): QuotDGVectorSpace<D, B, S, V, M> {
            return QuotDGVectorSpaceImpl(totalDGVectorSpace, quotGVectorSpace)
        }
    }
}

private class QuotDGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    totalDGVectorSpace: DGVectorSpace<D, B, S, V, M>,
    override val underlyingGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
) : QuotDGVectorSpace<D, B, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> by underlyingGVectorSpace {
    override val totalGVectorSpace: DGVectorSpace<D, B, S, V, M> = totalDGVectorSpace
    override val differential: GLinearMap<D, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M> =
        totalDGVectorSpace.differential.induce(underlyingGVectorSpace, underlyingGVectorSpace)
    override val context: DGVectorContext<D, QuotBasis<B, S, V>, S, V, M> = DGVectorContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGVectorSpace.matrixSpace
    override val cohomology: SubQuotGVectorSpace<D, QuotBasis<B, S, V>, S, V, M> by lazy {
        val dgVectorSpace = DGVectorSpace(this.underlyingGVectorSpace, this.differential)
        dgVectorSpace.cohomology
    }
}
