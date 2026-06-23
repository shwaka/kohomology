package com.github.shwaka.kohomology.linalg.echeloncalc

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.exchange

internal class InPlaceSparseRowEchelonFormCalculator<S : Scalar>(
    private val field: Field<S>
) : SparseRowEchelonFormCalculator<S> {
    override fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
        var currentColInd: Int = 0
        val pivots: MutableList<Int> = mutableListOf()
        var exchangeCount: Int = 0
        val currentMatrix: MutableMap<Int, MutableMap<Int, S>> = matrix.toMutableMapDeeply()
        while (currentColInd < colCount) {
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
                // Row echelon form only needs zeros below each pivot.
                // Clearing entries above pivots is deferred to reduce(), which computes the reduced form.
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
        for ((i, row) in reducedRowMap) {
            val elm: S = row[pivots[i]] ?: throw Exception("This can't happen!")
            val elmInv = this.field.context.run {
                elm.inv()
            }
            row.multiply(elmInv)
        }
        for (i in 0 until rank) {
            reducedRowMap.eliminateRowsAboveWithNormalizedPivot(i, pivots[i])
        }
        return reducedRowMap
    }

    private fun <K, L, S> Map<K, Map<L, S>>.toMutableMapDeeply(): MutableMap<K, MutableMap<L, S>> {
        return this.mapValues { (_, row) -> row.toMutableMap() }.toMutableMap()
    }

    private fun <K, V> MutableMap<K, V>.replace(getNewValue: (K, V) -> V) {
        val mapIterator = this.iterator()
        while (mapIterator.hasNext()) {
            val mapEntry = mapIterator.next()
            val newValue: V = getNewValue(mapEntry.key, mapEntry.value)
            mapEntry.setValue(newValue)
        }
    }

    // private fun <K, V : Any> MutableMap<K, V>.replaceNotNull(getNewValue: (K, V) -> V?) {
    //     val mapIterator = this.iterator()
    //     while (mapIterator.hasNext()) {
    //         val mapEntry = mapIterator.next()
    //         val newValue: V? = getNewValue(mapEntry.key, mapEntry.value)
    //         if (newValue != null) {
    //             mapEntry.setValue(newValue)
    //         }
    //     }
    // }

    // private fun MutableMap<Int, S>.subtract(other: Map<Int, S>) {
    //     this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
    //         // use this@subtract.replaceNotNull()?
    //         for ((i, value) in other) {
    //             when (val valueFromThis: S? = this@subtract[i]) {
    //                 null -> this@subtract[i] = -value
    //                 else -> {
    //                     val newValue = valueFromThis - value
    //                     if (newValue.isZero()) {
    //                         this@subtract.remove(i)
    //                     } else {
    //                         this@subtract[i] = newValue
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }

    private fun MutableMap<Int, S>.subtract(other: Map<Int, S>, scalar: S) {
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            for ((i, value) in other) {
                val scaledValue = value * scalar
                when (val valueFromThis: S? = this@subtract[i]) {
                    null -> this@subtract[i] = -scaledValue
                    else -> {
                        val newValue = valueFromThis - scaledValue
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

    private fun MutableMap<Int, S>.multiply(scalar: S) {
        if (scalar.isZero()) {
            this.clear()
        } else {
            this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
                this@multiply.replace { _, value -> value * scalar }
            }
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateRowsBelow(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            val mapIterator = this@eliminateRowsBelow.iterator()
            while (mapIterator.hasNext()) {
                val mapEntry = mapIterator.next()
                val (i, row) = mapEntry
                if (i > rowInd) {
                    val coeff: S? = row[colInd]
                    if (coeff != null) {
                        row.subtract(mainRow, coeff / elm)
                        if (row.isEmpty())
                            mapIterator.remove()
                    }
                }
            }
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateRowsAboveWithNormalizedPivot(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            // Called from reduce() after pivot rows are normalized, so coeff / elm is just coeff.
            val mapIterator = this@eliminateRowsAboveWithNormalizedPivot.iterator()
            while (mapIterator.hasNext()) {
                val mapEntry = mapIterator.next()
                val (i, row) = mapEntry
                if (i < rowInd) {
                    val coeff: S? = row[colInd]
                    if (coeff != null) {
                        row.subtract(mainRow, coeff)
                        if (row.isEmpty())
                            mapIterator.remove()
                    }
                }
            }
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateOtherRows(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            // If we use a for-loop like
            //   for ((i, row) in this@eliminateOtherRows)
            // then java.util.ConcurrentModificationException is thrown.
            // By using an iterator directly, this exception can be avoided.
            val mapIterator = this@eliminateOtherRows.iterator()
            while (mapIterator.hasNext()) {
                val mapEntry = mapIterator.next()
                val (i, row) = mapEntry
                if (i != rowInd) {
                    // row[colInd] == null の場合は、mainRow * (coeff/elm) は計算せずに
                    // row を直接返した方が有意に速い
                    val coeff: S? = row[colInd]
                    if (coeff != null) {
                        row.subtract(mainRow, coeff / elm)
                        if (row.isEmpty())
                            mapIterator.remove()
                    }
                }
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
