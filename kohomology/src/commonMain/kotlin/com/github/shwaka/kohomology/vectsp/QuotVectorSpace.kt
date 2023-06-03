package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.Printer

public data class QuotBasis<B : BasisName, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>
) : BasisName {
    override fun toString(): String {
        return "[${this.vector}]"
    }

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>> convertGetInternalPrintConfig(
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
        ): (PrintConfig) -> InternalPrintConfig<QuotBasis<B, S, V>, S> {
            return { printConfig: PrintConfig ->
                val internalPrintConfig: InternalPrintConfig<B, S> = getInternalPrintConfig(printConfig)
                val printer = Printer(printConfig)
                InternalPrintConfig(
                    coeffToString = internalPrintConfig.coeffToString,
                    basisToString = { basisName: QuotBasis<B, S, V> ->
                        "(${printer(basisName.vector)})"
                    },
                    basisComparator = null,
                )
            }
        }
    }
}

public interface QuotVectorSpace<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorSpace<QuotBasis<B, S, V>, S, V> {
    public val totalVectorSpace: VectorSpace<B, S, V>
    public val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M>
    public val projection: LinearMap<B, QuotBasis<B, S, V>, S, V, M>

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalVectorSpace: VectorSpace<B, S, V>,
            quotientGenerator: List<Vector<B, S, V>>,
        ): QuotVectorSpace<B, S, V, M> {
            val factory = QuotFactory(
                matrixSpace,
                totalVectorSpace,
                quotientGenerator,
            )
            return QuotVectorSpaceImpl(factory)
        }
    }
}

private class QuotFactory<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val totalVectorSpace: VectorSpace<B, S, V>,
    private val quotientGenerator: List<Vector<B, S, V>>,
) {
    val numVectorSpace = matrixSpace.numVectorSpace

    private val rowEchelonForm: RowEchelonForm<S, V, M> by lazy {
        matrixSpace.context.run {
            val joinedMatrix: M = run {
                val quotientMatrix = matrixSpace.fromVectors(quotientGenerator, totalVectorSpace.dim)
                val id = matrixSpace.getIdentity(totalVectorSpace.dim)
                listOf(quotientMatrix, id).join()
            }
            joinedMatrix.rowEchelonForm
        }
    }

    // dimension of denominator
    private val quotientDim: Int by lazy {
        rowEchelonForm.pivots.filter { it < quotientGenerator.size }.size
    }

    private val basisIndices: List<Int> by lazy {
        rowEchelonForm.pivots.slice(quotientDim until totalVectorSpace.dim)
    }

    fun getBasisNames(): List<QuotBasis<B, S, V>> {
        return basisIndices.map { index -> totalVectorSpace.getBasis()[index - quotientGenerator.size] }
            .map { vector -> QuotBasis(vector) }
    }

    private val transformationMatrix: M by lazy {
        matrixSpace.context.run {
            val size = rowEchelonForm.reducedMatrix.colCount
            val dim = totalVectorSpace.dim
            rowEchelonForm.reducedMatrix.colSlice((size - dim) until size)
        }
    }

    fun getProjectionMatrix(): M {
        return matrixSpace.context.run {
            this@QuotFactory.transformationMatrix.rowSlice(quotientDim until (totalVectorSpace.dim))
        }
    }

    fun getSectionMatrix(): M {
        return matrixSpace.fromVectors(
            basisIndices.map { index ->
                totalVectorSpace.getBasis()[index - quotientGenerator.size]
            },
            this.totalVectorSpace.dim
        )
    }

    init {
        // check that generators are in totalVectorSpace
        for (vector in quotientGenerator) {
            require(vector in totalVectorSpace) {
                "The vector $vector is not contained in the vector space $totalVectorSpace"
            }
        }
    }

    companion object {
        fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> MatrixSpace<S, V, M>.fromVectors(
            vectors: List<Vector<B, S, V>>,
            dim: Int? = null
        ): M {
            val numVectorList = vectors.map { it.toNumVector() }
            return this.fromNumVectorList(numVectorList, dim)
        }
    }
}

private class QuotVectorSpaceImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val factory: QuotFactory<B, S, V, M>
) : QuotVectorSpace<B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = factory.numVectorSpace
    override val basisNames: List<QuotBasis<B, S, V>> by lazy { factory.getBasisNames() }
    override val totalVectorSpace: VectorSpace<B, S, V> = factory.totalVectorSpace
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<QuotBasis<B, S, V>, S> =
        QuotBasis.convertGetInternalPrintConfig(totalVectorSpace.getInternalPrintConfig)
    override val context: VectorContext<QuotBasis<B, S, V>, S, V> = VectorContextImpl(this)

    private val basisNameToIndex: Map<QuotBasis<B, S, V>, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
    }

    override fun indexOf(basisName: QuotBasis<B, S, V>): Int {
        return this.basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    override val projection: LinearMap<B, QuotBasis<B, S, V>, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this.factory.totalVectorSpace,
            target = this,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.getProjectionMatrix(),
        )
    }

    override val section: LinearMap<QuotBasis<B, S, V>, B, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this,
            target = this.factory.totalVectorSpace,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.getSectionMatrix(),
        )
    }

    override fun toString(): String {
        val basisNamesString = this.basisNames.joinToString(", ") { it.toString() }
        return "QuotVectorSpace($basisNamesString)"
    }
}
