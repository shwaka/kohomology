package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.getPermutation

interface Matrix<S : Scalar, V : NumVector<S>> {
    val numVectorSpace: NumVectorSpace<S, V>
    val rowCount: Int
    val colCount: Int
    operator fun get(rowInd: Int, colInd: Int): S
    fun toPrettyString(): String

    fun toList(): List<List<S>> {
        return (0 until this.rowCount).map { i -> (0 until this.colCount).map { j -> this[i, j] } }
    }

    fun toNumVectorList(): List<V> {
        return (0 until this.colCount).map { j ->
            this.numVectorSpace.fromValueList((0 until this.rowCount).map { i -> this[i, j] })
        }
    }

    fun isZero(): Boolean
    fun isNotZero(): Boolean = !this.isZero()
}

interface MatrixOperations<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val matrixSpace: MatrixSpace<S, V, M>
    operator fun contains(matrix: M): Boolean
    fun add(first: M, second: M): M
    fun subtract(first: M, second: M): M
    fun multiply(first: M, second: M): M
    fun multiply(matrix: M, numVector: V): V
    fun multiply(matrix: M, scalar: S): M
    fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M>
    fun computeTranspose(matrix: M): M
    fun joinMatrices(matrix1: M, matrix2: M): M
    fun computeRowSlice(matrix: M, rowRange: IntRange): M
    fun computeColSlice(matrix: M, colRange: IntRange): M
    fun fromRowList(rowList: List<List<S>>, colCount: Int? = null): M
    fun fromColList(colList: List<List<S>>, rowCount: Int? = null): M {
        val rowCountNonNull: Int = when {
            colList.isNotEmpty() -> colList[0].size
            rowCount != null -> rowCount
            else -> throw IllegalArgumentException("Column list is empty and rowCount is not specified")
        }
        val colCount = colList.size
        val rows = (0 until rowCountNonNull).map { i -> (0 until colCount).map { j -> colList[j][i] } }
        return this.fromRowList(rows, colCount)
    }
    fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M
    fun fromColMap(colMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M {
        val rowMap: MutableMap<Int, MutableMap<Int, S>> = mutableMapOf()
        for ((colInd, col) in colMap) {
            for ((rowInd, elm) in col) {
                val row = rowMap.getOrPut(rowInd) { mutableMapOf() }
                row[colInd] = elm
            }
        }
        return this.fromRowMap(rowMap, rowCount, colCount)
    }
    fun fromNumVectorList(numVectors: List<V>, dim: Int? = null): M {
        if (numVectors.isEmpty() && (dim == null))
            throw IllegalArgumentException("Vector list is empty and dim is not specified")
        val cols = numVectors.map { v -> v.toList() }
        return this.fromColList(cols, dim)
    }
    fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M {
        if (list.size != rowCount * colCount)
            throw InvalidSizeException("The size of the list should be equal to rowCount * colCount")
        val rowList = (0 until rowCount).map { i -> list.subList(colCount * i, colCount * (i + 1)) }
        return this.fromRowList(rowList, colCount)
    }
}

