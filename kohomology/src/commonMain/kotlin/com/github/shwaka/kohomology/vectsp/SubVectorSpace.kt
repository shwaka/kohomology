package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig

public data class SubBasis<B : BasisName, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>
) : BasisName {
    override fun toString(): String {
        return "(${this.vector})"
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
            val joinedMatrix: M = matrixSpace.context.run {
                val subspaceMatrix = matrixSpace.fromVectors(generator, totalVectorSpace.dim)
                val id = matrixSpace.getIdentity(totalVectorSpace.dim)
                listOf(subspaceMatrix, id).join()
            }
            joinedMatrix.rowEchelonForm
        }
    }

    val dim: Int by lazy { rowEchelonForm.pivots.filter { it < generator.size }.size }

    fun getBasisNames(): List<SubBasis<B, S, V>> {
        return (0 until this.dim).map { index -> this.generator[index] }
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
            (0 until this.dim).map { index -> this.generator[index].toNumVector() },
            this.totalVectorSpace.dim,
        )
    }

    init {
        // check that generators are in totalVectorSpace
        for (vector in generator)
            if (vector !in totalVectorSpace)
                throw IllegalContextException("The vector $vector is not contained in the vector space $totalVectorSpace")
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

public class SubVectorSpace<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: SubFactory<B, S, V, M>
) : VectorSpace<SubBasis<B, S, V>, S, V> {
    override val numVectorSpace: NumVectorSpace<S, V> = factory.numVectorSpace
    override val basisNames: List<SubBasis<B, S, V>> by lazy { factory.getBasisNames() }
    public val generator: List<Vector<B, S, V>> = factory.generator
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubBasis<B, S, V>, S> =
        InternalPrintConfig.Companion::default
    override val context: VectorContext<SubBasis<B, S, V>, S, V> = VectorContextImpl(this)

    private val basisNameToIndex: Map<SubBasis<B, S, V>, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
    }

    override fun indexOf(basisName: SubBasis<B, S, V>): Int {
        return this.basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    public val inclusion: LinearMap<SubBasis<B, S, V>, B, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this,
            target = this.factory.totalVectorSpace,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.getInclusionMatrix(),
        )
    }

    public fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.factory.contains(vector)
    }

    override fun toString(): String {
        val basisNamesString = this.basisNames.joinToString(", ") { it.toString() }
        return "SubVectorSpace($basisNamesString)"
    }

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
            return SubVectorSpace(factory)
        }
    }
}
