package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

interface NumVector<S : Scalar<S>, V : NumVector<S, V>> {
    operator fun plus(other: V): V

    operator fun minus(other: V): V {
        return (this + (-other)).unwrap()
    }

    operator fun unaryMinus(): V {
        return this * (-this.vectorSpace.field.one)
    }

    operator fun times(other: S): V
    operator fun times(other: Int): V {
        return this * this.vectorSpace.field.fromInt(other)
    }

    fun unwrap(): V
    val vectorSpace: NumVectorSpace<S, V>
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
}
