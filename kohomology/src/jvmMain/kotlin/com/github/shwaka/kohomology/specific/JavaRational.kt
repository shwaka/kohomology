package com.github.shwaka.kohomology.specific

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarContext
import com.github.shwaka.kohomology.linalg.ScalarContextImpl
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import java.math.BigInteger

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
    // Since there is no method such as BigInteger.times(other: Int),
    // we cannot write numerator * denominator.signum() / g.
    val num = when (denominator.signum()) {
        1 -> numerator / g
        -1 -> -numerator / g
        else -> throw Exception("This can't happen!")
    }
    val den = denominator.abs() / g
    return Pair(num, den)
}

private fun BigInteger.isZero(): Boolean {
    return this == BigInteger.ZERO
}

private val BigInteger.isPositive: Boolean
    get() = this.signum() == 1

private val BigInteger.isNegative: Boolean
    get() = this.signum() == -1

private fun BigInteger(value: Int): BigInteger {
    return BigInteger.valueOf(value.toLong())
}

public class JavaRational private constructor(public val numerator: BigInteger, public val denominator: BigInteger) : Scalar {
    public companion object {
        public operator fun invoke(numerator: BigInteger, denominator: BigInteger): JavaRational {
            if (numerator.isZero())
                return JavaRationalField.zero
            if (numerator == denominator)
                return JavaRationalField.one
            // 約分 と denominator > 0
            val red = reduce(numerator, denominator)
            return JavaRational(red.first, red.second)
        }

        public operator fun invoke(numerator: Int, denominator: Int): JavaRational {
            // ↓明示的に invoke にしないと、 private constructor が呼ばれてしまうかも？
            return JavaRational.invoke(BigInteger(numerator), BigInteger(denominator))
        }

        internal fun fromReduced(numerator: BigInteger, denominator: BigInteger): JavaRational {
            if (numerator.isZero())
                return JavaRationalField.zero
            if (numerator == denominator)
                return JavaRationalField.one
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(numerator, denominator)
            }
            return JavaRational(numerator, denominator)
        }

        internal fun fromReduced(numerator: Int, denominator: Int): JavaRational {
            // If the pair numerator and denominator is already reduced (and denominator > 0)
            debugOnly {
                this.assertReduced(BigInteger(numerator), BigInteger(denominator))
            }
            return JavaRational(BigInteger(numerator), BigInteger(denominator))
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

        other as JavaRational

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

public object JavaRationalField : Field<JavaRational> {
    override val characteristic: Int = 0

    override val context: ScalarContext<JavaRational> = ScalarContextImpl(this)

    override fun contains(scalar: JavaRational): Boolean {
        return true // Type information is sufficient
    }

    override fun add(a: JavaRational, b: JavaRational): JavaRational {
        val numerator = a.numerator * b.denominator + b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return JavaRational(numerator, denominator)
    }

    override fun subtract(a: JavaRational, b: JavaRational): JavaRational {
        val numerator = a.numerator * b.denominator - b.numerator * a.denominator
        val denominator = a.denominator * b.denominator
        return JavaRational(numerator, denominator)
    }

    override fun multiply(a: JavaRational, b: JavaRational): JavaRational {
        return JavaRational(a.numerator * b.numerator, a.denominator * b.denominator)
    }

    override fun divide(a: JavaRational, b: JavaRational): JavaRational {
        if (b == JavaRational(0, 1)) {
            throw ArithmeticException("division by zero (Rational(0, 1))")
        }
        return JavaRational(a.numerator * b.denominator, a.denominator * b.numerator)
    }

    override fun unaryMinusOf(scalar: JavaRational): JavaRational {
        return JavaRational.fromReduced(-scalar.numerator, scalar.denominator)
    }

    override fun fromInt(n: Int): JavaRational {
        return JavaRational.fromReduced(n, 1)
    }

    override fun fromIntPair(numerator: Int, denominator: Int): JavaRational {
        return JavaRational(numerator, denominator)
    }

    override val zero: JavaRational = this.fromInt(0)
    override val one: JavaRational = this.fromInt(1)
    override val two: JavaRational = this.fromInt(2)
    override val three: JavaRational = this.fromInt(3)
    override val four: JavaRational = this.fromInt(4)
    override val five: JavaRational = this.fromInt(5)

    override fun toString(): String {
        return "RationalField"
    }
}
