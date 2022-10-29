package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixContext
import com.github.shwaka.kohomology.linalg.MatrixContextImpl
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.StringTable

public class SetMatrix<S : Scalar>(
    override val numVectorSpace: SetNumVectorSpace<S>,
    // characteristic for SetNumVectorSpace is always 2
    rowMap: Map<Int, Set<Int>>,
    override val rowCount: Int,
    override val colCount: Int,
) : Matrix<S, SetNumVector<S>> {
    public val rowMap: Map<Int, Set<Int>> = rowMap.filterValues { it.isNotEmpty() }

    override fun isZero(): Boolean {
        return this.rowMap.isEmpty()
    }

    override fun isIdentity(): Boolean {
        return (this.rowCount == this.colCount) &&
            this.rowMap.all { (rowInd, row) ->
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
        val row: Set<Int> = this.rowMap[rowInd]
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
        if (rowMap != other.rowMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVectorSpace.hashCode()
        result = 31 * result + rowCount
        result = 31 * result + colCount
        result = 31 * result + rowMap.hashCode()
        return result
    }
}

public class SetMatrixSpace<S : Scalar>(
    override val numVectorSpace: SetNumVectorSpace<S>
) : MatrixSpace<S, SetNumVector<S>, SetMatrix<S>> {
    override val field: Field<S> = numVectorSpace.field

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
        val newRowMap: MutableMap<Int, Set<Int>> = first.rowMap.toMutableMap()
        for ((rowInd, row) in second.rowMap) {
            val newRow: Set<Int>? = newRowMap[rowInd]
            if (newRow == null) {
                newRowMap[rowInd] = row
            } else {
                newRowMap[rowInd] = newRow xor row
            }
        }
        return SetMatrix(this.numVectorSpace, newRowMap, first.rowCount, first.colCount)
    }

    override fun subtract(first: SetMatrix<S>, second: SetMatrix<S>): SetMatrix<S> {
        return this.add(first, second)
    }

    override fun multiply(first: SetMatrix<S>, second: SetMatrix<S>): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SetMatrix<S>, scalar: S): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun multiply(matrix: SetMatrix<S>, numVector: SetNumVector<S>): SetNumVector<S> {
        TODO("Not yet implemented")
    }

    override fun computeTranspose(matrix: SetMatrix<S>): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun fromRowList(rowList: List<List<S>>, colCount: Int?): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun joinMatrices(matrix1: SetMatrix<S>, matrix2: SetMatrix<S>): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowSlice(matrix: SetMatrix<S>, rowRange: IntRange): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeColSlice(matrix: SetMatrix<S>, colRange: IntRange): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computeRowEchelonForm(matrix: SetMatrix<S>): RowEchelonForm<S, SetNumVector<S>, SetMatrix<S>> {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "SetMatrixSpace(${this.field})"
    }
}
