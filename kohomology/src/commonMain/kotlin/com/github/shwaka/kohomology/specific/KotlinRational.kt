package com.github.shwaka.kohomology.specific

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.ScalarContextImpl
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
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

private tailrec fun gcdInternal(a: BigInteger, b: BigInteger): BigInteger {
    // arguments should satisfy a >= b >= 0
    if (b == BigInteger.ZERO) return a
    return gcdInternal(b, a % b)
}

private fun reduce(numerator: BigInteger, denominator: BigInteger): Pair<BigInteger, BigInteger> {
    if (numerator == BigInteger.ZERO) return Pair(BigInteger.ZERO, BigInteger.ONE)
    val g = gcd(numerator, denominator)
    // Same as numerator * denominator.signum() / g,
    // but the following is faster, I believe.
    val num = when (denominator.signum()) {
        1 -> numerator / g
        -1 -> -numerator / g
        else -> throw Exception("This can't happen!")
    }
    val den = denominator.abs() / g
    return Pair(num, den)
}

public class KotlinRational private constructor(public val numerator: BigInteger, public val denominator: BigInteger) : Scalar {
    public companion object {
        public operator fun invoke(numerator: BigInteger, denominator: BigInteger): KotlinRational {
            if (numerator.isZero())
                return KotlinRationalField.zero
            if (numerator == denominator)
                return KotlinRationalField.one
            // 約分 と denominator > 0
            val red = reduce(numerator, denominator)
            return KotlinRational(red.first, red.second)
        }

        public operator fun invoke(numerator: Int, denominator: Int): KotlinRational {
            // ↓明示的に invoke にしないと、 private constructor が呼ばれてしまうかも？
            return KotlinRational.invoke(BigInteger(numerator), BigInteger(denominator))
        }

        internal fun fromReduced(numerator: BigInteger, denominator: BigInteger): KotlinRational {
            if (numerator.isZero())
                return KotlinRationalField.zero
            if (numerator == denominator)
                return KotlinRationalField.one
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(numerator, denominator)
            }
            return KotlinRational(numerator, denominator)
        }

        internal fun fromReduced(numerator: Int, denominator: Int): KotlinRational {
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(BigInteger(numerator), BigInteger(denominator))
            }
            return KotlinRational(BigInteger(numerator), BigInteger(denominator))
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

    override fun isOne(): Boolean {
        return (this.numerator == BigInteger.ONE) && (this.denominator == BigInteger.ONE)
    }

    override fun isPrintedPositively(): Boolean {
        return this.numerator.isPositive || this.numerator.isZero()
    }

    override fun toString(printConfig: PrintConfig, withSign: Boolean): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> if (withSign) this.toString() else this.toStringWithoutSign()
            PrintType.TEX -> if (withSign) this.toTex() else this.toTexWithoutSign()
        }
    }

    private fun toStringWithoutSign(): String {
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

    private fun toTexWithoutSign(): String {
        val numeratorAbs = this.numerator.abs()
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> numeratorAbs.toString()
            else -> "\\frac{$numeratorAbs}{${this.denominator}}"
        }
    }

    private fun toTex(): String {
        return when {
            this.numerator == BigInteger.ZERO -> "0"
            this.denominator == BigInteger.ONE -> this.numerator.toString()
            else -> {
                val sign: String = if (this.numerator.isNegative) "-" else ""
                "$sign\\frac{${this.numerator.abs()}}{${this.denominator}}"
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        // generated by Intellij Idea
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as KotlinRational

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

public object KotlinRationalField : Field<KotlinRational> {
    override val characteristic: Int = 0

    override val context: ScalarContext<KotlinRational> = ScalarContextImpl(this)

    override fun contains(scalar: KotlinRational): Boolean {
        return true // Type information is sufficient
    }

    override fun add(a: KotlinRational, b: KotlinRational): KotlinRational {
        val numerator = a.numerator * b.denominator + b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return KotlinRational(numerator, denominator)
    }

    override fun subtract(a: KotlinRational, b: KotlinRational): KotlinRational {
        val numerator = a.numerator * b.denominator - b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return KotlinRational(numerator, denominator)
    }

    override fun multiply(a: KotlinRational, b: KotlinRational): KotlinRational {
        return KotlinRational(a.numerator * b.numerator, a.denominator * b.denominator)
    }

    override fun divide(a: KotlinRational, b: KotlinRational): KotlinRational {
        if (b == KotlinRational(0, 1)) {
            throw ArithmeticException("division by zero (KotlinRational(0, 1))")
        }
        return KotlinRational(a.numerator * b.denominator, a.denominator * b.numerator)
    }

    override fun unaryMinusOf(scalar: KotlinRational): KotlinRational {
        return KotlinRational.fromReduced(-scalar.numerator, scalar.denominator)
    }

    override fun fromInt(n: Int): KotlinRational {
        return KotlinRational.fromReduced(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): KotlinRational {
        return KotlinRational(numerator, denominator)
    }

    override val zero: KotlinRational = this.fromInt(0)
    override val one: KotlinRational = this.fromInt(1)
    override val two: KotlinRational = this.fromInt(2)
    override val three: KotlinRational = this.fromInt(3)
    override val four: KotlinRational = this.fromInt(4)
    override val five: KotlinRational = this.fromInt(5)

    override fun toString(): String {
        return "RationalField"
    }
}
