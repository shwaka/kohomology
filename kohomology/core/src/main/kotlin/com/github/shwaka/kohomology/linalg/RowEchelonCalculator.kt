package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

class DenseRowEchelonForm<S : Scalar<S>>(private val originalMatrix: DenseMatrix<S>) : RowEchelonForm<S, DenseNumVector<S>, DenseMatrix<S>> {
    private val rawData = this.originalMatrix.toList().rowEchelonForm()
    override val matrix: DenseMatrix<S> by lazy {
        originalMatrix.matrixSpace.fromRows(this.rawData.first)
    }
    override val pivots: List<Int> by lazy {
        this.rawData.second
    }
    override val sign: Int by lazy {
        if (this.rawData.third % 2 == 0) 1 else -1
    }
    override val reducedMatrix: DenseMatrix<S>
        get() = TODO("Not yet implemented")

    fun <S : Scalar<S>> List<List<S>>.exchangeRows(i1: Int, i2: Int): List<List<S>> {
        if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
        return this.indices.map { i ->
            when (i) {
                i1 -> this[i2]
                i2 -> this[i1]
                else -> this[i]
            }
        }
    }

    operator fun <S : Scalar<S>> List<S>.plus(other: List<S>): List<S> {
        return this.zip(other).map { (a, b) -> a + b }
    }

    operator fun <S : Scalar<S>> List<S>.minus(other: List<S>): List<S> {
        return this.zip(other).map { (a, b) -> a - b }
    }

    operator fun <S : Scalar<S>> List<S>.times(other: S): List<S> {
        return this.map { a -> a * other }
    }

    fun <S : Scalar<S>> List<List<S>>.addToAnotherRow(from: Int, to: Int, scalar: S): List<List<S>> {
        if (from == to) throw IllegalArgumentException("Row numbers must be distinct")
        return this.indices.map { i ->
            when (i) {
                to -> this[to] + this[from] * scalar
                else -> this[i]
            }
        }
    }

    fun <S : Scalar<S>> List<List<S>>.multiplyScalarToRow(to: Int, scalar: S): List<List<S>> {
        if (scalar == scalar.field.zero) throw IllegalArgumentException("scalar must be non-zero")
        return this.indices.map { i ->
            when (i) {
                to -> this[to].map { a -> a * scalar }
                else -> this[i]
            }
        }
    }

    fun <S : Scalar<S>> List<List<S>>.eliminateOtherRows(rowInd: Int, colInd: Int): List<List<S>> {
        if (this[rowInd][colInd] == this[0][0].field.zero)
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        return this.indices.map { i ->
            when (i) {
                rowInd -> this[rowInd]
                else -> this[i] - this[rowInd] * (this[i][colInd] / this[rowInd][colInd])
            }
        }
    }

    fun <S : Scalar<S>> List<List<S>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        for (i in rowIndFrom until this.size) {
            if (this[i][colInd] != this[i][colInd].field.zero) return i
        }
        return null
    }

    private fun <S : Scalar<S>> List<List<S>>.rowEchelonFormInternal(
        currentColInd: Int,
        pivots: List<Int>,
        exchangeCount: Int
    ): Triple<List<List<S>>, List<Int>, Int> {
        if (currentColInd == this[0].size) {
            // 全ての列の処理が終わった場合
            return Triple(this, pivots, exchangeCount)
        }
        val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
        return if (rowInd == null) {
            this.rowEchelonFormInternal(currentColInd + 1, pivots, exchangeCount)
        } else {
            var newMatrix = this.eliminateOtherRows(rowInd, currentColInd)
            var newExchangeCount = exchangeCount
            if (rowInd != pivots.size) {
                newMatrix = newMatrix.exchangeRows(rowInd, pivots.size)
                newExchangeCount += 1
            }
            val newPivots = pivots + listOf(currentColInd)
            newMatrix.rowEchelonFormInternal(currentColInd + 1, newPivots, newExchangeCount)
        }
    }

    fun <S : Scalar<S>> List<List<S>>.rowEchelonForm(): Triple<List<List<S>>, List<Int>, Int> {
        return this.rowEchelonFormInternal(0, listOf(), 0)
    }
}
