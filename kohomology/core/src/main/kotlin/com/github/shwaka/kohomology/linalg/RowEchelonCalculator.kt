package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

object RowEchelonCalculator {
    fun <S : Scalar<S>> exchange(values: List<List<S>>, i1: Int, i2: Int): List<List<S>> {
        if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
        return values.indices.map { i ->
            when (i) {
                i1 -> values[i2]
                i2 -> values[i1]
                else -> values[i]
            }
        }
    }

    fun <S : Scalar<S>> add(values: List<List<S>>, from: Int, to: Int, scalar: S): List<List<S>> {
        if (from == to) throw IllegalArgumentException("Row numbers must be distinct")
        return values.indices.map { i ->
            when (i) {
                to -> values[to].zip(values[from]).map { (a, b) -> a + b * scalar }
                else -> values[i]
            }
        }
    }

    fun <S : Scalar<S>> multiply(values: List<List<S>>, to: Int, scalar: S): List<List<S>> {
        if (scalar == scalar.field.zero) throw IllegalArgumentException("scalar must be non-zero")
        return values.indices.map { i ->
            when (i) {
                to -> values[to].map { a -> a * scalar }
                else -> values[i]
            }
        }
    }
}
