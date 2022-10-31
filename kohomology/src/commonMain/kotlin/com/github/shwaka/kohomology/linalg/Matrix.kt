package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.getPermutation
import com.github.shwaka.parautil.pmapIndexedNotNull

public interface Matrix<S : Scalar, V : NumVector<S>> {
    public val numVectorSpace: NumVectorSpace<S, V>
    public val rowCount: Int
    public val colCount: Int
    public operator fun get(rowInd: Int, colInd: Int): S
    public fun toPrettyString(): String

    public fun toList(): List<List<S>> {
        return (0 until this.rowCount).map { i -> (0 until this.colCount).map { j -> this[i, j] } }
    }

    public fun toNumVectorList(): List<V> {
        return (0 until this.colCount).map { j ->
            this.numVectorSpace.fromValueList((0 until this.rowCount).map { i -> this[i, j] })
        }
    }

    public fun isZero(): Boolean
    public fun isNotZero(): Boolean = !this.isZero()
    public fun isIdentity(): Boolean
    public fun isNotIdentity(): Boolean = !this.isIdentity()
}

public interface MatrixContext<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> : NumVectorContext<S, V> {
    public val matrixSpace: MatrixSpace<S, V, M>

    public operator fun M.plus(other: M): M = this@MatrixContext.matrixSpace.add(this, other)
    public operator fun M.minus(other: M): M = this@MatrixContext.matrixSpace.subtract(this, other)
    public operator fun M.times(other: M): M = this@MatrixContext.matrixSpace.multiply(this, other)
    public operator fun M.times(numVector: V): V = this@MatrixContext.matrixSpace.multiply(this, numVector)
    public operator fun M.times(scalar: S): M = this@MatrixContext.matrixSpace.multiply(this, scalar)
    public operator fun S.times(matrix: M): M = matrix * this
    public operator fun M.times(scalar: Int): M = this@MatrixContext.matrixSpace.multiply(this, this@MatrixContext.matrixSpace.field.fromInt(scalar))
    public operator fun Int.times(matrix: M): M = matrix * this
    public operator fun M.times(sign: Sign): M {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(matrix: M): M = matrix * this
    public operator fun M.unaryMinus(): M = this * (-1)
    public val M.rowEchelonForm: RowEchelonForm<S, V, M>
        get() = this@MatrixContext.matrixSpace.computeRowEchelonForm(this) // TODO: cache!
    public fun M.rowSlice(rowRange: IntRange): M = this@MatrixContext.matrixSpace.computeRowSlice(this, rowRange)
    public fun M.colSlice(colRange: IntRange): M = this@MatrixContext.matrixSpace.computeColSlice(this, colRange)

    public fun List<M>.join(): M {
        if (this.isEmpty())
            throw IllegalArgumentException("Empty list of matrices cannot be reduced")
        return this.reduce { matrix1, matrix2 -> this@MatrixContext.matrixSpace.joinMatrices(matrix1, matrix2) }
    }

    public fun M.det(): S {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Determinant is defined only for square matrices")
        val rowEchelonForm = this.rowEchelonForm
        val rowEchelonMatrix: M = rowEchelonForm.matrix
        val sign: Sign = rowEchelonForm.sign
        return this@MatrixContext.matrixSpace.field.context.run {
            val detUpToSign = (0 until this@det.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
            detUpToSign * sign
        }
    }

    public fun M.detByPermutations(): S {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Determinant is defined only for square matrices")
        val n = this.rowCount
        var result: S = zero
        this@MatrixContext.matrixSpace.field.context.run {
            for ((perm, sign) in getPermutation((0 until n).toList())) {
                val product: S = (0 until n).zip(perm).map { (i, j) -> this@detByPermutations[i, j] }.reduce { a, b -> a * b }
                result += sign * product
            }
        }
        return result
    }

    public fun M.isInvertible(): Boolean {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Invertibility of non-square matrix is not defined")
        val pivots: List<Int> = this.rowEchelonForm.pivots
        return pivots.size == this.rowCount
    }

    public fun M.transpose(): M {
        return this@MatrixContext.matrixSpace.computeTranspose(this)
    }

    public fun M.innerProduct(numVector1: V, numVector2: V): S {
        return numVector1 dot (this * numVector2)
    }

    public fun M.computeKernelBasis(): List<V> {
        val rowEchelonForm = this.rowEchelonForm // TODO: cache できてないので、とりあえず local 変数に代入して誤魔化す
        val dim = this.colCount
        val pivots = rowEchelonForm.pivots
        val firstNonZeroIndex: Int = if (pivots.isEmpty()) this.colCount else pivots[0]
        val trivialVectors: List<V> = (0 until firstNonZeroIndex).map { i ->
            this.numVectorSpace.getOneAtIndex(i, dim)
        }
        val matrix = rowEchelonForm.reducedMatrix
        val vectorsForPivots: List<V> = pivots.indices.map { p ->
            val start = pivots[p] + 1
            val limit = if (p + 1 < pivots.size) pivots[p + 1] else dim
            (start until limit).map { k ->
                var numVector = this.numVectorSpace.getOneAtIndex(k, dim)
                for (q in p downTo 0) {
                    numVector -= this.numVectorSpace.getOneAtIndex(pivots[q], dim) * matrix[q, k]
                }
                numVector
            }
        }.flatten()
        return trivialVectors + vectorsForPivots
    }

    public fun M.computeImageBasis(): List<V> {
        // val rowEchelonForm = this.rowEchelonForm
        val pivots = this.rowEchelonForm.pivots
        val numVectorList = this.toNumVectorList()
        return pivots.map { pivot ->
            numVectorList[pivot]
        }
    }

    public fun M.findPreimage(numVector: V): V? {
        if (this.rowCount != numVector.dim)
            throw InvalidSizeException("Cannot consider preimage since numVector.dim != matrix.colCount")
        if (numVector.isZero())
            return this.numVectorSpace.getZero(this.colCount)
        val pivots = this.rowEchelonForm.pivots
        val reducedTransformation = this.rowEchelonForm.reducedTransformation
        val transformedNumVector = reducedTransformation * numVector
        if ((pivots.size until this.rowCount).any { transformedNumVector[it] != zero })
            return null
        val valueMap = pivots.mapIndexed { index, pivot ->
            val value = transformedNumVector[index]
            Pair(pivot, value)
        }.toMap()
        return this.numVectorSpace.fromValueMap(valueMap, this.colCount)
    }

    public fun List<List<S>>.toMatrix(colCount: Int? = null): M {
        return this@MatrixContext.matrixSpace.fromRowList(this, colCount)
    }
    public fun Map<Int, Map<Int, S>>.toMatrix(rowCount: Int, colCount: Int): M {
        return this@MatrixContext.matrixSpace.fromRowMap(this, rowCount, colCount)
    }
    // fun List<V>.toMatrix(dim: Int? = null) = this@MatrixContext.matrixSpace.fromNumVectorList(this, dim) // Platform declaration clash
    public fun List<S>.toMatrix(rowCount: Int, colCount: Int): M {
        return this@MatrixContext.matrixSpace.fromFlatList(this, rowCount, colCount)
    }
}

public class MatrixContextImpl<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
) : MatrixContext<S, V, M>, NumVectorContext<S, V> by NumVectorContextImpl(matrixSpace.numVectorSpace)

public interface MatrixSpace<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val context: MatrixContext<S, V, M>
    public val numVectorSpace: NumVectorSpace<S, V>
    public val field: Field<S>

