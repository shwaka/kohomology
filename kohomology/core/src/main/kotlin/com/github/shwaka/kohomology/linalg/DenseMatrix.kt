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

    override operator fun get(rowInd: Int, colInd: Int): S {
        return this.values[rowInd][colInd]
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

    override val field: Field<S> = this.numVectorSpace.field

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
                this.field.withContext {
                    sumRange
                        .map { k -> first.values[i][k] * second.values[k][j] }
                        .fold(zero) { a, b -> a + b }
                }
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
            this.field.withContext {
                row.zip(numVector.values)
                    .map { it.first * it.second }
                    .fold(zero) { a, b -> a + b }
            }
        }
        return this.numVectorSpace.fromValues(values)
    }

    override fun computeRowEchelonForm(matrix: DenseMatrix<S>): RowEchelonForm<S, DenseNumVector<S>, DenseMatrix<S>> {
        return DenseRowEchelonForm(this, matrix)
    }

    override fun computeTranspose(matrix: DenseMatrix<S>): DenseMatrix<S> {
        val rowCount = matrix.colCount
        val colCount = matrix.rowCount
        val values: List<List<S>> = (0 until rowCount).map { i ->
            (0 until colCount).map { j -> matrix[j, i] }
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

    override fun fromRows(rows: List<List<S>>, colCount: Int?): DenseMatrix<S> {
        val rowCount = rows.size
        val colCountNonNull: Int = when {
            rows.isNotEmpty() -> rows[0].size
            colCount != null -> colCount
            else -> throw IllegalArgumentException("Row list is empty and colCount is not specified")
        }
        return DenseMatrix(this.numVectorSpace, rows, rowCount, colCountNonNull)
    }

    override fun fromCols(cols: List<List<S>>, rowCount: Int?): DenseMatrix<S> {
        val rowCountNonNull: Int = when {
            cols.isNotEmpty() -> cols[0].size
            rowCount != null -> rowCount
            else -> throw IllegalArgumentException("Column list is empty and rowCount is not specified")
        }
        val colCount = cols.size
        val rows = (0 until rowCountNonNull).map { i -> (0 until colCount).map { j -> cols[j][i] } }
        return this.fromRows(rows, colCount)
    }

    override fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): DenseMatrix<S> {
        if (list.size != rowCount * colCount)
            throw IllegalArgumentException("The size of the list should be equal to rowCount * colCount")
        val values = (0 until rowCount).map { i -> list.subList(colCount * i, colCount * (i + 1)) }
        return DenseMatrix(this.numVectorSpace, values, rowCount, colCount)
    }

    private fun joinMatrices(matrix1: DenseMatrix<S>, matrix2: DenseMatrix<S>): DenseMatrix<S> {
        if (matrix1.rowCount != matrix2.rowCount)
            throw IllegalArgumentException("Cannot join two matrices of different row counts")
        val values = matrix1.values.zip(matrix2.values).map { (row1: List<S>, row2: List<S>) -> row1 + row2 }
        val rowCount = matrix1.rowCount
        val colCount = matrix1.colCount + matrix2.colCount
        return DenseMatrix(this.numVectorSpace, values, rowCount, colCount)
    }

    override fun joinMatrices(matrixList: List<DenseMatrix<S>>): DenseMatrix<S> {
        if (matrixList.isEmpty())
            throw IllegalArgumentException("Empty list of matrices cannot be reduced")
        return matrixList.reduce { matrix1, matrix2 -> this.joinMatrices(matrix1, matrix2) }
    }

    override fun computeRowSlice(matrix: DenseMatrix<S>, rowRange: IntRange): DenseMatrix<S> {
        val values = matrix.values.slice(rowRange)
        return this.fromRows(values, colCount = matrix.colCount)
    }

    override fun computeColSlice(matrix: DenseMatrix<S>, colRange: IntRange): DenseMatrix<S> {
        val values = matrix.values.map { row -> row.slice(colRange)}
        return this.fromRows(values, colCount = colRange.count())
    }
}
