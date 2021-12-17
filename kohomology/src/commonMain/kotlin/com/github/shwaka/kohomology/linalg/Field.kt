package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.PrintableWithSign
import com.github.shwaka.kohomology.util.Sign

public interface Scalar : PrintableWithSign {
    public fun isZero(): Boolean
    public fun isNotZero(): Boolean = !this.isZero()
    public fun isPrintedPositively(): Boolean
}

public interface ScalarOperations<S : Scalar> {
    public val field: Field<S>
    public val characteristic: Int
    public operator fun contains(scalar: S): Boolean
    public fun add(a: S, b: S): S
    public fun subtract(a: S, b: S): S
    public fun multiply(a: S, b: S): S
    public fun divide(a: S, b: S): S
    public fun unaryMinusOf(scalar: S): S = this.multiply(scalar, this.fromInt(-1))
    public fun fromInt(n: Int): S
    public fun fromIntPair(numerator: Int, denominator: Int): S = this.divide(this.fromInt(numerator), this.fromInt(denominator))
}

public open class ScalarContext<S : Scalar>(
    private val scalarOperations: ScalarOperations<S>
) : ScalarOperations<S> by scalarOperations {
    public operator fun S.plus(other: S): S = this@ScalarContext.add(this, other)
    public operator fun S.minus(other: S): S = this@ScalarContext.subtract(this, other)
    public operator fun S.times(other: S): S = this@ScalarContext.multiply(this, other)
    public operator fun S.times(other: Int): S = this@ScalarContext.multiply(this, this@ScalarContext.fromInt(other))
    public operator fun Int.times(other: S): S = this@ScalarContext.multiply(this@ScalarContext.fromInt(this), other)
    public operator fun S.times(sign: Sign): S {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(scalar: S): S = scalar * this
    public operator fun S.unaryMinus(): S = this@ScalarContext.unaryMinusOf(this)
    public operator fun S.div(other: S): S = this@ScalarContext.divide(this, other)
    public operator fun S.div(other: Int): S = this@ScalarContext.divide(this, this@ScalarContext.fromInt(other))
    public operator fun Int.div(other: S): S = this@ScalarContext.divide(this@ScalarContext.fromInt(this), other)
    public fun S.inv(): S = this@ScalarContext.divide(one, this)
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
    public fun Int.toScalar(): S = this@ScalarContext.fromInt(this)
    public val zero: S = 0.toScalar()
    public val one: S = 1.toScalar()
    public val two: S = 2.toScalar()
    public val three: S = 3.toScalar()
    public val four: S = 4.toScalar()
    public val five: S = 5.toScalar()
    public fun Iterable<S>.sum(): S = this.fold(zero) { acc, x -> acc + x }
    public fun Iterable<S>.product(): S = this.fold(one) { acc, x -> acc * x }
}

public interface Field<S : Scalar> : ScalarOperations<S> {
    public val context: ScalarContext<S>
    public val zero: S
        get() = this.context.zero
    public val one: S
        get() = this.context.one
}
