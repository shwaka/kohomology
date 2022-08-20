package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
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

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)?,
            getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
        ): SubQuotGVectorSpace<D, B, S, V, M> {
            return SubQuotGVectorSpaceImpl(
                numVectorSpace,
                degreeGroup,
                name,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
                getVectorSpace
            )
        }
    }
}

private class SubQuotGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val numVectorSpace: NumVectorSpace<S, V>,
    override val degreeGroup: DegreeGroup<D>,
    override val name: String,
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S>,
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)?,
    private val getVectorSpace: (D) -> SubQuotVectorSpace<B, S, V, M>,
) : SubQuotGVectorSpace<D, B, S, V, M> {
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
