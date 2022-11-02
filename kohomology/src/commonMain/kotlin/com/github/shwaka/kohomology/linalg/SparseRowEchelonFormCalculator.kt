package com.github.shwaka.kohomology.linalg

internal data class SparseRowEchelonFormData<S : Scalar>(
    val rowMap: Map<Int, Map<Int, S>>,
    val pivots: List<Int>,
    val exchangeCount: Int
)

// SparseRowEchelonFormCalculator is slower than InPlaceSparseRowEchelonFormCalculator
// and hence currently it is not used.
// It is remaining here for debugging.

// internal class SparseRowEchelonFormCalculator<S : Scalar>(private val field: Field<S>) {
//     fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
//         return matrix.rowEchelonFormInternal(0, listOf(), 0, colCount)
//     }
//
//     fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
//         val rank = pivots.size
//         var reducedRowMap = rowEchelonRowMap.mapValues { (i, row) ->
//             val elm: S = row[pivots[i]] ?: throw Exception("This can't happen!")
//             this.field.context.run {
//                 row * elm.inv()
//             }
//         }
//         for (i in 0 until rank) {
//             reducedRowMap = reducedRowMap.eliminateOtherRows(i, pivots[i])
//         }
//         return reducedRowMap
//     }
//
//     private tailrec fun Map<Int, Map<Int, S>>.rowEchelonFormInternal(
//         currentColInd: Int,
//         pivots: List<Int>,
//         exchangeCount: Int,
//         colCount: Int
//     ): SparseRowEchelonFormData<S> {
//         // use 'tailrec' to avoid StackOverflowError
//         if (this.isEmpty()) {
//             // 全ての成分が0の場合
//             return SparseRowEchelonFormData(this, emptyList(), 0)
//         }
//         if (currentColInd == colCount) {
//             // 全ての列の処理が終わった場合
//             return SparseRowEchelonFormData(this, pivots, exchangeCount)
//         }
//         val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
//         return if (rowInd == null) {
//             this.rowEchelonFormInternal(currentColInd + 1, pivots, exchangeCount, colCount)
//         } else {
//             var newRowMap = this.eliminateOtherRows(rowInd, currentColInd)
//             var newExchangeCount = exchangeCount
//             if (rowInd != pivots.size) {
//                 newRowMap = newRowMap.exchangeRows(rowInd, pivots.size)
//                 newExchangeCount += 1
//             }
//             val newPivots = pivots + listOf(currentColInd)
//             newRowMap.rowEchelonFormInternal(currentColInd + 1, newPivots, newExchangeCount, colCount)
//         }
//     }
//
//     private fun Map<Int, Map<Int, S>>.exchangeRows(i1: Int, i2: Int): Map<Int, Map<Int, S>> {
//         if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
//         return this.mapKeys { (i, _) ->
//             when (i) {
//                 i1 -> i2
//                 i2 -> i1
//                 else -> i
//             }
//         }
//     }
//
//     private operator fun Map<Int, S>.minus(other: Map<Int, S>): Map<Int, S> {
//         val newMap: MutableMap<Int, S> = this.toMutableMap()
//         this@SparseRowEchelonFormCalculator.field.context.run {
//             for ((i, value) in other) {
//                 when (val valueFromThis: S? = newMap[i]) {
//                     null -> newMap[i] = -value
//                     else -> newMap[i] = valueFromThis - value
//                 }
//             }
//         }
//         return newMap.filterValues { it.isNotZero() }
//     }
//
//     private operator fun Map<Int, S>.times(scalar: S): Map<Int, S> {
//         if (scalar.isZero())
//             return mapOf()
//         return this@SparseRowEchelonFormCalculator.field.context.run {
//             this@times.mapValues { (_, value) -> value * scalar }
//         }
//     }
//
//     private fun Map<Int, Map<Int, S>>.eliminateOtherRows(rowInd: Int, colInd: Int): Map<Int, Map<Int, S>> {
//         val mainRow = this[rowInd]
//             ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
//         val elm: S? = mainRow[colInd]
//         if (elm == null || elm.isZero())
//             throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
//         return this@SparseRowEchelonFormCalculator.field.context.run {
//             this@eliminateOtherRows.mapValues { (i, row) ->
//                 when (i) {
//                     rowInd -> row
//                     else -> {
//                         // row[colInd] == null の場合は、mainRow * (coeff/elm) は計算せずに
//                         // row を直接返した方が有意に速い
//                         val coeff: S? = row[colInd]
//                         if (coeff == null)
//                             row
//                         else
//                             row - mainRow * (coeff / elm)
//                     }
//                 }
//             }.filterValues { row -> row.isNotEmpty() }
//         }
//     }
//
//     private fun Map<Int, Map<Int, S>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
//         for (i in this.keys.filter { it >= rowIndFrom }) {
//             this[i]?.let { row ->
//                 row[colInd]?.let { elm ->
//                     if (elm.isNotZero())
//                         return i
//                 }
//             }
//         }
//         return null
//     }
// }

