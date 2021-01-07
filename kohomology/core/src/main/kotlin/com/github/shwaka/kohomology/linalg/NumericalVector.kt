package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

interface NumericalVector<S, V> {
    operator fun plus(other: V): V
    operator fun plus(other: NumericalVector<S, V>): NumericalVector<S, V> {
        return this.vectorSpace.wrap(this + other.unwrap())
    }
    fun timesInternal(other: Scalar<S>): V
    operator fun times(other: Scalar<S>): NumericalVector<S, V> {
        return this.vectorSpace.wrap(this.timesInternal(other))
    }
    fun unwrap(): V
    val vectorSpace: NumericalVectorSpace<S, V>
}

fun <S, SS : Scalar<S>, V> SS.timesVector(other: NumericalVector<S, V>): NumericalVector<S, V> {
    return other * this
}

interface NumericalVectorSpace<S, V> {
    fun wrap(v: V): NumericalVector<S, V>
}
