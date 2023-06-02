package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubBasis

public interface SubDGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorSpace<D, SubBasis<B, S, V>, S, V, M>,
    SubGVectorSpace<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            subGVectorSpace: SubGVectorSpace<D, B, S, V, M>,
            differential: GLinearMap<D, SubBasis<B, S, V>, SubBasis<B, S, V>, S, V, M>,
        ): SubDGVectorSpace<D, B, S, V, M> {
            return SubDGVectorSpaceImpl(subGVectorSpace, differential)
        }
    }
}

private class SubDGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val underlyingGVectorSpace: SubGVectorSpace<D, B, S, V, M>,
    override val differential: GLinearMap<D, SubBasis<B, S, V>, SubBasis<B, S, V>, S, V, M>,
) : SubDGVectorSpace<D, B, S, V, M>,
    SubGVectorSpace<D, B, S, V, M> by underlyingGVectorSpace {
    override val context: DGVectorContext<D, SubBasis<B, S, V>, S, V, M> = DGVectorContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGVectorSpace.matrixSpace
    override val cohomology: SubQuotGVectorSpace<D, SubBasis<B, S, V>, S, V, M> by lazy {
        val dgVectorSpace = DGVectorSpace(this.underlyingGVectorSpace, this.differential)
        dgVectorSpace.cohomology
    }
}