internal class InPlaceSparseRowEchelonFormCalculator<S : Scalar>(private val field: Field<S>) {
    fun rowEchelonForm(matrix: Map<Int, Map<Int, S>>, colCount: Int): SparseRowEchelonFormData<S> {
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
                currentMatrix.eliminateOtherRows(rowInd, currentColInd)
                if (rowInd != pivots.size) {
                    currentMatrix.exchangeRows(rowInd, pivots.size)
                    exchangeCount++
                }
                pivots.add(currentColInd)
                currentColInd++
            }
        }
        return SparseRowEchelonFormData(currentMatrix, pivots, exchangeCount)
    }

    fun reduce(rowEchelonRowMap: Map<Int, Map<Int, S>>, pivots: List<Int>): Map<Int, Map<Int, S>> {
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
            reducedRowMap.eliminateOtherRows(i, pivots[i])
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

    private fun <K, V : Any> MutableMap<K, V>.replaceNotNull(getNewValue: (K, V) -> V?) {
        val mapIterator = this.iterator()
        while (mapIterator.hasNext()) {
            val mapEntry = mapIterator.next()
            val newValue: V? = getNewValue(mapEntry.key, mapEntry.value)
            if (newValue != null) {
                mapEntry.setValue(newValue)
            }
        }
    }

    private fun MutableMap<Int, MutableMap<Int, S>>.exchangeRows(i1: Int, i2: Int) {
        if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
        when (val row1 = this[i1]) {
            null -> when (val row2 = this[i2]) {
                null -> return
                else -> {
                    this[i1] = row2
                    this.remove(i2)
                }
            }
            else -> when (val row2 = this[i2]) {
                null -> {
                    this[i2] = row1
                    this.remove(i1)
                }
                else -> {
                    this[i1] = row2
                    this[i2] = row1
                }
            }
        }
    }

    private fun MutableMap<Int, S>.subtract(other: Map<Int, S>) {
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            // use this@subtract.replaceNotNull()?
            for ((i, value) in other) {
                when (val valueFromThis: S? = this@subtract[i]) {
                    null -> this@subtract[i] = -value
                    else -> {
                        val newValue = valueFromThis - value
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

    private fun MutableMap<Int, S>.subtract(other: Map<Int, S>, scalar: S) {
        this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
            // use this@subtract.replaceNotNull()?
            for ((i, value) in other) {
                when (val valueFromThis: S? = this@subtract[i]) {
                    null -> this@subtract[i] = -value * scalar
                    else -> {
                        val newValue = valueFromThis - value * scalar
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

    private fun MutableMap<Int, MutableMap<Int, S>>.eliminateOtherRows(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        val elm: S? = mainRow[colInd]
        if (elm == null || elm.isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        return this@InPlaceSparseRowEchelonFormCalculator.field.context.run {
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
        // same as in SparseRowEchelonFormCalculator
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
