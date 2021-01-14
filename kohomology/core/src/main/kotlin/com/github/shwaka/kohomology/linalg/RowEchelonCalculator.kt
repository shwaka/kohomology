package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

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
    if (this[rowInd][colInd] == this[0][0].field.zero) throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
    return this.indices.map { i ->
        when (i) {
            rowInd -> this[rowInd]
            else -> this[i] - this[rowInd] * (this[i][colInd] / this[rowInd][colInd])
        }
    }
}
