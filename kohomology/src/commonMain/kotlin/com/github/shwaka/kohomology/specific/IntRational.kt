package com.github.shwaka.kohomology.specific

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.github.shwaka.kohomology.vectsp.PrintConfig
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

public class IntRational(numerator: Int, denominator: Int) : Scalar {
    public val numerator: Int
    public val denominator: Int
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }

    override fun isZero(): Boolean {
        return this.numerator == 0
    }

    override fun isPrintedPositively(): Boolean {
        return this.numerator >= 0
    }

    override fun toString(printConfig: PrintConfig, withSign: Boolean): String {
        return if (withSign) {
            this.toString()
        } else {
            this.toStringWithoutSign()
        }
    }

    private fun toStringWithoutSign(): String {
        val numeratorAbs = this.numerator.absoluteValue
        return when {
            this.numerator == 0 -> "0"
            this.denominator == 1 -> numeratorAbs.toString()
            else -> "$numeratorAbs/${this.denominator}"
        }
    }

    override fun toString(): String {
        return when {
            this.numerator == 0 -> "0"
            this.denominator == 1 -> this.numerator.toString()
            else -> "${this.numerator}/${this.denominator}"
        }
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
        var result = this.numerator
        result = 31 * result + this.denominator
        return result
    }
}

public object IntRationalField : Field<IntRational> {
    override val field: IntRationalField = this
    override val characteristic: Int = 0

    override val context: ScalarContext<IntRational> = ScalarContext(this)

    override fun contains(scalar: IntRational): Boolean {
        return true // Type information is sufficient
    }

    override fun add(a: IntRational, b: IntRational): IntRational {
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
        return IntRational(numerator, denominator)
    }

    override fun subtract(a: IntRational, b: IntRational): IntRational {
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
        return IntRational(numerator, denominator)
    }

    override fun multiply(a: IntRational, b: IntRational): IntRational {
        debugOnly {
            OverflowDetector.assertNoOverflow(a.numerator, b.numerator) { x, y -> x * y }
            OverflowDetector.assertNoOverflow(a.denominator, b.denominator) { x, y -> x * y }
        }
        return IntRational(a.numerator * b.numerator, a.denominator * b.denominator)
    }

    override fun divide(a: IntRational, b: IntRational): IntRational {
        debugOnly {
            OverflowDetector.assertNoOverflow(a.numerator, b.denominator) { a, b -> a * b }
            OverflowDetector.assertNoOverflow(a.denominator, b.numerator) { a, b -> a * b }
        }
        if (b == IntRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return IntRational(a.numerator * b.denominator, a.denominator * b.numerator)
    }

    override fun fromInt(n: Int): IntRational {
        return IntRational(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): IntRational {
        return IntRational(numerator, denominator)
    }

    override fun toString(): String {
        return "IntRationalField"
    }
}

public val DenseNumVectorSpaceOverIntRational: DenseNumVectorSpace<IntRational> =
    DenseNumVectorSpace.from(IntRationalField)
public val DenseMatrixSpaceOverIntRational: DenseMatrixSpace<IntRational> =
    DenseMatrixSpace.from(DenseNumVectorSpaceOverIntRational)

public val SparseNumVectorSpaceOverIntRational: SparseNumVectorSpace<IntRational> =
    SparseNumVectorSpace.from(IntRationalField)
public val SparseMatrixSpaceOverIntRational: SparseMatrixSpace<IntRational> =
    SparseMatrixSpace.from(SparseNumVectorSpaceOverIntRational)
