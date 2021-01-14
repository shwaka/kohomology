package com.github.shwaka.kohomology.field

interface Scalar<S : Scalar<S>> {
    operator fun plus(other: S): S
    operator fun minus(other: S): S {
        return this + this.field.fromInt(-1) * other
    }
    operator fun times(other: S): S
    operator fun times(other: Int): S {
        return this * this.field.fromInt(other)
    }
    operator fun div(other: S): S
    fun pow(exponent: Int): S {
        return when {
            exponent == 0 -> this.field.fromInt(1)
            exponent == 1 -> this.unwrap()
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this.unwrap() else this.field.fromInt(1)
                half * half * rem
            }
            exponent < 0 -> this.field.fromInt(1) / this.pow(-exponent)
            else -> throw Exception("This can't happen!")
        }
    }
    operator fun unaryMinus(): S {
        return this.field.zero - this.unwrap()
    }
    fun inv(): S {
        return this.field.one / this.unwrap()
    }
    fun unwrap(): S
    val field: Field<S>
}

operator fun <S : Scalar<S>> Int.times(other: S): S {
    return other * this
}

interface Field<S : Scalar<S>> {
    fun wrap(a: S): Scalar<S>
    fun fromInt(n: Int): S
    val zero: S
    val one: S
}
