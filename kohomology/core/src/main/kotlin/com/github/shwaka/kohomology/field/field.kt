package com.github.shwaka.kohomology.field

interface Scalar<S : Scalar<S>> {
    val field: Field<S>
}

interface ScalarOperations<S : Scalar<S>> {
    fun add(a: S, b: S): S
    fun subtract(a: S, b: S): S
    fun multiply(a: S, b: S): S
    fun divide(a: S, b: S): S
    fun fromInt(n: Int): S
    fun fromIntPair(numerator: Int, denominator: Int): S = this.divide(this.fromInt(numerator), this.fromInt(denominator))
}

open class ScalarContext<S : Scalar<S>>(
    private val scalarOperations: ScalarOperations<S>
) : ScalarOperations<S> by scalarOperations {
    operator fun S.plus(other: S): S = this@ScalarContext.add(this, other)
    operator fun S.minus(other: S): S = this@ScalarContext.subtract(this, other)
    operator fun S.times(other: S): S = this@ScalarContext.multiply(this, other)
    operator fun S.times(other: Int): S = this@ScalarContext.multiply(this, this@ScalarContext.fromInt(other))
    operator fun Int.times(other: S): S = this@ScalarContext.multiply(this@ScalarContext.fromInt(this), other)
    operator fun S.unaryMinus(): S = this@ScalarContext.multiply(this, this@ScalarContext.fromInt(-1))
    operator fun S.div(other: S): S = this@ScalarContext.divide(this, other)
    operator fun S.div(other: Int): S = this@ScalarContext.divide(this, this@ScalarContext.fromInt(other))
    operator fun Int.div(other: S): S = this@ScalarContext.divide(this@ScalarContext.fromInt(this), other)
    fun S.inv(): S = this@ScalarContext.divide(this.field.one, this)
    fun S.pow(exponent: Int): S {
        return when {
            exponent == 0 -> this.field.one
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else this.field.one
                half * half * rem
            }
            exponent < 0 -> this.field.one / this.pow(-exponent)
            else -> throw Exception("This can't happen!")
        }
    }
}

interface Field<S : Scalar<S>> : ScalarOperations<S> {
    val scalarContext: ScalarContext<S>
    fun <T> withContext(block: ScalarContext<S>.() -> T) = this.scalarContext.block()
    val zero: S
    val one: S
}
