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
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
            differentialOnTotalGVectorSpace: GLinearMap<D, B, B, S, V, M>,
        ): QuotDGVectorSpace<D, B, S, V, M> {
            require(differentialOnTotalGVectorSpace.source == quotGVectorSpace.totalGVectorSpace)
            require(differentialOnTotalGVectorSpace.target == quotGVectorSpace.totalGVectorSpace)
            val differential = differentialOnTotalGVectorSpace.induce(quotGVectorSpace, quotGVectorSpace)
            return QuotDGVectorSpaceImpl(quotGVectorSpace, differential)
        }
    }
}

private class QuotDGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val underlyingGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
    override val differential: GLinearMap<D, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M>,
) : QuotDGVectorSpace<D, B, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> by underlyingGVectorSpace {
    override val context: DGVectorContext<D, QuotBasis<B, S, V>, S, V, M> = DGVectorContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGVectorSpace.matrixSpace
    override val cohomology: SubQuotGVectorSpace<D, QuotBasis<B, S, V>, S, V, M> by lazy {
        val dgVectorSpace = DGVectorSpace(this.underlyingGVectorSpace, this.differential)
        dgVectorSpace.cohomology
    }
}
