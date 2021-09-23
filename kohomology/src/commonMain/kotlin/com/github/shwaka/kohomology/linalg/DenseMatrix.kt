package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.util.StringTable
import com.github.shwaka.parautil.pmap

public data class DenseMatrix<S : Scalar>(
    override val numVectorSpace: DenseNumVectorSpace<S>,
    val rowList: List<List<S>>,
    override val rowCount: Int,
    override val colCount: Int
) : Matrix<S, DenseNumVector<S>> {
    internal var rowEchelonForm: DenseRowEchelonForm<S>? = null
        set(value) {
            if (field != null)
                throw IllegalStateException("Cannot assign rowEchelonForm twice")
            field = value
        }

    init {
        if (this.rowList.any { row -> row.size != this.colCount })
            throw InvalidSizeException("The length of each row must be equal to colCount")
    }
    private fun toStringTable(): StringTable {
        return StringTable(this.rowList.map { row -> row.map { it.toString() } })
    }

    override fun toPrettyString(): String {
        return this.toStringTable().toPrettyString()
    }

    override fun toString(): String {
        return this.toStringTable().toString()
    }

    override operator fun get(rowInd: Int, colInd: Int): S {
        return this.rowList[rowInd][colInd]
    }

    override fun isZero(): Boolean {
        return this.rowList.all { row -> row.all { it.isZero() } }
    }
}

