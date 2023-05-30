package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace

public interface SubQuotGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, SubQuotBasis<B, S, V>, S, V> {
    override fun get(degree: D): SubQuotVectorSpace<B, S, V, M>
    override fun get(degree: Int): SubQuotVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    public val matrixSpace: MatrixSpace<S, V, M>
    public val totalGVectorSpace: GVectorSpace<D, B, S, V>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
        ): SubQuotGVectorSpace<D, B, S, V, M> {
            return SubQuotGVectorSpaceImpl(
                matrixSpace,
                totalGVectorSpace,
                name,
                getVectorSpace
            )
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            subspaceGenerator: SubGVectorSpace<D, B, S, V, M>,
            quotientGenerator: SubGVectorSpace<D, B, S, V, M>,
        ): SubQuotGVectorSpace<D, B, S, V, M> {
            return SubQuotGVectorSpace(
                matrixSpace,
                totalGVectorSpace,
                name = name,
            ) { degree ->
                SubQuotVectorSpace(
                    matrixSpace,
                    totalGVectorSpace[degree],
                    subspaceGenerator = subspaceGenerator[degree],
                    quotientGenerator = quotientGenerator[degree],
                )
            }
        }
    }
}

private class SubQuotGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val totalGVectorSpace: GVectorSpace<D, B, S, V>,
    override val name: String,
    private val getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
) : SubQuotGVectorSpace<D, B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalGVectorSpace.numVectorSpace
    override val degreeGroup: DegreeGroup<D> = totalGVectorSpace.degreeGroup
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> =
        SubQuotBasis.convertGetInternalPrintConfig(totalGVectorSpace.getInternalPrintConfig)
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)? =
        totalGVectorSpace.listDegreesForAugmentedDegree
    override val boundedness: Boundedness = totalGVectorSpace.boundedness
    private val cache: MutableMap<D, SubQuotVectorSpace<B, S, V, M>> = mutableMapOf()
    override val context: GVectorContext<D, SubQuotBasis<B, S, V>, S, V> = GVectorContextImpl(this)
    override val underlyingGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M> = this

    override fun get(degree: D): SubQuotVectorSpace<B, S, V, M> {
        return this.cache.getOrPut(degree) {
            this.getVectorSpace(degree)
        }
    }
    override fun get(degree: Int): SubQuotVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    override fun toString(): String {
        return this.name
    }
}
