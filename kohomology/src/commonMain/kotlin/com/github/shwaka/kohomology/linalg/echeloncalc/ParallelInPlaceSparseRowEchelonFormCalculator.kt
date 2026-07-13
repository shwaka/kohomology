package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.cancel.CancellationContext
import com.github.shwaka.kohomology.util.exchange
import com.github.shwaka.kohomology.util.parallel.ParallelConfig
import com.github.shwaka.kohomology.util.parallel.parallelMap

internal class ParallelInPlaceSparseRowEchelonFormCalculator<S : Scalar>(
    private val field: Field<S>,
    private val cancellationContext: CancellationContext?,
    private val parallelConfig: ParallelConfig = ParallelConfig(),
) : SparseRowEchelonFormCalculator<S> {
    override fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
        var currentColInd: Int = 0
        val pivots: MutableList<Int> = mutableListOf()
        var exchangeCount: Int = 0
        val currentMatrix: MutableMap<Int, MutableMap<Int, S>> = matrix.toMutableMapDeeply()
        while (currentColInd < colCount) {
            this.cancellationContext?.check()
            val rowInd: Int? = currentMatrix.findNonZero(currentColInd, pivots.size)
            if (rowInd == null) {
                currentColInd++
                continue
            } else {
                val pivotRowInd = pivots.size
                if (rowInd != pivotRowInd) {
                    currentMatrix.exchange(rowInd, pivotRowInd)
                    exchangeCount++
                }
                currentMatrix.eliminateRowsBelow(pivotRowInd, currentColInd)
                pivots.add(currentColInd)
                currentColInd++
            }
        }
        return SparseRowEchelonFormData(currentMatrix, pivots, exchangeCount)
    }

    override fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
        val rank = pivots.size
        val reducedRowMap = rowEchelonRowMap.toMutableMapDeeply()
        val normalizedRows = parallelMap(reducedRowMap.toList(), this.parallelConfig) { (i, row) ->
            val elm: S = row[pivots[i]] ?: throw Exception("This can't happen!")
            val elmInv = this.field.context.run {
                elm.inv()
            }
            Pair(i, row.multiplied(elmInv))
        }
        reducedRowMap.clear()
        for ((i, row) in normalizedRows) {
            if (row.isNotEmpty()) {
                reducedRowMap[i] = row
            }
        }
        for (i in 0 until rank) {
            reducedRowMap.eliminateRowsAboveWithNormalizedPivot(i, pivots[i])
        }
        return reducedRowMap
    }

    private fun <K, L, S> Map<K, Map<L, S>>.toMutableMapDeeply(): MutableMap<K, MutableMap<L, S>> {
        return this.mapValues { (_, row) -> row.toMutableMap() }.toMutableMap()
    }

    private data class RowUpdate<S : Scalar>(
        val rowInd: Int,
        val row: MutableMap<Int, S>,
    )

    private fun MutableMap<Int, S>.subtract(other: Map<Int, S>, scalar: S) {
        this@ParallelInPlaceSparseRowEchelonFormCalculator.field.context.run {
            for ((i, value) in other) {
                when (val valueFromThis: S? = this@subtract[i]) {
                    null -> this@subtract[i] = -value * scalar
                    else -> {
                        val newValue =
                            this@ParallelInPlaceSparseRowEchelonFormCalculator
                                .field
                                .subtractProduct(valueFromThis, value, scalar)
                        if (newValue.isZero()) {
                            this@subtract.remove(i)
                        } else {
                            this@subtract[i] = newValue
                        }
                    }
                }
            }
        }
    }

    private fun Map<Int, S>.multiplied(scalar: S): MutableMap<Int, S> {
        if (scalar.isZero()) {
            return mutableMapOf()
        }
        return this@ParallelInPlaceSparseRowEchelonFormCalculator.field.context.run {
            this@multiplied.mapValues { (_, value) -> value * scalar }.toMutableMap()
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateRowsBelow(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        val targets = this.entries
            .filter { (i, row) -> i > rowInd && row[colInd] != null }
            .map { (i, row) -> Pair(i, row.toMap()) }
        val updates = parallelMap(targets, this@ParallelInPlaceSparseRowEchelonFormCalculator.parallelConfig) { (i, row) ->
            this@ParallelInPlaceSparseRowEchelonFormCalculator.cancellationContext?.check()
            val coeff: S = row[colInd] ?: throw Exception("This can't happen!")
            val newRow = this@ParallelInPlaceSparseRowEchelonFormCalculator.field.context.run {
                row.toMutableMap().also { mutableRow ->
                    mutableRow.subtract(mainRow, coeff / elm)
                }
            }
            RowUpdate(i, newRow)
        }
        for ((i, row) in updates) {
            if (row.isEmpty()) {
                this.remove(i)
            } else {
                this[i] = row
            }
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateRowsAboveWithNormalizedPivot(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        val targets = this.entries
            .filter { (i, row) -> i < rowInd && row[colInd] != null }
            .map { (i, row) -> Pair(i, row.toMap()) }
        val updates = parallelMap(targets, this@ParallelInPlaceSparseRowEchelonFormCalculator.parallelConfig) { (i, row) ->
            this@ParallelInPlaceSparseRowEchelonFormCalculator.cancellationContext?.check()
            val coeff: S = row[colInd] ?: throw Exception("This can't happen!")
            val newRow = row.toMutableMap().also { mutableRow ->
                mutableRow.subtract(mainRow, coeff)
            }
            RowUpdate(i, newRow)
        }
        for ((i, row) in updates) {
            if (row.isEmpty()) {
                this.remove(i)
            } else {
                this[i] = row
            }
        }
    }

    private fun Map<Int, Map<Int, S>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        for ((i, row) in this) {
            if (i >= rowIndFrom) {
                row[colInd]?.let { elm ->
                    if (elm.isNotZero())
                        return i
                }
            }
        }
        return null
    }
}
