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
import com.github.shwaka.kohomology.vectsp.Vector

public interface SubQuotGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GAlgebra<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
            multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
            unit: GVector<D, SubQuotBasis<B, S, V>, S, V>,
            isCommutative: Boolean = false,
        ): SubQuotGAlgebra<D, B, S, V, M> {
            return SubQuotGAlgebraImpl(matrixSpace, subQuotGVectorSpace, multiplication, unit, isCommutative)
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
            getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
            unitVector: Vector<SubQuotBasis<B, S, V>, S, V>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
            isCommutative: Boolean = false,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ): SubQuotGAlgebra<D, B, S, V, M> {
            val subQuotGMagma = SubQuotGMagma(
                matrixSpace,
                totalGVectorSpace,
                name,
                getVectorSpace,
                getMultiplication,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
            )
            val unit = subQuotGMagma.fromVector(unitVector, 0)
            return SubQuotGAlgebraImpl(matrixSpace, subQuotGMagma, subQuotGMagma.multiplication, unit, isCommutative)
        }
    }
}

private class SubQuotGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    override val multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
    override val unit: GVector<D, SubQuotBasis<B, S, V>, S, V>,
    override val isCommutative: Boolean,
) : SubQuotGAlgebra<D, B, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> by subQuotGVectorSpace {
    override val context: GAlgebraContext<D, SubQuotBasis<B, S, V>, S, V, M> = GAlgebraContextImpl(this)

    override fun getIdentity(): GAlgebraMap<D, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        // If this method is implemented in the interface SubQuotGAlgebra,
        // a type error is thrown.
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    override val underlyingGAlgebra: GAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> = this
}
