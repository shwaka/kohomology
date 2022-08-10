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
import com.github.shwaka.kohomology.vectsp.Vector

public interface SubQuotGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GAlgebra<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
            getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
            unitVector: Vector<SubQuotBasis<B, S, V>, S, V>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ): SubQuotGAlgebra<D, B, S, V, M> {
            return SubQuotGAlgebraImpl(
                matrixSpace,
                degreeGroup,
                name,
                getVectorSpace,
                getMultiplication,
                unitVector,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
            )
        }
    }
}

private class SubQuotGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    degreeGroup: DegreeGroup<D>,
    name: String,
    getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
    getMultiplication: (D, D) -> BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M>,
    unitVector: Vector<SubQuotBasis<B, S, V>, S, V>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
    listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
) : SubQuotGAlgebra<D, B, S, V, M>,
    SubQuotGMagma<D, B, S, V, M> by SubQuotGMagma(matrixSpace, degreeGroup, name, getVectorSpace, getMultiplication, getInternalPrintConfig, listDegreesForAugmentedDegree) {
    override val context: GAlgebraContext<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        GAlgebraContextImpl(this)
    }

    override val unit: GVector<D, SubQuotBasis<B, S, V>, S, V> by lazy {
        this.fromVector(unitVector, 0)
    }

    override fun getIdentity(): GAlgebraMap<D, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        // If this method is implemented in the interface GAlgebra,
        // a type error is thrown.
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }
}
