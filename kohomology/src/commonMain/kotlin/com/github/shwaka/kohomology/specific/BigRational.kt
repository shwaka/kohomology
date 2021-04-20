package com.github.shwaka.kohomology.specific

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.DecomposedSparseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.SparseMatrixSpace
import com.github.shwaka.kohomology.linalg.SparseNumVectorSpace
import com.ionspin.kotlin.bignum.integer.BigInteger

private fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    if (a == BigInteger.ZERO || b == BigInteger.ZERO) {
        throw ArithmeticException("gcd not defined for 0")
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
    val num = numerator * denominator.signum() / g
    val den = denominator.abs() / g
    return Pair(num, den)
}

class BigRational private constructor(val numerator: BigInteger, val denominator: BigInteger) : Scalar {
    companion object {
        operator fun invoke(numerator: BigInteger, denominator: BigInteger): BigRational {
            // 約分 と denominator > 0
            val red = reduce(numerator, denominator)
            return BigRational(red.first, red.second)
        }

        operator fun invoke(numerator: Int, denominator: Int): BigRational {
            // ↓明示的に invoke にしないと、 private constructor が呼ばれてしまうかも？
            return BigRational.invoke(BigInteger(numerator), BigInteger(denominator))
        }

        internal fun fromReduced(numerator: BigInteger, denominator: BigInteger): BigRational {
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(numerator, denominator)
            }
            return BigRational(numerator, denominator)
        }

        internal fun fromReduced(numerator: Int, denominator: Int): BigRational {
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(BigInteger(numerator), BigInteger(denominator))
            }
            return BigRational(BigInteger(numerator), BigInteger(denominator))
        }

        private fun assertReduced(numerator: BigInteger, denominator: BigInteger) {
            if (denominator.isZero())
                throw IllegalArgumentException("denominator is zero")
            if (denominator.isNegative)
                throw IllegalArgumentException("denominator is negative")
            if (numerator.isZero()) {
                if (denominator != BigInteger.ONE)
                    throw IllegalArgumentException("numerator is zero, but denominator is not one")
            } else {
                val gcd = gcd(numerator, denominator)
                if (gcd != BigInteger.ONE)
                    throw IllegalArgumentException("not reduced")
            }
        }
    }

    override fun isZero(): Boolean {
        return this.numerator.isZero()
    }

    override fun isPrintedPositively(): Boolean {
        return this.numerator.isPositive || this.numerator.isZero()
    }

    override fun toStringWithoutSign(): String {
        val numeratorAbs = this.numerator.abs()
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> numeratorAbs.toString()
            else -> "$numeratorAbs/${this.denominator}"
        }
    }

    override fun toString(): String {
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> this.numerator.toString()
            else -> "${this.numerator}/${this.denominator}"
        }
    }

    override fun toTexWithoutSign(): String {
        val numeratorAbs = this.numerator.abs()
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> numeratorAbs.toString()
            else -> "\\frac{$numeratorAbs}{${this.denominator}}"
        }
    }

    override fun toTex(): String {
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> this.numerator.toString()
            else -> "\\frac{${this.numerator}}{${this.denominator}}"
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

    override fun unaryMinusOf(scalar: BigRational): BigRational {
        return BigRational.fromReduced(-scalar.numerator, scalar.denominator)
    }

    override fun fromInt(n: Int): BigRational {
        return BigRational.fromReduced(n, 1)
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
val DecomposedSparseMatrixSpaceOverBigRational = DecomposedSparseMatrixSpace.from(SparseNumVectorSpaceOverBigRational)
