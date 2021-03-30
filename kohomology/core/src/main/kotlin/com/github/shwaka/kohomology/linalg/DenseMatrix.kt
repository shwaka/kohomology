package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.StringTable

data class DenseMatrix<S : Scalar>(
    override val numVectorSpace: DenseNumVectorSpace<S>,
    val values: List<List<S>>,
    override val rowCount: Int,
    override val colCount: Int
) : Matrix<S, DenseNumVector<S>> {
    init {
        if (this.values.any { row -> row.size != this.colCount })
            throw IllegalArgumentException("The length of each row must be equal to colCount")
    }
    private fun toStringTable(): StringTable {
        return StringTable(this.values.map { row -> row.map { it.toString() } })
    }

    override fun toPrettyString(): String {
        return this.toStringTable().toPrettyString()
    }

    override fun toString(): String {
        return this.toStringTable().toString()
    }
}

class DenseMatrixSpace<S : Scalar>(
    override val numVectorSpace: DenseNumVectorSpace<S>
) : MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> {

    // class DenseMatrixContext<S : Scalar>(
    //     override val field: Field<S>,
    //     private val numVectorSpace: DenseNumVectorSpace<S>,
    //     private val matrixSpace: DenseMatrixSpace<S>
    // ) : MatrixOperations<S, DenseNumVector<S>, DenseMatrix<S>> by matrixSpace,
    //     MatrixContext<S, DenseNumVector<S>, DenseMatrix<S>>

    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<DenseNumVectorSpace<*>, DenseMatrixSpace<*>> = mutableMapOf()
        fun <S : Scalar> from(numVectorSpace: DenseNumVectorSpace<S>): DenseMatrixSpace<S> {
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

    val field: Field<S> = this.numVectorSpace.field

    override val matrixContext = MatrixContext(this.field, this.numVectorSpace, this)

    override fun contains(matrix: DenseMatrix<S>): Boolean {
        return matrix.numVectorSpace == this.numVectorSpace
    }

    override fun add(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw ArithmeticException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw ArithmeticException("The denseMatrix $second does not match the context ($this)")
        if (first.rowCount != second.rowCount || first.colCount != second.colCount)
            throw ArithmeticException("Cannot add matrices: different shapes")
        val values = first.values.zip(second.values).map { (rowInThis, rowInOther) ->
            rowInThis.zip(rowInOther).map { (elmInThis, elmInOther) -> this.field.withContext { elmInThis + elmInOther } }
        }
        return this.fromRows(values)
    }

    override fun subtract(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw ArithmeticException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw ArithmeticException("The denseMatrix $second does not match the context ($this)")
        return this.withContext {
            first + (-1) * second
        }
    }

    override fun multiply(first: DenseMatrix<S>, second: DenseMatrix<S>): DenseMatrix<S> {
        if (first !in this)
            throw ArithmeticException("The denseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw ArithmeticException("The denseMatrix $second does not match the context ($this)")
        if (first.colCount != second.rowCount)
            throw ArithmeticException("Cannot multiply matrices: first.colCount != second.rowCount")
        val rowRange = 0 until first.rowCount
        val sumRange = 0 until first.colCount
        val colRange = 0 until second.colCount
        val values = rowRange.map { i ->
            colRange.map { j ->
                sumRange
                    .map { k -> this.field.withContext { first.values[i][k] * second.values[k][j] } }
                    .reduce(this.field::add)
            }
        }
        return this.fromRows(values)
    }

    override fun multiply(matrix: DenseMatrix<S>, scalar: S): DenseMatrix<S> {
        if (matrix !in this)
            throw ArithmeticException("The denseMatrix $matrix does not match the context ($this)")
        if (scalar !in this.field)
            throw ArithmeticException("The scalar $scalar does not match the context (${this.field})")
        val values = matrix.values.map { row -> row.map { elm -> this.field.withContext { -elm } } }
        return this.fromRows(values)
    }

    override fun multiply(matrix: DenseMatrix<S>, numVector: DenseNumVector<S>): DenseNumVector<S> {
        if (matrix !in this)
            throw ArithmeticException("The denseMatrix $matrix does not match the context ($this)")
        if (numVector !in this.numVectorSpace)
            throw ArithmeticException("The numVector $numVector does not match the context (${this.numVectorSpace})")
        if (matrix.colCount != numVector.dim)
            throw ArithmeticException("Cannot multiply matrix and vector: matrix.colCount != vector.dim")
        val values = matrix.values.map { row ->
            row.zip(numVector.values)
                .map { this.field.withContext { it.first * it.second } }
                .reduce { a, b -> this.field.withContext { a + b } }
        }
        return this.numVectorSpace.fromValues(values)
    }

    override fun computeRowEchelonForm(matrix: DenseMatrix<S>): RowEchelonForm<S, DenseNumVector<S>, DenseMatrix<S>> {
        return DenseRowEchelonForm(this, matrix)
    }

    override fun computeTranspose(matrix: DenseMatrix<S>): DenseMatrix<S> {
        val rowCount = matrix.colCount
        val colCount = matrix.rowCount
        val values: List<List<S>> = this.withContext {
            (0 until rowCount).map { i ->
                (0 until colCount).map { j -> matrix[j, i] }
            }
        }
        return DenseMatrix(
            this.numVectorSpace,
            rowCount = rowCount,
            colCount = colCount,
            values = values
        )
    }

    override fun computeInnerProduct(
        matrix: DenseMatrix<S>,
        numVector1: DenseNumVector<S>,
        numVector2: DenseNumVector<S>
    ): S {
        return this.withContext {
            numVector1 dot (matrix * numVector2)
        }
    }

    override fun fromRows(rows: List<List<S>>): DenseMatrix<S> {
        if (rows.isEmpty())
            throw IllegalArgumentException("Row list is empty, which is not supported")
        return DenseMatrix(this.numVectorSpace, rows, rows.size, rows[0].size)
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
        return DenseMatrix(this.numVectorSpace, values, rowCount, colCount)
    }

    override fun getElement(matrix: DenseMatrix<S>, rowInd: Int, colInd: Int): S {
        return matrix.values[rowInd][colInd]
    }
}
