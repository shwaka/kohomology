package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.PrintableWithSign
import com.github.shwaka.kohomology.util.Sign

public interface Scalar : PrintableWithSign {
    public fun isZero(): Boolean
    public fun isNotZero(): Boolean = !this.isZero()
    public fun isOne(): Boolean
    public fun isNotOne(): Boolean = !this.isOne()
    public fun isPrintedPositively(): Boolean
}

public interface ScalarContext<S : Scalar> {
    public val field: Field<S>

    public operator fun S.plus(other: S): S = this@ScalarContext.field.add(this, other)
    public operator fun S.minus(other: S): S = this@ScalarContext.field.subtract(this, other)
    public operator fun S.times(other: S): S = this@ScalarContext.field.multiply(this, other)
    public operator fun S.times(other: Int): S = this@ScalarContext.field.multiply(this, this@ScalarContext.field.fromInt(other))
    public operator fun Int.times(other: S): S = this@ScalarContext.field.multiply(this@ScalarContext.field.fromInt(this), other)
    public operator fun S.times(sign: Sign): S {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(scalar: S): S = scalar * this
    public operator fun S.unaryMinus(): S = this@ScalarContext.field.unaryMinusOf(this)
    public operator fun S.div(other: S): S = this@ScalarContext.field.divide(this, other)
    public operator fun S.div(other: Int): S = this@ScalarContext.field.divide(this, this@ScalarContext.field.fromInt(other))
    public operator fun Int.div(other: S): S = this@ScalarContext.field.divide(this@ScalarContext.field.fromInt(this), other)
    public fun S.inv(): S = this@ScalarContext.field.divide(one, this)
    public fun S.pow(exponent: Int): S {
        return when {
            exponent == 0 -> one
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else one
                half * half * rem
            }
            exponent < 0 -> one / this.pow(-exponent)
            else -> throw Exception("This can't happen!")
        }
    }
    public fun Int.toScalar(): S = this@ScalarContext.field.fromInt(this)
    public fun fromInt(n: Int): S = this.field.fromInt(n)
    public fun fromIntPair(numerator: Int, denominator: Int): S = this.field.fromIntPair(numerator, denominator)

    public val zero: S
        get() = this.field.zero
    public val one: S
        get() = this.field.one
    public val two: S
        get() = this.field.two
    public val three: S
        get() = this.field.three
    public val four: S
        get() = this.field.four
    public val five: S
        get() = this.field.five

    public fun Iterable<S>.sum(): S = this.fold(zero) { acc, x -> acc + x }
    public fun Iterable<S>.product(): S = this.fold(one) { acc, x -> acc * x }
}

internal class ScalarContextImpl<S : Scalar>(
    override val field: Field<S>,
) : ScalarContext<S>

public interface Field<S : Scalar> {
    public val context: ScalarContext<S>
    public val characteristic: Int
    public operator fun contains(scalar: S): Boolean
    public fun add(a: S, b: S): S
    public fun subtract(a: S, b: S): S
    public fun multiply(a: S, b: S): S
    public fun divide(a: S, b: S): S
    public fun unaryMinusOf(scalar: S): S = this.multiply(scalar, this.fromInt(-1))
    public fun fromInt(n: Int): S
    public fun fromIntPair(numerator: Int, denominator: Int): S = this.divide(this.fromInt(numerator), this.fromInt(denominator))

    // Scalar values zero, one,..., five should be defined in classes that implement Field
    // since it should be stored in a property for performance reason.
    public val zero: S
    public val one: S
    public val two: S
    public val three: S
    public val four: S
    public val five: S
}

public interface FiniteField<S : Scalar> : Field<S> {
    public val order: Int
    public val elements: List<S>
}
