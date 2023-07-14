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
import com.github.shwaka.kohomology.util.PrintType
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
    public val projection: GLinearMap<D, B, SubQuotBasis<B, S, V>, S, V, M>
    public val section: GLinearMap<D, SubQuotBasis<B, S, V>, B, S, V, M>
    public fun subspaceContains(gVector: GVector<D, B, S, V>): Boolean {
        val subQuotVectorSpace = this[gVector.degree]
        return subQuotVectorSpace.subspaceContains(gVector.vector)
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            subspaceGenerator: SubGVectorSpace<D, B, S, V, M>,
            quotientGenerator: SubGVectorSpace<D, B, S, V, M>,
            boundedness: Boundedness = totalGVectorSpace.boundedness,
            name: String = "${subspaceGenerator.name}/${quotientGenerator.name}",
        ): SubQuotGVectorSpace<D, B, S, V, M> {
            return SubQuotGVectorSpaceImpl(
                matrixSpace,
                totalGVectorSpace,
                subspaceGenerator = subspaceGenerator,
                quotientGenerator = quotientGenerator,
                name = name,
                boundedness = boundedness,
            )
        }
    }
}

private class SubQuotGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val totalGVectorSpace: GVectorSpace<D, B, S, V>,
    private val subspaceGenerator: SubGVectorSpace<D, B, S, V, M>,
    private val quotientGenerator: SubGVectorSpace<D, B, S, V, M>,
    override val name: String,
    override val boundedness: Boundedness,
) : SubQuotGVectorSpace<D, B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalGVectorSpace.numVectorSpace
    override val degreeGroup: DegreeGroup<D> = totalGVectorSpace.degreeGroup
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> =
        SubQuotBasis.convertGetInternalPrintConfig(totalGVectorSpace.getInternalPrintConfig)
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)? =
        totalGVectorSpace.listDegreesForAugmentedDegree
    private val cache: MutableMap<D, SubQuotVectorSpace<B, S, V, M>> = mutableMapOf()
    override val context: GVectorContext<D, SubQuotBasis<B, S, V>, S, V> = GVectorContextImpl(this)
    override val underlyingGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M> = this
    override val zeroGVector: ZeroGVector<D, SubQuotBasis<B, S, V>, S, V> = ZeroGVector(this)
    override val projection: GLinearMap<D, B, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        GLinearMap(
            source = this.totalGVectorSpace,
            target = this,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "projection",
        ) { degree ->
            this[degree].projection
        }
    }
    override val section: GLinearMap<D, SubQuotBasis<B, S, V>, B, S, V, M> by lazy {
        GLinearMap(
            source = this,
            target = this.totalGVectorSpace,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "section",
        ) { degree ->
            this[degree].section
        }
    }

    override fun get(degree: D): SubQuotVectorSpace<B, S, V, M> {
        return this.cache.getOrPut(degree) {
            SubQuotVectorSpace(
                matrixSpace,
                totalGVectorSpace[degree],
                subspaceGenerator = subspaceGenerator[degree],
                quotientGenerator = quotientGenerator[degree],
            )
        }
    }
    override fun get(degree: Int): SubQuotVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        val sub = this.subspaceGenerator.toString(printConfig)
        val quot = this.quotientGenerator.toString(printConfig)
        return "$sub/$quot"
    }
}
