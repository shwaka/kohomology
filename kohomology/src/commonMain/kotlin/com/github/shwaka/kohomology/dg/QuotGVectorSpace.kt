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
import com.github.shwaka.kohomology.vectsp.QuotBasis
import com.github.shwaka.kohomology.vectsp.QuotVectorSpace

public interface QuotGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, QuotBasis<B, S, V>, S, V> {
    override fun get(degree: D): QuotVectorSpace<B, S, V, M>
    override fun get(degree: Int): QuotVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    public val matrixSpace: MatrixSpace<S, V, M>
    public val totalGVectorSpace: GVectorSpace<D, B, S, V>
    public val projection: GLinearMap<D, B, QuotBasis<B, S, V>, S, V, M>
    public val section: GLinearMap<D, QuotBasis<B, S, V>, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            name: String,
            boundedness: Boundedness = totalGVectorSpace.boundedness,
            getVectorSpace: (D) -> QuotVectorSpace<B, S, V, M>,
        ): QuotGVectorSpace<D, B, S, V, M> {
            return QuotGVectorSpaceImpl(
                matrixSpace,
                totalGVectorSpace,
                name,
                boundedness,
                getVectorSpace,
            )
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            totalGVectorSpace: GVectorSpace<D, B, S, V>,
            quotientGenerator: SubGVectorSpace<D, B, S, V, M>,
            boundedness: Boundedness = totalGVectorSpace.boundedness,
        ): QuotGVectorSpace<D, B, S, V, M> {
            return QuotGVectorSpace(
                matrixSpace,
                totalGVectorSpace,
                name = name,
                boundedness = boundedness,
            ) { degree ->
                QuotVectorSpace(
                    matrixSpace,
                    totalGVectorSpace[degree],
                    quotientGenerator = quotientGenerator[degree],
                )
            }
        }
    }
}

private class QuotGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    override val totalGVectorSpace: GVectorSpace<D, B, S, V>,
    override val name: String,
    override val boundedness: Boundedness,
    private val getVectorSpace: (D) -> QuotVectorSpace<B, S, V, M>,
) : QuotGVectorSpace<D, B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalGVectorSpace.numVectorSpace
    override val degreeGroup: DegreeGroup<D> = totalGVectorSpace.degreeGroup
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<QuotBasis<B, S, V>, S> =
        QuotBasis.convertGetInternalPrintConfig(totalGVectorSpace.getInternalPrintConfig)
    override val listDegreesForAugmentedDegree: ((Int) -> List<D>)? =
        totalGVectorSpace.listDegreesForAugmentedDegree
    private val cache: MutableMap<D, QuotVectorSpace<B, S, V, M>> = mutableMapOf()
    override val context: GVectorContext<D, QuotBasis<B, S, V>, S, V> = GVectorContextImpl(this)
    override val underlyingGVectorSpace: QuotGVectorSpace<D, B, S, V, M> = this
    override val zeroGVector: ZeroGVector<D, QuotBasis<B, S, V>, S, V> = ZeroGVector(this)
    override val section: GLinearMap<D, QuotBasis<B, S, V>, B, S, V, M> by lazy {
        GLinearMap(
            source = this,
            target = this.totalGVectorSpace,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "inclusion",
        ) { degree ->
            this[degree].section
        }
    }
    override val projection: GLinearMap<D, B, QuotBasis<B, S, V>, S, V, M> by lazy {
        GLinearMap(
            source = this.totalGVectorSpace,
            target = this,
            degree = this.degreeGroup.zero,
            matrixSpace = this.matrixSpace,
            name = "inclusion",
        ) { degree ->
            this[degree].projection
        }
    }

    override fun get(degree: D): QuotVectorSpace<B, S, V, M> {
        return this.cache.getOrPut(degree) {
            this.getVectorSpace(degree)
        }
    }
    override fun get(degree: Int): QuotVectorSpace<B, S, V, M> {
        return this[this.degreeGroup.fromInt(degree)]
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return this.name
    }
}