    public fun getZero(rowCount: Int, colCount: Int): M {
        val rowMap: Map<Int, Map<Int, S>> = mapOf()
        return this.fromRowMap(rowMap, rowCount, colCount)
    }

    public fun getZero(dim: Int): M {
        return this.getZero(dim, dim)
    }

    public fun getIdentity(dim: Int): M {
        val one = this.field.one
        val rowMap = List(dim) { i -> i to mapOf(i to one) }.toMap()
        return this.fromRowMap(rowMap, dim, dim)
    }
    public operator fun contains(matrix: M): Boolean
    public fun add(first: M, second: M): M
    public fun subtract(first: M, second: M): M
    public fun multiply(first: M, second: M): M
    public fun multiply(matrix: M, numVector: V): V
    public fun multiply(matrix: M, scalar: S): M
    public fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M>
    public fun computeTranspose(matrix: M): M
    public fun joinMatrices(matrix1: M, matrix2: M): M
    public fun computeRowSlice(matrix: M, rowRange: IntRange): M
    public fun computeColSlice(matrix: M, colRange: IntRange): M
    public fun fromRowList(rowList: List<List<S>>, colCount: Int? = null): M
    public fun fromColList(colList: List<List<S>>, rowCount: Int? = null): M {
        val rowCountNonNull: Int = when {
            colList.isNotEmpty() -> colList[0].size
            rowCount != null -> rowCount
            else -> throw IllegalArgumentException("Column list is empty and rowCount is not specified")
        }
        val colCount = colList.size
        val rows = (0 until rowCountNonNull).map { i -> (0 until colCount).map { j -> colList[j][i] } }
        return this.fromRowList(rows, colCount)
    }
    public fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M
    public fun fromColMap(colMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M {
        val rowMap: MutableMap<Int, MutableMap<Int, S>> = mutableMapOf()
        for ((colInd, col) in colMap) {
            for ((rowInd, elm) in col) {
                val row = rowMap.getOrPut(rowInd) { mutableMapOf() }
                row[colInd] = elm
            }
        }
        return this.fromRowMap(rowMap, rowCount, colCount)
    }
    public fun fromNumVectorList(numVectors: List<V>, dim: Int? = null): M {
        // overridden in DenseMatrixSpace to use toList() instead of toMap()
        if (numVectors.isEmpty() && (dim == null))
            throw IllegalArgumentException("Vector list is empty and dim is not specified")
        val dimNotNull: Int = dim ?: numVectors[0].dim
        val colMap = numVectors.pmapIndexedNotNull { i, v ->
            if (v.isZero())
                null
            else
                Pair(i, v.toMap())
        }.toMap()
        return this.fromColMap(colMap, dimNotNull, numVectors.size)
    }
    public fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M {
        if (list.size != rowCount * colCount)
            throw InvalidSizeException("The size of the list should be equal to rowCount * colCount")
        val rowList = (0 until rowCount).map { i -> list.subList(colCount * i, colCount * (i + 1)) }
        return this.fromRowList(rowList, colCount)
    }
}

public abstract class RowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    protected open val matrixSpace: MatrixSpace<S, V, M>, // type is overridden in SetRowEchelonForm
    protected val originalMatrix: M,
) {
    public val matrix: M by lazy {
        this.computeRowEchelonForm()
    }
    public val reducedMatrix: M by lazy {
        this.computeReducedRowEchelonForm()
    }
    public val pivots: List<Int> by lazy {
        this.computePivots()
    }
    public val sign: Sign by lazy {
        this.computeSign()
    }
    protected abstract fun computeRowEchelonForm(): M
    protected abstract fun computeReducedRowEchelonForm(): M
    protected abstract fun computePivots(): List<Int>
    protected abstract fun computeSign(): Sign

    private val augmentedOriginalMatrix: M by lazy {
        val rowCount = this.originalMatrix.rowCount
        this.matrixSpace.context.run {
            listOf(
                this@RowEchelonForm.originalMatrix,
                this@RowEchelonForm.matrixSpace.getIdentity(rowCount)
            ).join()
        }
    }

    public val transformation: M by lazy {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        this.matrixSpace.context.run {
            this@RowEchelonForm.augmentedOriginalMatrix.rowEchelonForm.matrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
    public val reducedTransformation: M by lazy {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        this.matrixSpace.context.run {
            this@RowEchelonForm.augmentedOriginalMatrix.rowEchelonForm.reducedMatrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
}
