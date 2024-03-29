package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface SubQuotGLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GLieAlgebra<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
            multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
        ): SubQuotGLieAlgebra<D, B, S, V, M> {
            return SubQuotGLieAlgebraImpl(matrixSpace, subQuotGVectorSpace, multiplication)
        }
    }
}

private class SubQuotGLieAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
) : SubQuotGLieAlgebra<D, B, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> by subQuotGVectorSpace {

    override val context: GLieAlgebraContext<D, SubQuotBasis<B, S, V>, S, V, M> = GLieAlgebraContext(this)

    override val underlyingGLieAlgebra: GLieAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> = this
}
