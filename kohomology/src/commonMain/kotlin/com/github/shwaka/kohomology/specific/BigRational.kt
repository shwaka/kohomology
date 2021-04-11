package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
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
    denominator.signum()
    val num = numerator * denominator.signum().toInt() / g
    val den = denominator.abs() / g
    return Pair(num, den)
}

class BigRational(numerator: BigInteger, denominator: BigInteger) : Scalar {
    val numerator: BigInteger
    val denominator: BigInteger
    init {
        // 約分 と denominator > 0
        // 生成時に毎回やるのは無駄な気もする
        val red = reduce(numerator, denominator)
        this.numerator = red.first
        this.denominator = red.second
    }
    constructor(numerator: Int, denominator: Int) : this(BigInteger(numerator), BigInteger(denominator))

    override fun isZero(): Boolean {
        return this.numerator.isZero()
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
        // generated by Intellij Idea
        var result = this.numerator.hashCode()
        result = 31 * result + this.denominator.hashCode()
        return result
    }
}

object BigRationalField : Field<BigRational> {
    override val field = this
    override val characteristic = 0

    override val context: ScalarContext<BigRational> = ScalarContext(this)

    override fun contains(scalar: BigRational): Boolean {
        return true // Type information is sufficient
    }

    override fun add(a: BigRational, b: BigRational): BigRational {
        val numerator = a.numerator * b.denominator + b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return BigRational(numerator, denominator)
    }

    override fun subtract(a: BigRational, b: BigRational): BigRational {
        val numerator = a.numerator * b.denominator - b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return BigRational(numerator, denominator)
    }

    override fun multiply(a: BigRational, b: BigRational): BigRational {
        return BigRational(a.numerator * b.numerator, a.denominator * b.denominator)
    }

    override fun divide(a: BigRational, b: BigRational): BigRational {
        if (b == BigRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return BigRational(a.numerator * b.denominator, a.denominator * b.numerator)
    }

    override fun fromInt(n: Int): BigRational {
        return BigRational(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): BigRational {
        return BigRational(numerator, denominator)
    }

    override fun toString(): String {
        return "BigRationalField"
    }
}

val DenseNumVectorSpaceOverBigRational = DenseNumVectorSpace.from(BigRationalField)
val DenseMatrixSpaceOverBigRational = DenseMatrixSpace.from(DenseNumVectorSpaceOverBigRational)

val SparseNumVectorSpaceOverBigRational = SparseNumVectorSpace.from(BigRationalField)
val SparseMatrixSpaceOverBigRational = SparseMatrixSpace.from(SparseNumVectorSpaceOverBigRational)
