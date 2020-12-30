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


class Rational(numerator: Int, denominator: Int) : Scalar<Rational> {
    private val numerator: Int
    private val denominator: Int
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }
    override val field = RationalField
    override operator fun plus(other: Rational): Rational {
        val numerator = this.numerator * other.denominator + other.numerator * this.denominator
        val denominator = this.denominator * other.denominator
        return Rational(numerator, denominator)
    }
    override operator fun times(other: Rational): Rational {
        return Rational(this.numerator * other.numerator, this.denominator * other.denominator)
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

    override fun unwrap(): Rational {
        return this
    }

    override fun equals(other: Any?): Boolean {
        // generated by Intellij Idea
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

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

object RationalField : Field<Rational> {
    override fun wrap(a: Rational): Scalar<Rational> {
        return a
    }
    override fun fromInteger(n: Int): Rational {
        return Rational(n, 1)
    }
}
