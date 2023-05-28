package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
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

public interface SubQuotGMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagma<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
            multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
        ): SubQuotGMagma<D, B, S, V, M> {
            return SubQuotGMagmaImpl(matrixSpace, subQuotGVectorSpace, multiplication)
        }
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
            getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ): SubQuotGMagma<D, B, S, V, M> {
            val subQuotGVectorSpace = SubQuotGVectorSpace(
                matrixSpace,
                totalGVectorSpace,
                name,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
                getVectorSpace,
            )
            val multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M> by lazy {
                val bilinearMapName = "Multiplication($name)"
                GBilinearMap(
                    subQuotGVectorSpace,
                    subQuotGVectorSpace,
                    subQuotGVectorSpace,
                    0,
                    bilinearMapName,
                ) { p, q -> getMultiplication(p, q) }
            }
            return SubQuotGMagmaImpl(
                matrixSpace,
                subQuotGVectorSpace,
                multiplication
            )
        }
    }
}

private class SubQuotGMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
) : SubQuotGMagma<D, B, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> by subQuotGVectorSpace {
    override val context: GMagmaContext<D, SubQuotBasis<B, S, V>, S, V, M> = GMagmaContextImpl(this)
}
