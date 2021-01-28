package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

interface NumVector<S : Scalar<S>, V : NumVector<S, V>> {
    operator fun plus(other: V): V

    operator fun minus(other: V): V {
        return (this + (-other)).unwrap()
    }

    operator fun unaryMinus(): V {
        return this * (-this.numVectorSpace.field.one)
    }

    operator fun times(other: S): V
    operator fun times(other: Int): V {
        return this * this.numVectorSpace.field.fromInt(other)
    }

    operator fun get(index: Int): S
    fun toList(): List<S> {
        return (0 until this.dim).map { i -> this[i] }
    }

    fun unwrap(): V
    val numVectorSpace: NumVectorSpace<S, V>
    val dim: Int
}

operator fun <S : Scalar<S>, V : NumVector<S, V>> S.times(other: V): V {
    return other * this
}

operator fun <S : Scalar<S>, V : NumVector<S, V>> Int.times(other: V): V {
    return other * this
}

interface NumVectorSpace<S : Scalar<S>, V : NumVector<S, V>> {
    val field: Field<S>
    fun getZero(dim: Int): V
    fun fromValues(values: List<S>): V
    fun fromValues(vararg values: S): V
}
