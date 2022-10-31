package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixContext
import com.github.shwaka.kohomology.linalg.MatrixContextImpl
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.StringTable
import com.github.shwaka.kohomology.util.isOdd

public class SetMatrix<S : Scalar>(
    override val numVectorSpace: SetNumVectorSpace<S>,
    // characteristic for SetNumVectorSpace is always 2
    rowSetMap: Map<Int, Set<Int>>,
    override val rowCount: Int,
    override val colCount: Int,
) : Matrix<S, SetNumVector<S>> {
    public val rowSetMap: Map<Int, Set<Int>> = rowSetMap.filterValues { it.isNotEmpty() }

    override fun isZero(): Boolean {
        return this.rowSetMap.isEmpty()
    }

    override fun isIdentity(): Boolean {
        return (this.rowCount == this.colCount) &&
            (this.rowSetMap.size == this.rowCount) &&
            this.rowSetMap.all { (rowInd, row) ->
                row.size == 1 && row.first() == rowInd
            }
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
        if ((rowInd < 0) || (colInd < 0))
            throw IndexOutOfBoundsException("Index for matrix cannot be negative")
        if ((rowInd >= this.rowCount) || (colInd >= this.colCount))
            throw IndexOutOfBoundsException(
                "Given index ($rowInd, $colInd) is not contained in the size (${this.rowCount}, ${this.colCount})"
            )
        val row: Set<Int> = this.rowSetMap[rowInd]
            ?: return this.numVectorSpace.field.zero
        return if (row.contains(colInd)) {
            this.numVectorSpace.field.one
        } else {
            this.numVectorSpace.field.zero
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SetMatrix<*>

        if (numVectorSpace != other.numVectorSpace) return false
        if (rowCount != other.rowCount) return false
        if (colCount != other.colCount) return false
        if (rowSetMap != other.rowSetMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVectorSpace.hashCode()
        result = 31 * result + rowCount
        result = 31 * result + colCount
        result = 31 * result + rowSetMap.hashCode()
        return result
    }
}

public class SetMatrixSpace<S : Scalar> private constructor(
    override val numVectorSpace: SetNumVectorSpace<S>
) : MatrixSpace<S, SetNumVector<S>, SetMatrix<S>> {
    public companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<SetNumVectorSpace<*>, SetMatrixSpace<*>> = mutableMapOf()
        public fun <S : Scalar> from(numVectorSpace: SetNumVectorSpace<S>): SetMatrixSpace<S> {
            if (this.cache.containsKey(numVectorSpace)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[numVectorSpace] as SetMatrixSpace<S>
            } else {
                val matrixSpace = SetMatrixSpace(numVectorSpace)
                this.cache[numVectorSpace] = matrixSpace
                return matrixSpace
            }
        }
    }
    override val field: Field<S> =
        numVectorSpace.field

    override val context: MatrixContext<S, SetNumVector<S>, SetMatrix<S>> =
        MatrixContextImpl(this)

    override fun contains(matrix: SetMatrix<S>): Boolean {
        return matrix.numVectorSpace == this.numVectorSpace
    }

    override fun add(first: SetMatrix<S>, second: SetMatrix<S>): SetMatrix<S> {
        require(first in this) {
            "The setMatrix $first does not match the context ($this)"
        }
        require(second in this) {
            "The setMatrix $second does not match the context ($this)"
        }
        require(first.rowCount == second.rowCount && first.colCount == second.colCount) {
            "Cannot add matrices: different shapes"
        }
        val newRowSetMap: MutableMap<Int, Set<Int>> = first.rowSetMap.toMutableMap()
        for ((rowInd, row) in second.rowSetMap) {
            val newRow: Set<Int>? = newRowSetMap[rowInd]
            if (newRow == null) {
                newRowSetMap[rowInd] = row
            } else {
                newRowSetMap[rowInd] = newRow xor row
            }
        }
        return SetMatrix(this.numVectorSpace, newRowSetMap, first.rowCount, first.colCount)
    }

    override fun subtract(first: SetMatrix<S>, second: SetMatrix<S>): SetMatrix<S> {
        // Since characteristic 2
        return this.add(first, second)
    }

    override fun multiply(first: SetMatrix<S>, second: SetMatrix<S>): SetMatrix<S> {
        require(first in this) {
            "The setMatrix $first does not match the context ($this)"
        }
        require(second in this) {
            "The setMatrix $second does  not match the context ($this)"
        }
        require(first.colCount == second.rowCount) {
            "Cannot multiply matrices: first.colCount != second.rowCount"
        }
        val rowSetMap = first.rowSetMap.mapValues { (_, row1) ->
            (0 until first.colCount).mapNotNull { colInd ->
                val count = row1.map { sumInd ->
                    second.rowSetMap[sumInd]?.contains(colInd) ?: false
                }.filter { it }.size
                if (count.isOdd()) {
                    colInd
                } else {
                    null
                }
            }.toSet()
        }
        return SetMatrix(this.numVectorSpace, rowSetMap, first.rowCount, second.colCount)
    }

    override fun multiply(matrix: SetMatrix<S>, scalar: S): SetMatrix<S> {
        return if (scalar.isZero()) {
            this.getZero(matrix.rowCount, matrix.colCount)
        } else {
            matrix
        }
    }

    override fun multiply(matrix: SetMatrix<S>, numVector: SetNumVector<S>): SetNumVector<S> {
        require(matrix in this) {
            "The setMatrix $matrix does not match the context ($this)"
        }
        require(numVector in this.numVectorSpace) {
            "The numVector $numVector does not match the context (${this.numVectorSpace})"
        }
        require(matrix.colCount == numVector.dim) {
            "Cannot multiply matrix and vector: matrix.colCount != vector.dim"
        }
        val valueSet = matrix.rowSetMap.mapNotNull { (rowInd, row) ->
            val count = (row intersect numVector.valueSet).size
            if (count.isOdd()) {
                rowInd
            } else {
                null
            }
        }.toSet()
        return SetNumVector(valueSet, this.field, matrix.rowCount)
    }

    override fun computeTranspose(matrix: SetMatrix<S>): SetMatrix<S> {
        val rowCount = matrix.colCount
        val colCount = matrix.rowCount
        val rowSetMap: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
        for ((rowInd, row) in matrix.rowSetMap) {
            for (colInd in row) {
                val newRow = rowSetMap.getOrPut(colInd) { mutableSetOf() }
                newRow.add(rowInd)
            }
        }
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCount)
    }

    override fun fromRowList(rowList: List<List<S>>, colCount: Int?): SetMatrix<S> {
        val rowCount = rowList.size
        val colCountNonNull: Int = when {
            rowList.isNotEmpty() -> rowList[0].size
            colCount != null -> colCount
            else -> throw IllegalArgumentException("Row list is empty and colCount is not specified")
        }
        val rowSetMap: Map<Int, Set<Int>> = rowList.mapIndexedNotNull { rowInd, row ->
            val newRow: Set<Int> = row.mapIndexedNotNull { colInd, elm ->
                if (elm.isZero()) {
                    null
                } else {
                    colInd
                }
            }.toSet()
            if (newRow.isEmpty()) {
                null
            } else {
                Pair(rowInd, newRow)
            }
        }.toMap()
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCountNonNull)
    }

    override fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): SetMatrix<S> {
        val rowSetMap: Map<Int, Set<Int>> = rowMap.mapValues { (_, row) -> row.keys.toSet() }
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCount)
    }

    override fun joinMatrices(matrix1: SetMatrix<S>, matrix2: SetMatrix<S>): SetMatrix<S> {
        require(matrix1.rowCount == matrix2.rowCount) {
            "Cannot join two matrices of different row counts"
        }
        val rowSetMap: MutableMap<Int, Set<Int>> = matrix1.rowSetMap.toMutableMap()
        for ((rowInd2, row2) in matrix2.rowSetMap) {
            val row1 = matrix1.rowSetMap.getOrElse(rowInd2) { setOf() }
            val newRow = row1 union row2.map { colInd -> colInd + matrix1.colCount }
            rowSetMap[rowInd2] = newRow
        }
        val rowCount = matrix1.rowCount
        val colCount = matrix1.colCount + matrix2.colCount
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCount)
    }

    override fun computeRowSlice(matrix: SetMatrix<S>, rowRange: IntRange): SetMatrix<S> {
        val rowCount = rowRange.count()
        val colCount = matrix.colCount
        val rowSetMap = matrix.rowSetMap.filterKeys { rowInd -> rowInd in rowRange }
            .mapKeys { (rowInd, _) -> rowInd - rowRange.first }
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCount)
    }

    override fun computeColSlice(matrix: SetMatrix<S>, colRange: IntRange): SetMatrix<S> {
        val rowCount = matrix.rowCount
        val colCount = colRange.count()
        val rowSetMap = matrix.rowSetMap.mapValues { (_, row) ->
            row.filter { colInd -> colInd in colRange }
                .map { colInd -> colInd - colRange.first }
                .toSet()
        }.filterValues { row -> row.isNotEmpty() }
        return SetMatrix(this.numVectorSpace, rowSetMap, rowCount, colCount)
    }

    override fun computeRowEchelonForm(matrix: SetMatrix<S>): RowEchelonForm<S, SetNumVector<S>, SetMatrix<S>> {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "SetMatrixSpace(${this.field})"
    }
}
