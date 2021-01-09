package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

interface NumVector<S, V> {
    operator fun plus(other: V): V
    operator fun plus(other: NumVector<S, V>): NumVector<S, V> {
        return this.vectorSpace.wrap(this + other.unwrap())
    }

    operator fun minus(other: V): V {
        return (this + (-this.vectorSpace.wrap(other))).unwrap()
    }
    operator fun minus(other: NumVector<S, V>): NumVector<S, V> {
        return this.vectorSpace.wrap(this + other.unwrap())
    }

    operator fun unaryMinus(): NumVector<S, V> {
        return this * (-this.vectorSpace.field.ONE)
    }

    operator fun times(other: Scalar<S>): NumVector<S, V>
    fun unwrap(): V
    val vectorSpace: NumVectorSpace<S, V>
}

interface NumVectorSpace<S, V> {
    fun wrap(v: V): NumVector<S, V>
    val field: Field<S>
}
