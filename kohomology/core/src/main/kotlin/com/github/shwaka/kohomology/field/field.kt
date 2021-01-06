package com.github.shwaka.kohomology.field

interface Scalar<S> {
    operator fun plus(other: S): S
    operator fun plus(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this + other.unwrap())
    }
    operator fun minus(other: S): S {
        return this + this.field.fromInt(-1) * other
    }
    operator fun minus(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this - other.unwrap())
    }
    operator fun times(other: S): S
    operator fun times(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this * other.unwrap())
    }
    operator fun div(other: S): S
    operator fun div(other: Scalar<S>): Scalar<S> {
        return this.field.wrap(this * other.unwrap())
    }
    fun pow(exponent: Int): Scalar<S> {
        return when {
            exponent == 0 -> this.field.fromInt(1)
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else this.field.fromInt(1)
                half * half * rem
            }
            exponent < 0 -> this.field.fromInt(1) / this.pow(-exponent)
            else -> throw Exception("This can't happen!")
        }
    }
    operator fun unaryMinus(): Scalar<S> {
        return this.field.ZERO - this
    }
    fun inv(): Scalar<S> {
        return this.field.ONE / this
    }
    fun unwrap(): S
    val field: Field<S>
}

interface Field<S> {
    fun wrap(a: S): Scalar<S>
    fun fromInt(n: Int): Scalar<S>
    val ZERO: Scalar<S>
    val ONE: Scalar<S>
}
