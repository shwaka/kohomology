package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

fun <S : Scalar<S>> List<List<S>>.exchangeRows(i1: Int, i2: Int): List<List<S>> {
    if (i1 == i2) return this
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
    if (this[rowInd][colInd] == this[0][0].field.zero) throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
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
    pivots: List<Int>
): Pair<List<List<S>>, List<Int>> {
    if (currentColInd == this[0].size) {
        return Pair(this, pivots)
    }
    val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
    return if (rowInd == null) {
        this.rowEchelonFormInternal(currentColInd + 1, pivots)
    } else {
        val eliminated = this.eliminateOtherRows(rowInd, currentColInd).exchangeRows(rowInd, pivots.size)
        val newPivots = pivots + listOf(currentColInd)
        eliminated.rowEchelonFormInternal(currentColInd + 1, newPivots)
    }
}

fun <S : Scalar<S>> List<List<S>>.rowEchelonFrom(): Pair<List<List<S>>, List<Int>> {
    return this.rowEchelonFormInternal(0, listOf())
}
