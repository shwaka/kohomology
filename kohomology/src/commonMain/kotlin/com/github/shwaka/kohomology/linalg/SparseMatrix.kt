package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.StringTable

data class SparseMatrix<S : Scalar>(
    override val numVectorSpace: SparseNumVectorSpace<S>,
    val rowMap: Map<Int, Map<Int, S>>,
    override val rowCount: Int,
    override val colCount: Int,
) : Matrix<S, SparseNumVector<S>> {
    init {
        // TODO: check that each index is smaller than rowCount or colCount
    }

    private fun toStringTable(): StringTable {
        val valueList = (0 until this.rowCount).map { rowInd ->
            (0 until this.colCount).map { colInd ->
                this[rowInd, colInd].toString()
            }
        }
        return StringTable(valueList)
    }

    override fun toPrettyString(): String {
        return this.toStringTable().toPrettyString()
    }

    override fun toString(): String {
        return this.toStringTable().toString()
    }

    override fun get(rowInd: Int, colInd: Int): S {
        this.rowMap[rowInd]?.let { row ->
            row[colInd]?.let { return it }
        }
        return this.numVectorSpace.field.zero
    }
}

class SparseMatrixSpace<S : Scalar>(
    override val numVectorSpace: SparseNumVectorSpace<S>
) : MatrixSpace<S, SparseNumVector<S>, SparseMatrix<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<SparseNumVectorSpace<*>, SparseMatrixSpace<*>> = mutableMapOf()
        fun <S : Scalar> from(numVectorSpace: SparseNumVectorSpace<S>): SparseMatrixSpace<S> {
            if (this.cache.containsKey(numVectorSpace)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[numVectorSpace] as SparseMatrixSpace<S>
            } else {
                val matrixSpace = SparseMatrixSpace(numVectorSpace)
                this.cache[numVectorSpace] = matrixSpace
                return matrixSpace
            }
        }
    }

    override val field: Field<S> = this.numVectorSpace.field

    override val context = MatrixContext(this.field, this.numVectorSpace, this)

    override fun contains(matrix: SparseMatrix<S>): Boolean {
        return matrix.numVectorSpace == this.numVectorSpace
    }

    override fun add(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        if (first !in this)
            throw ArithmeticException("The sparseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw ArithmeticException("The sparseMatrix $second does not match the context ($this)")
        if (first.rowCount != second.rowCount || first.colCount != second.colCount)
            throw ArithmeticException("Cannot add matrices: different shapes")
        val newRowMap: MutableMap<Int, MutableMap<Int, S>> = first.rowMap.mapValues { (_, row) -> row.toMutableMap() }.toMutableMap()
        this.field.context.run {
            for ((rowInd, row) in second.rowMap) {
                val newRow = newRowMap.getOrPut(rowInd) { mutableMapOf() }
                for ((colInd, elm) in row) {
                    when (val temp: S? = newRow[colInd]) {
                        null -> newRow[colInd] = elm
                        else -> newRow[colInd] = temp + elm
                    }
                    if (newRow[colInd] == null)
                        newRow.remove(colInd)
                }
                if (newRow.isEmpty())
                    newRowMap.remove(rowInd)
            }
        }
        return SparseMatrix(this.numVectorSpace, newRowMap, first.rowCount, first.colCount)
    }

    override fun subtract(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        if (first !in this)
            throw ArithmeticException("The sparseMatrix $first does not match the context ($this)")
        if (second !in this)
            throw ArithmeticException("The sparseMatrix $second does not match the context ($this)")
        if (first.rowCount != second.rowCount || first.colCount != second.colCount)
            throw ArithmeticException("Cannot add matrices: different shapes")
        val newRowMap: MutableMap<Int, MutableMap<Int, S>> = first.rowMap.mapValues { (_, row) -> row.toMutableMap() }.toMutableMap()
        this.field.context.run {
            for ((rowInd, row) in second.rowMap) {
                val newRow = newRowMap.getOrPut(rowInd) { mutableMapOf() }
                for ((colInd, elm) in row) {
                    when (val temp: S? = newRow[colInd]) {
                        null -> newRow[colInd] = -elm
                        else -> newRow[colInd] = temp - elm
                    }
                    if (newRow[colInd] == null)
                        newRow.remove(colInd)
                }
                if (newRow.isEmpty())
                    newRowMap.remove(rowInd)
            }
        }
        return SparseMatrix(this.numVectorSpace, newRowMap, first.rowCount, first.colCount)
    }

    override fun multiply(first: SparseMatrix<S>, second: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SparseMatrix<S>, scalar: S): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SparseMatrix<S>, numVector: SparseNumVector<S>): SparseNumVector<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowEchelonForm(matrix: SparseMatrix<S>): RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>> {
        TODO("Not yet implemented")
    }

    override fun computeTranspose(matrix: SparseMatrix<S>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeInnerProduct(
        matrix: SparseMatrix<S>,
        numVector1: SparseNumVector<S>,
        numVector2: SparseNumVector<S>
    ): S {
        return this.context.run {
            numVector1 dot (matrix * numVector2)
        }
    }

    override fun fromRows(rows: List<List<S>>, colCount: Int?): SparseMatrix<S> {
        val rowCount = rows.size
        val colCountNonNull: Int = when {
            rows.isNotEmpty() -> rows[0].size
            colCount != null -> colCount
            else -> throw IllegalArgumentException("Row list is empty and colCount is not specified")
        }
        val zero = this.field.zero
        val rowMap: Map<Int, Map<Int, S>> = rows.mapIndexedNotNull() { rowInd, row ->
            val newRow = row.mapIndexedNotNull { colInd, elm -> if (elm == zero) null else Pair(colInd, elm) }.toMap()
            if (newRow.isEmpty())
                null
            else
                Pair(rowInd, newRow)
        }.toMap()
        return SparseMatrix(this.numVectorSpace, rowMap, rowCount, colCountNonNull)

    }

    override fun fromCols(cols: List<List<S>>, rowCount: Int?): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun joinMatrices(matrixList: List<SparseMatrix<S>>): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowSlice(matrix: SparseMatrix<S>, rowRange: IntRange): SparseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeColSlice(matrix: SparseMatrix<S>, colRange: IntRange): SparseMatrix<S> {
        TODO("Not yet implemented")
    }
}
