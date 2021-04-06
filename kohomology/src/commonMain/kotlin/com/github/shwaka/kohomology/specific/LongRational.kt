package com.github.shwaka.kohomology.specific

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import kotlin.Exception
import kotlin.math.absoluteValue
import kotlin.math.sign

private fun gcd(a: Long, b: Long): Long {
    if (a == 0L || b == 0L) {
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

private fun gcdInternal(a: Long, b: Long): Long {
    // arguments should satisfy a >= b >= 0
    if (b == 0L) return a
    return gcdInternal(b, a % b)
}

private fun reduce(numerator: Long, denominator: Long): Pair<Long, Long> {
    if (numerator == 0L) return Pair(0, 1)
    val g = gcd(numerator, denominator)
    val num = numerator * denominator.sign / g
    val den = denominator.absoluteValue / g
    return Pair(num, den)
}

class LongRational(numerator: Long, denominator: Long) : Scalar {
    val numerator: Long
    val denominator: Long
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }

    override fun toString(): String {
        return when {
            this.numerator == 0L -> {
                "0"
            }
            this.denominator == 1L -> {
                this.numerator.toString()
            }
            else -> {
                "${this.numerator}/${this.denominator}"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        // generated by Intellij Idea
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as LongRational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        // generated by Intellij Idea
        var result = this.numerator.hashCode()
        result = 31 * result + this.denominator.hashCode()
        return result
    }
}

object LongRationalField : Field<LongRational> {
    override val field = this
    override val characteristic = 0

    override val context: ScalarContext<LongRational> = ScalarContext(this)

    override fun contains(scalar: LongRational): Boolean {
        return true // Type information is sufficient
    }

    override fun add(a: LongRational, b: LongRational): LongRational {
        debugOnly {
            val num1 = a.numerator
            val den1 = a.denominator
            val num2 = b.numerator
            val den2 = b.denominator
            OverflowDetector.assertNoOverflow(num1, den1, num2, den2) { n1, d1, n2, d2 ->
                n1 * d2 + n2 * d1
            }
            OverflowDetector.assertNoOverflow(num1, den1, num2, den2) { _, d1, _, d2 ->
                d1 * d2
            }
        }
        val numerator = a.numerator * b.denominator + b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return LongRational(numerator, denominator)
    }

    override fun subtract(a: LongRational, b: LongRational): LongRational {
        debugOnly {
            val num1 = a.numerator
            val den1 = a.denominator
            val num2 = b.numerator
            val den2 = b.denominator
            OverflowDetector.assertNoOverflow(num1, den1, num2, den2) { n1, d1, n2, d2 ->
                n1 * d2 - n2 * d1
            }
            OverflowDetector.assertNoOverflow(num1, den1, num2, den2) { _, d1, _, d2 ->
                d1 * d2
            }
        }
        val numerator = a.numerator * b.denominator - b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return LongRational(numerator, denominator)
    }

    override fun multiply(a: LongRational, b: LongRational): LongRational {
        debugOnly {
            OverflowDetector.assertNoOverflow(a.numerator, b.numerator) { x, y -> x * y }
            OverflowDetector.assertNoOverflow(a.denominator, b.denominator) { x, y -> x * y }
        }
        return LongRational(a.numerator * b.numerator, a.denominator * b.denominator)
    }

    override fun divide(a: LongRational, b: LongRational): LongRational {
        debugOnly {
            OverflowDetector.assertNoOverflow(a.numerator, b.denominator) { a, b -> a * b }
            OverflowDetector.assertNoOverflow(a.denominator, b.numerator) { a, b -> a * b }
        }
        if (b == LongRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return LongRational(a.numerator * b.denominator, a.denominator * b.numerator)
    }

    override fun fromInt(n: Int): LongRational {
        return LongRational(n.toLong(), 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): LongRational {
        return LongRational(numerator.toLong(), denominator.toLong())
    }

    override fun toString(): String {
        return "LongRationalField"
    }
}

val DenseNumVectorSpaceOverLongRational = DenseNumVectorSpace.from(LongRationalField)
val DenseMatrixSpaceOverLongRational = DenseMatrixSpace.from(DenseNumVectorSpaceOverLongRational)
