package com.github.shwaka.kohomology.field

import kotlin.Exception
import kotlin.math.absoluteValue
import kotlin.math.sign

private fun gcd(a: Int, b: Int): Int {
    if (a == 0 || b == 0) {
        throw Exception("gcd not defined for 0")
    }
    val aAbs = a.absoluteValue
    val bAbs = b.absoluteValue
    return if (aAbs >= bAbs) {
        gcdInternal(aAbs, bAbs)
    } else {
        gcdInternal(bAbs, aAbs)
    }
}

private fun gcdInternal(a: Int, b: Int): Int {
    // arguments should satisfy a >= b >= 0
    if (b == 0) return a
    return gcdInternal(b, a % b)
}

private fun reduce(numerator: Int, denominator: Int): Pair<Int, Int> {
    if (numerator == 0) return Pair(0, 1)
    val g = gcd(numerator, denominator)
    val num = numerator * denominator.sign / g
    val den = denominator.absoluteValue / g
    return Pair(num, den)
}

class IntRational(numerator: Int, denominator: Int) : RationalScalar<IntRational> {
    private val numerator: Int
    private val denominator: Int
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }
    override val field = IntRationalField
    override operator fun plus(other: IntRational): IntRational {
        val numerator = this.numerator * other.denominator + other.numerator * this.denominator
        val denominator = this.denominator * other.denominator
        return IntRational(numerator, denominator)
    }
    override operator fun times(other: IntRational): IntRational {
        return IntRational(this.numerator * other.numerator, this.denominator * other.denominator)
    }
    override operator fun div(other: IntRational): IntRational {
        if (other == IntRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return IntRational(this.numerator * other.denominator, this.denominator * other.numerator)
    }

    override fun toString(): String {
        return when {
            this.numerator == 0 -> {
                "0"
            }
            this.denominator == 1 -> {
                this.numerator.toString()
            }
            else -> {
                "${this.numerator}/${this.denominator}"
            }
        }
    }

    override fun unwrap(): IntRational {
        return this
    }

    override fun equals(other: Any?): Boolean {
        // generated by Intellij Idea
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as IntRational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        // generated by Intellij Idea
        var result = numerator
        result = 31 * result + denominator
        return result
    }
}

object IntRationalField : RationalField<IntRational> {
    override fun fromInt(n: Int): IntRational {
        return IntRational(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): IntRational {
        return IntRational(numerator, denominator)
    }

    override fun toString(): String {
        return "IntRationalField"
    }

    override val zero = IntRational(0, 1)
    override val one = IntRational(1, 1)
}
