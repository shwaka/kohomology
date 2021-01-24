package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.util.StringTable

class DenseMatrix<S : Scalar<S>>(
    private val values: List<List<S>>,
    override val matrixSpace: DenseMatrixSpace<S>,
    rowCount: Int? = null,
    colCount: Int? = null
) : Matrix<S, DenseNumVector<S>, DenseMatrix<S>> {
    init {
        if (colCount == null && values.isEmpty())
            throw IllegalArgumentException("colCount should be explicitly set when the matrix has no row")
    }
    override val rowCount: Int = rowCount ?: this.values.size
    override val colCount: Int = colCount ?: this.values[0].size

    override fun plus(other: DenseMatrix<S>): DenseMatrix<S> {
        if (this.rowCount != other.rowCount || this.colCount != other.colCount) {
            throw ArithmeticException("Cannot add matrices")
        }
        val values = this.values.zip(other.values).map { (rowInThis, rowInOther) ->
            rowInThis.zip(rowInOther).map { (elmInThis, elmInOther) -> elmInThis + elmInOther }
        }
        return this.matrixSpace.fromRows(values)
    }

    override fun minus(other: DenseMatrix<S>): DenseMatrix<S> {
        return this + other * (-1)
    }

    override fun times(other: DenseMatrix<S>): DenseMatrix<S> {
        if (this.colCount != other.colCount) {
            throw ArithmeticException("Cannot multiply matrices")
        }
        val rowRange = 0 until this.rowCount
        val sumRange = 0 until this.colCount
        val colRange = 0 until other.colCount
        val values = rowRange.map { i ->
            colRange.map { j ->
                sumRange
                    .map { k -> this.values[i][k] * other.values[k][j] }
                    .reduce(Scalar<S>::plus)
            }
        }
        return this.matrixSpace.fromRows(values)
    }

    override fun times(scalar: S): DenseMatrix<S> {
        val values = this.values.map { row -> row.map { elm -> -elm } }
        return this.matrixSpace.fromRows(values)
    }

    override fun times(vector: DenseNumVector<S>): DenseNumVector<S> {
        val values = this.values.map { row ->
            row.zip(vector.values)
                .map { it.first * it.second }
                .reduce { a, b -> a + b }
        }
        return this.matrixSpace.vectorSpace.fromValues(values)
    }

    override fun rowEchelonForm(): Triple<DenseMatrix<S>, List<Int>, Int> {
        val (rowEchelonForm, pivots, exchangeCount) = this.values.rowEchelonForm()
        return Triple(this.matrixSpace.fromRows(rowEchelonForm), pivots, exchangeCount)
    }

    override fun get(rowInd: Int, colInd: Int): S {
        return this.values[rowInd][colInd]
    }

    override fun unwrap(): DenseMatrix<S> {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as DenseMatrix<*>

        if (this.values != other.values) return false
        if (this.matrixSpace != other.matrixSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + matrixSpace.hashCode()
        return result
    }

    private fun toStringTable(): StringTable {
        return StringTable(this.values.map { row -> row.map { it.toString() } })
    }

    override fun toString(): String {
        return this.toStringTable().toString()
    }

    override fun toPrettyString(): String {
        return this.toStringTable().toPrettyString()
    }
}

class DenseMatrixSpace<S : Scalar<S>>(
    override val vectorSpace: DenseNumVectorSpace<S>
) : MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> {
    override val field: Field<S> = vectorSpace.field

    override fun fromRows(rows: List<List<S>>): DenseMatrix<S> {
        if (rows.isEmpty())
            throw IllegalArgumentException("Row list is empty, which is not supported")
        return DenseMatrix(rows, this)
    }

    override fun fromCols(cols: List<List<S>>): DenseMatrix<S> {
        if (cols.isEmpty())
            throw IllegalArgumentException("Column list is empty, which is not supported")
        val rowCount = cols[0].size
        val colCount = cols.size
        val rows = (0 until rowCount).map { i -> (0 until colCount).map { j -> cols[j][i] } }
        return this.fromRows(rows)
    }

    override fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): DenseMatrix<S> {
        if (list.size != rowCount * colCount)
            throw IllegalArgumentException("The size of the list should be equal to rowCount * colCount")
        val values = (0 until rowCount).map { i -> list.subList(colCount * i, colCount * (i + 1)) }
        return DenseMatrix(values, this, rowCount, colCount)
    }
}