public class DenseMatrixSpace<S : Scalar> private constructor(
    override val numVectorSpace: DenseNumVectorSpace<S>
) : MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> {

    // class DenseMatrixContext<S : Scalar>(
    //     override val field: Field<S>,
    //     private val numVectorSpace: DenseNumVectorSpace<S>,
    //     private val matrixSpace: DenseMatrixSpace<S>
    // ) : MatrixOperations<S, DenseNumVector<S>, DenseMatrix<S>> by matrixSpace,
    //     MatrixContext<S, DenseNumVector<S>, DenseMatrix<S>>

    public companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<DenseNumVectorSpace<*>, DenseMatrixSpace<*>> = mutableMapOf()
        public fun <S : Scalar> from(numVectorSpace: DenseNumVectorSpace<S>): DenseMatrixSpace<S> {
            if (this.cache.containsKey(numVectorSpace)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[numVectorSpace] as DenseMatrixSpace<S>
            } else {
                val matrixSpace = DenseMatrixSpace(numVectorSpace)
                this.cache[numVectorSpace] = matrixSpace
                return matrixSpace
            }
        }
    }

    // override val matrixContext: MatrixContext<S, DenseNumVector<S>, DenseMatrix<S>> =
    //     DenseMatrixContext(this.numVectorSpace.field, this.numVectorSpace, this)

    override val field: Field<S> = this.numVectorSpace.field

    override val context: MatrixContext<S, DenseNumVector<S>, DenseMatrix<S>> = MatrixContext(this.field, this.numVectorSpace, this)
    override val matrixSpace: MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> = this

    override fun contains(matrix: DenseMatrix<S>): Boolean {
        return matrix.numVectorSpace == this.numVectorSpace
    }

    override fun add(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw IllegalContextException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw IllegalContextException("The denseMatrix $second does not match the context ($this)")
        if (first.rowCount != second.rowCount || first.colCount != second.colCount)
            throw InvalidSizeException("Cannot add matrices: different shapes")
        val rowList = first.rowList.zip(second.rowList).map { (rowInThis, rowInOther) ->
            rowInThis.zip(rowInOther).map { (elmInThis, elmInOther) -> this.field.context.run { elmInThis + elmInOther } }
        }
        return this.fromRowList(rowList)
    }

    override fun subtract(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw IllegalContextException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw IllegalContextException("The denseMatrix $second does not match the context ($this)")
        return this.context.run {
            first + (-1) * second
        }
    }

    override fun multiply(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw IllegalContextException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw IllegalContextException("The denseMatrix $second does not match the context ($this)")
        if (first.colCount != second.rowCount)
            throw InvalidSizeException("Cannot multiply matrices: first.colCount != second.rowCount")
        val rowRange = 0 until first.rowCount
        val sumRange = 0 until first.colCount
        val colRange = 0 until second.colCount
        val rowList = rowRange.map { i ->
            colRange.map { j ->
                this.field.context.run {
                    sumRange
                        .map { k -> first.rowList[i][k] * second.rowList[k][j] }
                        .sum()
                }
            }
        }
        return this.fromRowList(
            rowList = rowList,
            colCount = second.colCount
        )
    }

    override fun multiply(matrix: DenseMatrix<S>, scalar: S): DenseMatrix<S> {
        if (matrix !in this)
            throw IllegalContextException("The denseMatrix $matrix does not match the context ($this)")
        if (scalar !in this.field)
            throw IllegalContextException("The scalar $scalar does not match the context (${this.field})")
        val rowList = matrix.rowList.map { row -> row.map { elm -> this.field.context.run { elm * scalar } } }
        return this.fromRowList(rowList)
    }

    override fun multiply(matrix: DenseMatrix<S>, numVector: DenseNumVector<S>): DenseNumVector<S> {
        if (matrix !in this)
            throw IllegalContextException("The denseMatrix $matrix does not match the context ($this)")
        if (numVector !in this.numVectorSpace)
            throw IllegalContextException("The numVector $numVector does not match the context (${this.numVectorSpace})")
        if (matrix.colCount != numVector.dim)
            throw InvalidSizeException("Cannot multiply matrix and vector: matrix.colCount != vector.dim")
        val valueList = matrix.rowList.map { row ->
            this.field.context.run {
                row.zip(numVector.valueList)
                    .map { it.first * it.second }
                    .sum()
            }
        }
        return this.numVectorSpace.fromValueList(valueList)
    }

    override fun computeRowEchelonForm(matrix: DenseMatrix<S>): RowEchelonForm<S, DenseNumVector<S>, DenseMatrix<S>> {
        matrix.rowEchelonForm?.let { return it }
        val rowEchelonForm = DenseRowEchelonForm(this, matrix)
        matrix.rowEchelonForm = rowEchelonForm
        return rowEchelonForm
    }

    override fun computeTranspose(matrix: DenseMatrix<S>): DenseMatrix<S> {
        val rowCount = matrix.colCount
        val colCount = matrix.rowCount
        val rowList: List<List<S>> = (0 until rowCount).map { i ->
            (0 until colCount).map { j -> matrix[j, i] }
        }
        return DenseMatrix(
            this.numVectorSpace,
            rowCount = rowCount,
            colCount = colCount,
            rowList = rowList
        )
    }

    override fun fromRowList(rowList: List<List<S>>, colCount: Int?): DenseMatrix<S> {
        val rowCount = rowList.size
        val colCountNonNull: Int = when {
            rowList.isNotEmpty() -> rowList[0].size
            colCount != null -> colCount
            else -> throw IllegalArgumentException("Row list is empty and colCount is not specified")
        }
        return DenseMatrix(this.numVectorSpace, rowList, rowCount, colCountNonNull)
    }

    override fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): DenseMatrix<S> {
        val rowList = (0 until rowCount).map { rowInd ->
            val row = rowMap[rowInd]
            if (row == null) {
                List(colCount) { this.field.zero }
            } else {
                (0 until colCount).map { colInd ->
                    row[colInd] ?: this.field.zero
                }
            }
        }
        return this.fromRowList(rowList, colCount)
    }

    override fun joinMatrices(matrix1: DenseMatrix<S>, matrix2: DenseMatrix<S>): DenseMatrix<S> {
        if (matrix1.rowCount != matrix2.rowCount)
            throw InvalidSizeException("Cannot join two matrices of different row counts")
        val rowList = matrix1.rowList.zip(matrix2.rowList).map { (row1: List<S>, row2: List<S>) -> row1 + row2 }
        val rowCount = matrix1.rowCount
        val colCount = matrix1.colCount + matrix2.colCount
        return DenseMatrix(this.numVectorSpace, rowList, rowCount, colCount)
    }

    override fun computeRowSlice(matrix: DenseMatrix<S>, rowRange: IntRange): DenseMatrix<S> {
        val rowList = matrix.rowList.slice(rowRange)
        return this.fromRowList(rowList, colCount = matrix.colCount)
    }

    override fun computeColSlice(matrix: DenseMatrix<S>, colRange: IntRange): DenseMatrix<S> {
        val rowList = matrix.rowList.map { row -> row.slice(colRange) }
        return this.fromRowList(rowList, colCount = colRange.count())
    }

    override fun toString(): String {
        return "DenseMatrixSpace(${this.field})"
    }

    override fun fromNumVectorList(numVectors: List<DenseNumVector<S>>, dim: Int?): DenseMatrix<S> {
        // It is better to use toList() than toMap() for DenseMatrix
        if (numVectors.isEmpty() && (dim == null))
            throw IllegalArgumentException("Vector list is empty and dim is not specified")
        val colList = numVectors.pmap { v -> v.toList() }
        return this.fromColList(colList, dim)
    }
}
