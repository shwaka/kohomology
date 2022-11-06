package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.exchange

internal data class SetRowEchelonFormData<S : Scalar>(
    val rowSetMap: Map<Int, Set<Int>>,
    val pivots: List<Int>,
    val exchangeCount: Int,
)

internal class NonInPlaceSetRowEchelonForm<S : Scalar>(
    override val matrixSpace: SetMatrixSpace<S>,
    originalMatrix: SetMatrix<S>,
) : RowEchelonForm<S, SetNumVector<S>, SetMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val data: SetRowEchelonFormData<S> by lazy {
        val rowSetMap = this@NonInPlaceSetRowEchelonForm.originalMatrix.rowSetMap
        this.rowEchelonForm(rowSetMap, this.colCount)
    }

    override fun computeRowEchelonForm(): SetMatrix<S> {
        return this.matrixSpace.fromRowSetMap(this.data.rowSetMap, this.rowCount, this.colCount)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        return Sign.fromParity(this.data.exchangeCount)
    }

    override fun computeReducedRowEchelonForm(): SetMatrix<S> {
        val rank = this.data.pivots.size
        // Since any non-zero element is equal to 1,
        // there is no need to change the leading entry in a row to 1.
        var reducedRowSetMap = this.data.rowSetMap
        for (i in 0 until rank) {
            reducedRowSetMap = reducedRowSetMap.eliminateOtherRows(i, pivots[i])
        }
        return this.matrixSpace.fromRowSetMap(reducedRowSetMap, this.rowCount, this.colCount)
    }

    private fun rowEchelonForm(matrix: Map<Int, Set<Int>>, colCount: Int): SetRowEchelonFormData<S> {
        return matrix.rowEchelonFormInternal(0, listOf(), 0, colCount)
    }

    private tailrec fun Map<Int, Set<Int>>.rowEchelonFormInternal(
        currentColInd: Int,
        pivots: List<Int>,
        exchangeCount: Int,
        colCount: Int,
    ): SetRowEchelonFormData<S> {
        // Almost all code is copied from SparseRowEchelonFormCalculator
        // use 'tailrec' to avoid StackOverflowError
        if (this.isEmpty()) {
            // 全ての成分が0の場合
            return SetRowEchelonFormData(this, emptyList(), 0)
        }
        if (currentColInd == colCount) {
            // 全ての列の処理が終わった場合
            return SetRowEchelonFormData(this, pivots, exchangeCount)
        }
        val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
        return if (rowInd == null) {
            this.rowEchelonFormInternal(currentColInd + 1, pivots, exchangeCount, colCount)
        } else {
            var newRowSetMap = this.eliminateOtherRows(rowInd, currentColInd)
            var newExchangeCount = exchangeCount
            if (rowInd != pivots.size) {
                newRowSetMap = newRowSetMap.exchangeRows(rowInd, pivots.size)
                newExchangeCount += 1
            }
            val newPivots = pivots + listOf(currentColInd)
            newRowSetMap.rowEchelonFormInternal(currentColInd + 1, newPivots, newExchangeCount, colCount)
        }
    }

    private fun Map<Int, Set<Int>>.exchangeRows(i1: Int, i2: Int): Map<Int, Set<Int>> {
        require(i1 != i2) { "Row numbers must be distinct" }
        return this.mapKeys { (i, _) ->
            when (i) {
                i1 -> i2
                i2 -> i1
                else -> i
            }
        }
    }

    private operator fun Set<Int>.minus(other: Set<Int>): Set<Int> {
        return this xor other
    }

    private operator fun Set<Int>.times(scalar: S): Set<Int> {
        return if (scalar.isZero()) {
            emptySet()
        } else {
            this
        }
    }

    private fun Map<Int, Set<Int>>.eliminateOtherRows(rowInd: Int, colInd: Int): Map<Int, Set<Int>> {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        require(mainRow.contains(colInd)) {
            "Cannot eliminate since the element at ($rowInd, $colInd) is zero"
        }
        return this.mapValues { (i, row) ->
            when (i) {
                rowInd -> row
                else -> {
                    if (row.contains(colInd)) {
                        row - mainRow
                    } else {
                        row
                    }
                }
            }
        }
    }

    private fun Map<Int, Set<Int>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        for (i in this.keys.filter { it >= rowIndFrom }) {
            this[i]?.let { row ->
                if (row.contains(colInd))
                    return i
            }
        }
        return null
    }
}

