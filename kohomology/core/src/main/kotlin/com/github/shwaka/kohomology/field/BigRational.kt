package com.github.shwaka.kohomology.field

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.Exception

private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    if (a == BigInteger.ZERO || b == BigInteger.ZERO) {
        throw Exception("gcd not defined for 0")
    }
    val aAbs = a.abs()
    val bAbs = b.abs()
    return if (aAbs >= bAbs) {
        gcdInternal(aAbs, bAbs)
    } else {
        gcdInternal(bAbs, aAbs)
    }
}

private fun gcdInternal(a: BigInteger, b: BigInteger): BigInteger {
    // arguments should satisfy a >= b >= 0
    if (b == BigInteger.ZERO) return a
    return gcdInternal(b, a % b)
}

private fun reduce(numerator: BigInteger, denominator: BigInteger): Pair<BigInteger, BigInteger> {
    if (numerator == BigInteger.ZERO) return Pair(BigInteger.ZERO, BigInteger.ONE)
    val g = gcd(numerator, denominator)
    val num = numerator * denominator.sign.toInt() / g
    val den = denominator.abs() / g
    return Pair(num, den)
}

class BigRational(numerator: BigInteger, denominator: BigInteger) : RationalScalar<BigRational> {
    private val numerator: BigInteger
    private val denominator: BigInteger
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }
    constructor(numerator: Int, denominator: Int) : this(BigInteger(numerator), BigInteger(denominator))
    override val field = BigRationalField
    override operator fun plus(other: BigRational): BigRational {
        val numerator = this.numerator * other.denominator + other.numerator * this.denominator
        val denominator = this.denominator * other.denominator
        return BigRational(numerator, denominator)
    }
    override operator fun times(other: BigRational): BigRational {
        return BigRational(this.numerator * other.numerator, this.denominator * other.denominator)
    }
    override operator fun div(other: BigRational): BigRational {
        if (other == BigRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return BigRational(this.numerator * other.denominator, this.denominator * other.denominator)
    }

    override fun toString(): String {
        return when {
            this.numerator == BigInteger.ZERO -> {
                "0"
            }
            this.denominator == BigInteger.ONE -> {
                this.numerator.toString()
            }
            else -> {
                "${this.numerator}/${this.denominator}"
            }
        }
    }

    override fun unwrap(): BigRational {
        return this
    }

    override fun equals(other: Any?): Boolean {
        // generated by Intellij Idea
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as BigRational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * this.numerator.hashCode() + this.denominator.hashCode()
    }
}

object BigRationalField : RationalField<BigRational> {
    override fun wrap(a: BigRational): Scalar<BigRational> {
        return a
    }
    override fun fromInt(n: Int): BigRational {
        return BigRational(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): Scalar<BigRational> {
        return BigRational(numerator, denominator)
    }

    override val ZERO = BigRational(BigInteger.ZERO, BigInteger.ONE)
    override val ONE = BigRational(BigInteger.ONE, BigInteger.ONE)
}
