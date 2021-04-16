package com.github.shwaka.kohomology.linalg

internal data class RowEchelonFormData<S : Scalar>(
    val rowMap: Map<Int, Map<Int, S>>,
    val pivots: List<Int>,
    val exchangeCount: Int
)

internal class SparseRowEchelonFormCalculator<S : Scalar>(private val field: Field<S>) {
    fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): RowEchelonFormData<S> {
        return matrix.rowEchelonFormInternal(0, listOf(), 0, colCount)
    }

    fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
        val rank = pivots.size
        var reducedRowMap = rowEchelonRowMap.mapValues { (i, row) ->
            val elm: S = row[pivots[i]] ?: throw Exception("This can't happen!")
            this.field.context.run {
                row * elm.inv()
            }
        }
        for (i in 0 until rank) {
            reducedRowMap = reducedRowMap.eliminateOtherRows(i, pivots[i])
        }
        return reducedRowMap
    }

    private fun Map<Int, Map<Int, S>>.rowEchelonFormInternal(
        currentColInd: Int,
        pivots: List<Int>,
        exchangeCount: Int,
        colCount: Int
    ): RowEchelonFormData<S> {
        if (this.isEmpty()) {
            // 0 行の行列だった場合
            return RowEchelonFormData(this, emptyList(), 0)
        }
        if (currentColInd == colCount) {
            // 全ての列の処理が終わった場合
            return RowEchelonFormData(this, pivots, exchangeCount)
        }
        val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
        return if (rowInd == null) {
            this.rowEchelonFormInternal(currentColInd + 1, pivots, exchangeCount, colCount)
        } else {
            var newRowMap = this.eliminateOtherRows(rowInd, currentColInd)
            var newExchangeCount = exchangeCount
            if (rowInd != pivots.size) {
                newRowMap = newRowMap.exchangeRows(rowInd, pivots.size)
                newExchangeCount += 1
            }
            val newPivots = pivots + listOf(currentColInd)
            newRowMap.rowEchelonFormInternal(currentColInd + 1, newPivots, newExchangeCount, colCount)
        }
    }

    private fun Map<Int, Map<Int, S>>.exchangeRows(i1: Int, i2: Int): Map<Int, Map<Int, S>> {
        if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
        return this.mapKeys { (i, _) ->
            when (i) {
                i1 -> i2
                i2 -> i1
                else -> i
            }
        }
    }

    private operator fun Map<Int, S>.minus(other: Map<Int, S>): Map<Int, S> {
        val newMap: MutableMap<Int, S> = this.toMutableMap()
        this@SparseRowEchelonFormCalculator.field.context.run {
            for ((i, value) in other) {
                when (val valueFromThis: S? = newMap[i]) {
                    null -> newMap[i] = -value
                    else -> newMap[i] = valueFromThis - value
                }
            }
        }
        return newMap.filterValues { it.isNotZero() }
    }

    private operator fun Map<Int, S>.times(scalar: S): Map<Int, S> {
        if (scalar.isZero())
            return mapOf()
        return this@SparseRowEchelonFormCalculator.field.context.run {
            this@times.mapValues { (_, value) -> value * scalar }
        }
    }

    private fun Map<Int, Map<Int, S>>.eliminateOtherRows(rowInd: Int, colInd: Int): Map<Int, Map<Int, S>> {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        val zero = this@SparseRowEchelonFormCalculator.field.zero
        return this@SparseRowEchelonFormCalculator.field.context.run {
            this@eliminateOtherRows.mapValues { (i, row) ->
                when (i) {
                    rowInd -> row
                    else -> row - mainRow * (row.getOrElse(colInd) { zero } / elm)
                }
            }.filterValues { row -> row.isNotEmpty() }
        }
    }

    private fun Map<Int, Map<Int, S>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        for (i in this.keys.filter { it >= rowIndFrom }) {
            this[i]?.let { row ->
                row[colInd]?.let { elm ->
                    if (elm.isNotZero())
                        return i
                }
            }
        }
        return null
    }
}
