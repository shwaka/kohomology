package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

interface NumericalVector<S, V> {
    operator fun plus(other: V): V
    operator fun plus(other: NumericalVector<S, V>): NumericalVector<S, V> {
        return this.vectorSpace.wrap(this + other.unwrap())
    }
    operator fun times(other: Scalar<S>): NumericalVector<S, V>
    fun unwrap(): V
    val vectorSpace: NumericalVectorSpace<S, V>
}

interface NumericalVectorSpace<S, V> {
    fun wrap(v: V): NumericalVector<S, V>
}
