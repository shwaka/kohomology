package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig

/**
 * An implementation of [BasisName] for a direct sum.
 */
public data class DirectSumBasis<B>(val index: Int, val basisName: B) : BasisName

private class DirectSumFactory<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val vectorSpaceList: List<VectorSpace<B, S, V>>,
    val matrixSpace: MatrixSpace<S, V, M>,
    val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<DirectSumBasis<B>, S>,
) {
    val numVectorSpace: NumVectorSpace<S, V> = matrixSpace.numVectorSpace
    // TODO: vectorSpaceList から取得できる numVectorSpace たちと一致しているかチェックする？

    val basisNames: List<DirectSumBasis<B>> = vectorSpaceList.mapIndexed { index, vectorSpace ->
        vectorSpace.basisNames.map { basisName ->
            DirectSumBasis(index, basisName)
        }
    }.flatten()
}

/**
 * A direct sum of a finite number of vector spaces.
 */
public class DirectSum<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: DirectSumFactory<B, S, V, M>
) : VectorSpace<DirectSumBasis<B>, S, V> {
    override val numVectorSpace: NumVectorSpace<S, V> = factory.numVectorSpace
    override val basisNames: List<DirectSumBasis<B>> = factory.basisNames
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<DirectSumBasis<B>, S> =
        factory.getInternalPrintConfig
    override val context: VectorContext<DirectSumBasis<B>, S, V> = VectorContextImpl(this)

    private val basisNameToIndex: Map<DirectSumBasis<B>, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
    }

    override fun indexOf(basisName: DirectSumBasis<B>): Int {
        return this.basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    /** A list of vector spaces in a direct sum. */
    public val vectorSpaceList: List<VectorSpace<B, S, V>> = factory.vectorSpaceList

    /** The [MatrixSpace] used in a direct sum. */
    public val matrixSpace: MatrixSpace<S, V, M> = factory.matrixSpace

    /** The number of vector spaces in a direct sum. */
    public val size: Int = vectorSpaceList.size

    /**
     * Constructs the direct sum of vector spaces in [vectorSpaceList].
     *
     * The list [vectorSpaceList] can contain a vector space more than once,
     * since they are distinguished by the index in the list.
     */
    public constructor(
        vectorSpaceList: List<VectorSpace<B, S, V>>,
        matrixSpace: MatrixSpace<S, V, M>,
        getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<DirectSumBasis<B>, S> =
            { printConfig -> InternalPrintConfig.default(printConfig) },
    ) : this(DirectSumFactory(vectorSpaceList, matrixSpace, getInternalPrintConfig))

    private val inclusionList: List<LinearMap<B, DirectSumBasis<B>, S, V, M>> by lazy {
        val one = this.matrixSpace.field.one
        (0 until this.size).map { index ->
            // ↓累積和にした方が速いけど、その必要ある？
            val accumulatedDim = (0 until index).map { this.vectorSpaceList[it].dim }.sum()
            val currentDim = this.vectorSpaceList[index].dim
            val rowMap: Map<Int, Map<Int, S>> = (0 until currentDim).map { colIndex ->
                Pair(accumulatedDim + colIndex, mapOf(colIndex to one))
            }.toMap()
            val matrix = this.matrixSpace.fromRowMap(rowMap, rowCount = this.dim, colCount = currentDim)
            LinearMap.fromMatrix(this.vectorSpaceList[index], this, this.matrixSpace, matrix)
        }
    }

    /** Inclusion from a component to a direct sum. */
    public fun inclusion(index: Int): LinearMap<B, DirectSumBasis<B>, S, V, M> {
        if (index < 0)
            throw IndexOutOfBoundsException("index must be non-negative")
        if (index >= this.size)
            throw IndexOutOfBoundsException("index must be smaller than the number of vector spaces in the direct sum")
        return this.inclusionList[index]
    }

    private val projectionList: List<LinearMap<DirectSumBasis<B>, B, S, V, M>> by lazy {
        (0 until this.size).map { index ->
            val matrix = this.matrixSpace.context.run {
                this@DirectSum.inclusionList[index].matrix.transpose()
            }
            LinearMap.fromMatrix(this, this.vectorSpaceList[index], this.matrixSpace, matrix)
        }
    }

    /** Projection from a direct sum to a component. */
    public fun projection(index: Int): LinearMap<DirectSumBasis<B>, B, S, V, M> {
        if (index < 0)
            throw IndexOutOfBoundsException("index must be non-negative")
        if (index >= this.size)
            throw IndexOutOfBoundsException("index must be smaller than the number of vector spaces in the direct sum")
        return this.projectionList[index]
    }

    /** Construct an element of a direct sum from a list of vectors. */
    public fun fromVectorList(vectorList: List<Vector<B, S, V>>): Vector<DirectSumBasis<B>, S, V> {
        if (vectorList.size != this.size)
            throw IllegalArgumentException("The size (${vectorList.size}) of vectorList must be equal to the number (${this.size}) of vector spaces in the direct sum")
        val vectorListInDirectSum = vectorList.mapIndexed { i, vector -> this.inclusion(i)(vector) }
        return this.context.run {
            vectorListInDirectSum.sum()
        }
    }

    /** Represent an element of a direct sum as a list of vectors. */
    public fun toVectorList(vector: Vector<DirectSumBasis<B>, S, V>): List<Vector<B, S, V>> {
        return (0 until this.size).map { i -> this.projection(i)(vector) }
    }

    override fun toString(): String {
        return "DirectSum(${this.vectorSpaceList.joinToString(", ") { it.toString() }})"
    }
}
