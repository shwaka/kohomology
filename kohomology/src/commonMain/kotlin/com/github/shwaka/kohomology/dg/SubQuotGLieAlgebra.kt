package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace

public interface SubQuotGLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GLieAlgebra<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
            getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { InternalPrintConfig.default(it) },
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ) : SubQuotGLieAlgebra<D, B, S, V, M> {
            return SubQuotGLieAlgebraImpl(
                matrixSpace,
                degreeGroup,
                name,
                getVectorSpace,
                getMultiplication,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
            )
        }
    }
}

private class SubQuotGLieAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    degreeGroup: DegreeGroup<D>,
    name: String,
    getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
    getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { InternalPrintConfig.default(it) },
    listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
) : SubQuotGLieAlgebra<D, B, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> by SubQuotGMagma(matrixSpace, degreeGroup, name, getVectorSpace, getMultiplication, getInternalPrintConfig, listDegreesForAugmentedDegree) {
    override val context: GLieAlgebraContext<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        GLieAlgebraContextImpl(this)
    }

}
