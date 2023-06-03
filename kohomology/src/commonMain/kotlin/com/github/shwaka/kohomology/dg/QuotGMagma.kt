package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.QuotBasis
import com.github.shwaka.kohomology.vectsp.QuotVectorSpace

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
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            boundedness: Boundedness = totalGVectorSpace.boundedness,
            getVectorSpace: (D) -> QuotVectorSpace<B, S, V, M>,
            getMultiplication: (D, D) -> BilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, S, V, M>,
        ): QuotGMagma<D, B, S, V, M> {
            val quotGVectorSpace = QuotGVectorSpace(
                matrixSpace,
                totalGVectorSpace,
                name,
                boundedness,
                getVectorSpace,
            )
            val multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M> by lazy {
                val bilinearMapName = "Multiplication($name)"
                GBilinearMap(
                    matrixSpace,
                    quotGVectorSpace,
                    quotGVectorSpace,
                    quotGVectorSpace,
                    0,
                    bilinearMapName,
                ) { p, q -> getMultiplication(p, q) }
            }
            return QuotGMagmaImpl(
                matrixSpace,
                quotGVectorSpace,
                multiplication
            )
        }
    }
}

private class QuotGMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    quotGVectorSpace: QuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<QuotBasis<B, S, V>, QuotBasis<B, S, V>, QuotBasis<B, S, V>, D, S, V, M>,
) : QuotGMagma<D, B, S, V, M>,
    QuotGVectorSpace<D, B, S, V, M> by quotGVectorSpace {
    override val context: GMagmaContext<D, QuotBasis<B, S, V>, S, V, M> = GMagmaContextImpl(this)
}
