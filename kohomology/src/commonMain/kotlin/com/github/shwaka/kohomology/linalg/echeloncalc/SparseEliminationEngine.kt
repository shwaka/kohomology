package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.cancel.CancellationContext

internal class SparseEliminationEngine<S : Scalar>(
    private val field: Field<S>,
    matrix: Map<Int, Map<Int, S>>,
    private val cancellationContext: CancellationContext?,
) {
    private val rows: MutableMap<Int, MutableMap<Int, S>> = matrix
        .mapValues { (_, row) -> row.toMutableMap() }
        .toMutableMap()
    private val cols: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    init {
        for ((rowIndex, row) in this.rows) {
            for (colIndex in row.keys) {
                this.cols.getOrPut(colIndex) { mutableSetOf() }.add(rowIndex)
            }
        }
    }

    fun computeRowEchelonForm(colCount: Int): SparseRowEchelonFormData<S> {
        val pivots: MutableList<Int> = mutableListOf()
        var exchangeCount = 0
        for (colIndex in 0 until colCount) {
            this.cancellationContext?.check()
            val pivotRowIndex = pivots.size
            val rowIndex = this.findPivotRow(colIndex, pivotRowIndex) ?: continue
            if (rowIndex != pivotRowIndex) {
                this.exchangeRows(rowIndex, pivotRowIndex)
                exchangeCount++
            }
            this.eliminateRowsBelow(pivotRowIndex, colIndex)
            pivots.add(colIndex)
        }
        return SparseRowEchelonFormData(this.rows, pivots, exchangeCount)
    }

    fun reduce(pivots: List<Int>): Map<Int, Map<Int, S>> {
        for ((rowIndex, pivot) in pivots.withIndex()) {
            val row = this.rows[rowIndex] ?: throw Exception("This can't happen!")
            val elm = row[pivot] ?: throw Exception("This can't happen!")
            val elmInv = this.field.context.run { elm.inv() }
            this.multiplyRow(rowIndex, elmInv)
        }
        for ((rowIndex, pivot) in pivots.withIndex()) {
            this.eliminateRowsAboveWithNormalizedPivot(rowIndex, pivot)
        }
        return this.rows
    }

    private fun findPivotRow(colIndex: Int, rowIndexFrom: Int): Int? {
        return this.cols[colIndex]
            ?.asSequence()
            ?.filter { rowIndex -> rowIndex >= rowIndexFrom }
            ?.minOrNull()
    }

    private fun exchangeRows(rowIndex1: Int, rowIndex2: Int) {
        val row1 = this.rows[rowIndex1]
        val row2 = this.rows[rowIndex2]
        if (row2 == null) {
            this.rows.remove(rowIndex1)
        } else {
            this.rows[rowIndex1] = row2
        }
        if (row1 == null) {
            this.rows.remove(rowIndex2)
        } else {
            this.rows[rowIndex2] = row1
        }
        val affectedCols = buildSet {
            row1?.keys?.let { this.addAll(it) }
            row2?.keys?.let { this.addAll(it) }
        }
        for (colIndex in affectedCols) {
            val rowSet = this.cols[colIndex] ?: continue
            val hasRow1 = rowSet.remove(rowIndex1)
            val hasRow2 = rowSet.remove(rowIndex2)
            if (hasRow1) {
                rowSet.add(rowIndex2)
            }
            if (hasRow2) {
                rowSet.add(rowIndex1)
            }
        }
    }

    private fun eliminateRowsBelow(rowIndex: Int, colIndex: Int) {
        val mainRow = this.rows[rowIndex]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowIndex is zero")
        val elm = mainRow[colIndex]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowIndex, $colIndex) is zero")
        val targetRowIndices = this.cols[colIndex]
            ?.filter { targetRowIndex -> targetRowIndex > rowIndex }
            ?: emptyList()
        for (targetRowIndex in targetRowIndices) {
            this.cancellationContext?.check()
            val targetRow = this.rows[targetRowIndex] ?: continue
            val coeff = targetRow[colIndex] ?: continue
            this.field.context.run {
                this@SparseEliminationEngine.subtractMultiple(targetRowIndex, mainRow, coeff / elm)
            }
        }
    }

    private fun eliminateRowsAboveWithNormalizedPivot(rowIndex: Int, colIndex: Int) {
        val mainRow = this.rows[rowIndex]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowIndex is zero")
        val elm = mainRow[colIndex]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowIndex, $colIndex) is zero")
        val targetRowIndices = this.cols[colIndex]
            ?.filter { targetRowIndex -> targetRowIndex < rowIndex }
            ?: emptyList()
        for (targetRowIndex in targetRowIndices) {
            this.cancellationContext?.check()
            val targetRow = this.rows[targetRowIndex] ?: continue
            val coeff = targetRow[colIndex] ?: continue
            this.subtractMultiple(targetRowIndex, mainRow, coeff)
        }
    }

    private fun subtractMultiple(rowIndex: Int, other: Map<Int, S>, scalar: S) {
        val row = this.rows[rowIndex] ?: throw Exception("This can't happen!")
        this.field.context.run {
            for ((colIndex, value) in other) {
                val oldValue = row[colIndex]
                val newValue = when (oldValue) {
                    null -> -value * scalar
                    else -> this@SparseEliminationEngine.field.subtractProduct(oldValue, value, scalar)
                }
                this@SparseEliminationEngine.setValue(rowIndex, colIndex, newValue)
            }
        }
        if (row.isEmpty()) {
            this.rows.remove(rowIndex)
        }
    }

    private fun multiplyRow(rowIndex: Int, scalar: S) {
        val row = this.rows[rowIndex] ?: throw Exception("This can't happen!")
        if (scalar.isZero()) {
            for (colIndex in row.keys.toList()) {
                this.setValue(rowIndex, colIndex, this.field.zero)
            }
            this.rows.remove(rowIndex)
            return
        }
        val entries = row.toList()
        this.field.context.run {
            for ((colIndex, value) in entries) {
                this@SparseEliminationEngine.setValue(rowIndex, colIndex, value * scalar)
            }
        }
    }

    private fun setValue(rowIndex: Int, colIndex: Int, value: S) {
        val row = this.rows.getOrPut(rowIndex) { mutableMapOf() }
        val hadValue = row.containsKey(colIndex)
        if (value.isZero()) {
            if (hadValue) {
                row.remove(colIndex)
                this.cols[colIndex]?.remove(rowIndex)
            }
        } else {
            row[colIndex] = value
            if (!hadValue) {
                this.cols.getOrPut(colIndex) { mutableSetOf() }.add(rowIndex)
            }
        }
    }
}
