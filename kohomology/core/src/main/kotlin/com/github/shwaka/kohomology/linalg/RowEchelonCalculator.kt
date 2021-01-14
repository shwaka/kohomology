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

fun <S : Scalar<S>> List<List<S>>.addToAnotherRow(from: Int, to: Int, scalar: S): List<List<S>> {
    if (from == to) throw IllegalArgumentException("Row numbers must be distinct")
    return this.indices.map { i ->
        when (i) {
            to -> this[to].zip(this[from]).map { (a, b) -> a + b * scalar }
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
