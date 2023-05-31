package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
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
import com.github.shwaka.kohomology.vectsp.SubBasis
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.asSubVectorSpace

public interface SubGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, SubBasis<B, S, V>, S, V> {
    override fun get(degree: D): SubVectorSpace<B, S, V, M>
    override fun get(degree: Int): SubVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    public val matrixSpace: MatrixSpace<S, V, M>
    public val totalGVectorSpace: GVectorSpace<D, B, S, V>
    public val inclusion: GLinearMap<D, SubBasis<B, S, V>, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            boundedness: Boundedness = totalGVectorSpace.boundedness,
            getVectorSpace: (D) -> SubVectorSpace<B, S, V, M>,
        ): SubGVectorSpace<D, B, S, V, M> {
            return SubGVectorSpaceImpl(
                matrixSpace,
                totalGVectorSpace,
                name,
                boundedness,
                getVectorSpace,
            )
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromList(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            gVectorList: List<GVector<D, B, S, V>>,
        ): SubGVectorSpace<D, B, S, V, M> {
            // local variable is necessary for smart cast
            val degreeGroup = totalGVectorSpace.degreeGroup
            val boundedness = if (degreeGroup is AugmentedDegreeGroup) {
                Boundedness.fromDegreeList(
                    degreeGroup,
                    gVectorList.map { it.degree }
                )
            } else {
                Boundedness()
            }
            return SubGVectorSpaceImpl(
                matrixSpace,
                totalGVectorSpace,
                name,
                boundedness,
            ) { degree ->
                val vectorListForDegree = gVectorList.filter { it.degree == degree }.map { it.vector }
                SubVectorSpace(
                    matrixSpace,
                    totalGVectorSpace[degree],
                    vectorListForDegree,
                )
            }
        }
    }
}

private class SubGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val totalGVectorSpace: GVectorSpace<D, B, S, V>,
    override val name: String,
    override val boundedness: Boundedness,
    private val getVectorSpace: (D) -> SubVectorSpace<B, S, V, M>,
) : SubGVectorSpace<D, B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalGVectorSpace.numVectorSpace
    override val degreeGroup: DegreeGroup<D> = totalGVectorSpace.degreeGroup
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> =
        SubBasis.convertGetInternalPrintConfig(totalGVectorSpace.getInternalPrintConfig)
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)? =
        totalGVectorSpace.listDegreesForAugmentedDegree
    private val cache: MutableMap<D, SubVectorSpace<B, S, V, M>> = mutableMapOf()
    override val context: GVectorContext<D, SubBasis<B, S, V>, S, V> = GVectorContextImpl(this)
    override val underlyingGVectorSpace: SubGVectorSpace<D, B, S, V, M> = this
    override val inclusion: GLinearMap<D, SubBasis<B, S, V>, B, S, V, M> by lazy {
        GLinearMap(
            source = this,
            target = this.totalGVectorSpace,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "inclusion",
        ) { degree ->
            this[degree].inclusion
        }
    }

    override fun get(degree: D): SubVectorSpace<B, S, V, M> {
        return this.cache.getOrPut(degree) {
            this.getVectorSpace(degree)
        }
    }
    override fun get(degree: Int): SubVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    override fun toString(): String {
        return this.name
    }
}

private class WholeSubGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val totalGVectorSpace: GVectorSpace<D, B, S, V>,
) : SubGVectorSpace<D, B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalGVectorSpace.numVectorSpace
    override val degreeGroup: DegreeGroup<D> = totalGVectorSpace.degreeGroup
    override val name: String = totalGVectorSpace.name
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> =
        SubBasis.convertGetInternalPrintConfig(totalGVectorSpace.getInternalPrintConfig)
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)? =
        totalGVectorSpace.listDegreesForAugmentedDegree
    override val boundedness: Boundedness = totalGVectorSpace.boundedness
    private val cache: MutableMap<D, SubVectorSpace<B, S, V, M>> = mutableMapOf()
    override val context: GVectorContext<D, SubBasis<B, S, V>, S, V> = GVectorContextImpl(this)
    override val underlyingGVectorSpace: SubGVectorSpace<D, B, S, V, M> = this
    override val inclusion: GLinearMap<D, SubBasis<B, S, V>, B, S, V, M> by lazy {
        GLinearMap(
            source = this,
            target = this.totalGVectorSpace,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "inclusion",
        ) { degree ->
            this[degree].inclusion
        }
    }

    override fun get(degree: D): SubVectorSpace<B, S, V, M> {
        return this.cache.getOrPut(degree) {
            this.totalGVectorSpace[degree].asSubVectorSpace(matrixSpace)
        }
    }
    override fun get(degree: Int): SubVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    override fun toString(): String {
        return this.name
    }
}

public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
GVectorSpace<D, B, S, V>.asSubGVectorSpace(
    matrixSpace: MatrixSpace<S, V, M>,
): SubGVectorSpace<D, B, S, V, M> {
    return WholeSubGVectorSpace(matrixSpace, totalGVectorSpace = this)
}