internal class InPlaceSetRowEchelonForm<S : Scalar>(
    override val matrixSpace: SetMatrixSpace<S>,
    originalMatrix: SetMatrix<S>,
) : RowEchelonForm<S, SetNumVector<S>, SetMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val data: SetRowEchelonFormData<S> by lazy {
        val rowSetMap = this@InPlaceSetRowEchelonForm.originalMatrix.rowSetMap
        this.rowEchelonForm(rowSetMap, this.colCount)
    }

    override fun computeRowEchelonForm(): SetMatrix<S> {
        return this.matrixSpace.fromRowSetMap(this.data.rowSetMap, this.rowCount, this.colCount)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        return Sign.fromParity(this.data.exchangeCount)
    }

    override fun computeReducedRowEchelonForm(): SetMatrix<S> {
        val rank = this.data.pivots.size
        // Since any non-zero element is equal to 1,
        // there is no need to change the leading entry in a row to 1.
        val reducedRowSetMap = this.data.rowSetMap.toMutableDeeply()
        for (i in 0 until rank) {
            reducedRowSetMap.eliminateOtherRows(i, pivots[i])
        }
        return this.matrixSpace.fromRowSetMap(reducedRowSetMap, this.rowCount, this.colCount)
    }

    private fun <K, T> Map<K, Set<T>>.toMutableDeeply(): MutableMap<K, MutableSet<T>> {
        return this.mapValues { (_, row) -> row.toMutableSet() }.toMutableMap()
    }

    private fun rowEchelonForm(matrix: Map<Int, Set<Int>>, colCount: Int): SetRowEchelonFormData<S> {
        var currentColInd: Int = 0
        val pivots: MutableList<Int> = mutableListOf()
        var exchangeCount: Int = 0
        val currentMatrix: MutableMap<Int, MutableSet<Int>> = matrix.toMutableDeeply()
        while (currentColInd < colCount) {
            val rowInd: Int? = currentMatrix.findNonZero(currentColInd, pivots.size)
            if (rowInd == null) {
                currentColInd++
                continue
            } else {
                currentMatrix.eliminateOtherRows(rowInd, currentColInd)
                if (rowInd != pivots.size) {
                    currentMatrix.exchange(rowInd, pivots.size)
                    exchangeCount++
                }
                pivots.add(currentColInd)
                currentColInd++
            }
        }
        return SetRowEchelonFormData(currentMatrix, pivots, exchangeCount)
    }

    private fun MutableSet<Int>.subtract(other: Set<Int>) {
        for (i in other) {
            if (this.contains(i)) {
                this.remove(i)
            } else {
                this.add(i)
            }
        }
    }

    private fun MutableMap<Int, MutableSet<Int>>.eliminateOtherRows(rowInd: Int, colInd: Int) {
        val mainRow = this[rowInd]
            ?: throw IllegalArgumentException("Cannot eliminate since the row $rowInd is zero")
        require(mainRow.contains(colInd)) {
            "Cannot eliminate since the element at ($rowInd, $colInd) is zero"
        }
        // If we use a for-loop like
        //   for ((i, row) in this@eliminateOtherRows)
        // then java.util.ConcurrentModificationException is thrown.
        // By using an iterator directly, this exception can be avoided.
        val mapIterator = this.iterator()
        while (mapIterator.hasNext()) {
            val mapEntry = mapIterator.next()
            val (i, row) = mapEntry
            if (i != rowInd) {
                if (row.contains(colInd)) {
                    row.subtract(mainRow)
                }
            }
        }
    }

    private fun Map<Int, Set<Int>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        // same as in SetRowEchelonForm
        for (i in this.keys.filter { it >= rowIndFrom }) {
            this[i]?.let { row ->
                if (row.contains(colInd))
                    return i
            }
        }
        return null
    }
}
