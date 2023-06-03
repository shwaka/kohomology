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

public data class SubBasis<B : BasisName, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>
) : BasisName {
    override fun toString(): String {
        return "(${this.vector})"
    }

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>> convertGetInternalPrintConfig(
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
        ): (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> {
            return { printConfig: PrintConfig ->
                val internalPrintConfig: InternalPrintConfig<B, S> = getInternalPrintConfig(printConfig)
                val printer = Printer(printConfig)
                InternalPrintConfig(
                    coeffToString = internalPrintConfig.coeffToString,
                    basisToString = { basisName: SubBasis<B, S, V> ->
                        "(${printer(basisName.vector)})"
                    },
                    basisComparator = null,
                )
            }
        }
    }
}

public interface SubVectorSpace<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    VectorSpace<SubBasis<B, S, V>, S, V> {
    public val totalVectorSpace: VectorSpace<B, S, V>
    public val generator: List<Vector<B, S, V>>
    public val inclusion: LinearMap<SubBasis<B, S, V>, B, S, V, M>
    public val retraction: LinearMap<B, SubBasis<B, S, V>, S, V, M>
    public fun subspaceContains(vector: Vector<B, S, V>): Boolean

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalVectorSpace: VectorSpace<B, S, V>,
            generator: List<Vector<B, S, V>>,
        ): SubVectorSpace<B, S, V, M> {
            val factory = SubFactory(
                matrixSpace,
                totalVectorSpace,
                generator,
            )
            return SubVectorSpaceImpl(factory)
        }
    }
}

private class SubFactory<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val totalVectorSpace: VectorSpace<B, S, V>,
    val generator: List<Vector<B, S, V>>,
) {
    val numVectorSpace = matrixSpace.numVectorSpace

    private val rowEchelonForm: RowEchelonForm<S, V, M> by lazy {
        matrixSpace.context.run {
            val joinedMatrix: M = run {
                val subspaceMatrix = matrixSpace.fromVectors(generator, totalVectorSpace.dim)
                val id = matrixSpace.getIdentity(totalVectorSpace.dim)
                listOf(subspaceMatrix, id).join()
            }
            joinedMatrix.rowEchelonForm
        }
    }

    val dim: Int by lazy { rowEchelonForm.pivots.filter { it < generator.size }.size }

    private val basisIndices: List<Int> by lazy {
        rowEchelonForm.pivots.slice(0 until dim)
    }

    fun getBasisNames(): List<SubBasis<B, S, V>> {
        return this.basisIndices.map { index -> this.generator[index] }
            .map { vector -> SubBasis(vector) }
    }

    private val transformationMatrix: M by lazy {
        this.matrixSpace.context.run {
            val size = this@SubFactory.rowEchelonForm.reducedMatrix.colCount
            val dim = this@SubFactory.totalVectorSpace.dim
            this@SubFactory.rowEchelonForm.reducedMatrix.colSlice((size - dim) until size)
        }
    }

    fun getInclusionMatrix(): M {
        return this.matrixSpace.fromNumVectorList(
            this.basisIndices.map { index -> this.generator[index].toNumVector() },
            this.totalVectorSpace.dim,
        )
    }

    fun getRetractionMatrix(): M {
        return this.matrixSpace.context.run {
            this@SubFactory.transformationMatrix.rowSlice(0 until this@SubFactory.dim)
        }
    }

    init {
        // check that generators are in totalVectorSpace
        for (vector in generator) {
            require(vector in totalVectorSpace) {
                "The vector $vector is not contained in the vector space $totalVectorSpace"
            }
        }
    }

    fun contains(vector: Vector<B, S, V>): Boolean {
        return this.matrixSpace.context.run {
            val numVector = this@SubFactory.transformationMatrix * vector.numVector
            val start = this@SubFactory.dim
            val limit = this@SubFactory.totalVectorSpace.dim
            (start until limit).all { numVector[it].isZero() }
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

private class SubVectorSpaceImpl<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val factory: SubFactory<B, S, V, M>
) : SubVectorSpace<B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = factory.numVectorSpace
    override val basisNames: List<SubBasis<B, S, V>> by lazy { factory.getBasisNames() }
    override val totalVectorSpace: VectorSpace<B, S, V> = factory.totalVectorSpace
    override val generator: List<Vector<B, S, V>> = factory.generator
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> =
        SubBasis.convertGetInternalPrintConfig(totalVectorSpace.getInternalPrintConfig)
    override val context: VectorContext<SubBasis<B, S, V>, S, V> = VectorContextImpl(this)

    private val basisNameToIndex: Map<SubBasis<B, S, V>, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
    }

    override fun indexOf(basisName: SubBasis<B, S, V>): Int {
        return this.basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    override val inclusion: LinearMap<SubBasis<B, S, V>, B, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this,
            target = this.factory.totalVectorSpace,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.getInclusionMatrix(),
        )
    }

    override val retraction: LinearMap<B, SubBasis<B, S, V>, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this.factory.totalVectorSpace,
            target = this,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.getRetractionMatrix(),
        )
    }

    override fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.factory.contains(vector)
    }

    override fun toString(): String {
        val basisNamesString = this.basisNames.joinToString(", ") { it.toString() }
        return "SubVectorSpace($basisNamesString)"
    }
}

private class WholeSubVectorSpace<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    override val totalVectorSpace: VectorSpace<B, S, V>,
) : SubVectorSpace<B, S, V, M> {
    override val numVectorSpace: NumVectorSpace<S, V> = totalVectorSpace.numVectorSpace
    override val basisNames: List<SubBasis<B, S, V>> = totalVectorSpace.getBasis().map { SubBasis(it) }
    override val generator: List<Vector<B, S, V>> = totalVectorSpace.getBasis()
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> =
        SubBasis.convertGetInternalPrintConfig(totalVectorSpace.getInternalPrintConfig)
    override val context: VectorContext<SubBasis<B, S, V>, S, V> = VectorContextImpl(this)
    override fun subspaceContains(vector: Vector<B, S, V>): Boolean = true

    private val basisNameToIndex: Map<SubBasis<B, S, V>, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
    }

    override fun indexOf(basisName: SubBasis<B, S, V>): Int {
        return this.basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    override val inclusion: LinearMap<SubBasis<B, S, V>, B, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this,
            target = this.totalVectorSpace,
            matrixSpace = this.matrixSpace,
            matrix = this.matrixSpace.getIdentity(this.totalVectorSpace.dim),
        )
    }

    override val retraction: LinearMap<B, SubBasis<B, S, V>, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this.totalVectorSpace,
            target = this,
            matrixSpace = this.matrixSpace,
            matrix = this.matrixSpace.getIdentity(this.totalVectorSpace.dim),
        )
    }
}

public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
VectorSpace<B, S, V>.asSubVectorSpace(
    matrixSpace: MatrixSpace<S, V, M>,
): SubVectorSpace<B, S, V, M> {
    return WholeSubVectorSpace(matrixSpace, totalVectorSpace = this)
}
