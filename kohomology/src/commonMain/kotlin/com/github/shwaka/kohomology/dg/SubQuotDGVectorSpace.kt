package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface SubQuotDGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorSpace<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
            differential: GLinearMap<D, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
        ): SubQuotDGVectorSpace<D, B, S, V, M> {
            return SubQuotDGVectorSpaceImpl(subQuotGVectorSpace, differential)
        }
    }
}

private class SubQuotDGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val underlyingGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    override val differential: GLinearMap<D, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
) : SubQuotDGVectorSpace<D, B, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> by underlyingGVectorSpace {
    override val context: DGVectorContext<D, SubQuotBasis<B, S, V>, S, V, M> = DGVectorContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGVectorSpace.matrixSpace
    override val cohomology: SubQuotGVectorSpace<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        val dgVectorSpace = DGVectorSpace(this.underlyingGVectorSpace, this.differential)
        dgVectorSpace.cohomology
    }
}
