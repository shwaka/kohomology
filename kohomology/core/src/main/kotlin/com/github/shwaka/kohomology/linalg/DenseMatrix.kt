package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

class DenseMatrix<S : Scalar<S>>(
    val values: List<List<S>>,
    override val matrixSpace: DenseMatrixSpace<S>
) : Matrix<S, DenseNumVector<S>, DenseMatrix<S>> {
    override val colCount: Int
        get() = this.values.size
    override val rowCount: Int
        get() = this.values[0].size // TODO: this throws an error when colCount = 0

    override fun plus(other: DenseMatrix<S>): DenseMatrix<S> {
        if (this.rowCount != other.rowCount || this.colCount != other.colCount) {
            throw ArithmeticException("Cannot add matrices")
        }
        val values = this.values.zip(other.values).map { (rowInThis, rowInOther) ->
            rowInThis.zip(rowInOther).map { (elmInThis, elmInOther) -> elmInThis + elmInOther }
        }
        return this.matrixSpace.get(values)
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
        return this.matrixSpace.get(values)
    }

    override fun times(scalar: S): DenseMatrix<S> {
        val values = this.values.map { row -> row.map { elm -> -elm } }
        return this.matrixSpace.get(values)
    }

    override fun times(vector: DenseNumVector<S>): DenseNumVector<S> {
        val values = this.values.map { row ->
            row.zip(vector.values)
                .map { it.first * it.second }
                .reduce { a, b -> a + b }
        }
        return this.matrixSpace.vectorSpace.get(values)
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
}

class DenseMatrixSpace<S : Scalar<S>>(
    override val vectorSpace: DenseNumVectorSpace<S>
) : MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> {
    override val field: Field<S> = vectorSpace.field

    override fun wrap(m: DenseMatrix<S>): Matrix<S, DenseNumVector<S>, DenseMatrix<S>> {
        return m
    }

    fun get(values: List<List<S>>): DenseMatrix<S> {
        return DenseMatrix(values, this)
    }

    fun fromRows(vararg rows: List<S>): DenseMatrix<S> {
        return DenseMatrix(rows.toList(), this)
    }
}