class MatrixContext<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    private val matrixOperations: MatrixOperations<S, V, M>
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), MatrixOperations<S, V, M> by matrixOperations {
    operator fun M.plus(other: M): M = this@MatrixContext.add(this, other)
    operator fun M.minus(other: M): M = this@MatrixContext.subtract(this, other)
    operator fun M.times(other: M): M = this@MatrixContext.multiply(this, other)
    operator fun M.times(numVector: V): V = this@MatrixContext.multiply(this, numVector)
    operator fun M.times(scalar: S): M = this@MatrixContext.multiply(this, scalar)
    operator fun S.times(matrix: M): M = matrix * this
    operator fun M.times(scalar: Int): M = this@MatrixContext.multiply(this, this@MatrixContext.field.fromInt(scalar))
    operator fun Int.times(matrix: M): M = matrix * this
    operator fun M.unaryMinus(): M = this * (-1)
    val M.rowEchelonForm: RowEchelonForm<S, V, M>
        get() = this@MatrixContext.computeRowEchelonForm(this) // TODO: cache!
    fun M.rowSlice(rowRange: IntRange): M = this@MatrixContext.computeRowSlice(this, rowRange)
    fun M.colSlice(colRange: IntRange): M = this@MatrixContext.computeColSlice(this, colRange)

    fun List<M>.join(): M {
        if (this.isEmpty())
            throw IllegalArgumentException("Empty list of matrices cannot be reduced")
        return this.reduce { matrix1, matrix2 -> this@MatrixContext.joinMatrices(matrix1, matrix2) }
    }

    fun M.det(): S {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Determinant is defined only for square matrices")
        val rowEchelonForm = this.rowEchelonForm
        val rowEchelonMatrix: M = rowEchelonForm.matrix
        val sign: Sign = rowEchelonForm.sign
        return this@MatrixContext.field.context.run {
            val detUpToSign = (0 until this@det.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
            detUpToSign * sign
        }
    }

    fun M.detByPermutations(): S {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Determinant is defined only for square matrices")
        val n = this.rowCount
        var result: S = zero
        this@MatrixContext.field.context.run {
            for ((perm, sign) in getPermutation((0 until n).toList())) {
                val product: S = (0 until n).zip(perm).map { (i, j) -> this@detByPermutations[i, j] }.reduce { a, b -> a * b }
                result += sign * product
            }
        }
        return result
    }

    fun M.isInvertible(): Boolean {
        if (this.rowCount != this.colCount)
            throw InvalidSizeException("Invertibility of non-square matrix is not defined")
        val pivots: List<Int> = this.rowEchelonForm.pivots
        return pivots.size == this.rowCount
    }

    fun M.transpose(): M {
        return this@MatrixContext.computeTranspose(this)
    }

    fun M.innerProduct(numVector1: V, numVector2: V): S {
        return numVector1 dot (this * numVector2)
    }

    fun M.computeKernelBasis(): List<V> {
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

    fun M.findPreimage(numVector: V): V? {
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

    fun List<List<S>>.toMatrix(colCount: Int? = null): M = this@MatrixContext.fromRowList(this, colCount)
    fun Map<Int, Map<Int, S>>.toMatrix(rowCount: Int, colCount: Int) = this@MatrixContext.fromRowMap(this, rowCount, colCount)
    // fun List<V>.toMatrix(dim: Int? = null) = this@MatrixContext.fromNumVectorList(this, dim) // Platform declaration clash
    fun List<S>.toMatrix(rowCount: Int, colCount: Int) = this@MatrixContext.fromFlatList(this, rowCount, colCount)
}

interface MatrixSpace<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> : MatrixOperations<S, V, M> {
    val context: MatrixContext<S, V, M>
    val numVectorSpace: NumVectorSpace<S, V>
    val field: Field<S>

    fun getZero(rowCount: Int, colCount: Int): M {
        val zero = this.field.zero
        val rows = List(rowCount) { List(colCount) { zero } }
        return this.fromRowList(rows)
    }

    fun getZero(dim: Int): M {
        return this.getZero(dim, dim)
    }

    fun getId(dim: Int): M {
        val zero = this.field.zero
        val one = this.field.one
        val rows = List(dim) { i ->
            List(dim) { j ->
                if (i == j)
                    one
                else
                    zero
            }
        }
        return this.fromRowList(rows, colCount = dim)
    }
}

abstract class RowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    protected val matrixSpace: MatrixSpace<S, V, M>,
    protected val originalMatrix: M,
) {
    val matrix: M by lazy {
        this.computeRowEchelonForm()
    }
    val reducedMatrix: M by lazy {
        this.computeReducedRowEchelonForm()
    }
    val pivots: List<Int> by lazy {
        this.computePivots()
    }
    val sign: Sign by lazy {
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
                this@RowEchelonForm.matrixSpace.getId(rowCount)
            ).join()
        }
    }

    val transformation: M by lazy {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        this.matrixSpace.context.run {
            this@RowEchelonForm.augmentedOriginalMatrix.rowEchelonForm.matrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
    val reducedTransformation: M by lazy {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        this.matrixSpace.context.run {
            this@RowEchelonForm.augmentedOriginalMatrix.rowEchelonForm.reducedMatrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
}
