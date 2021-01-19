package com.github.shwaka.kohomology.field

import com.ionspin.kotlin.bignum.integer.BigInteger

interface Integer<I : Integer<I>> {
    operator fun plus(other: Integer<*>): I
    operator fun minus(other: Integer<*>): I
    operator fun unaryMinus(): I
    operator fun times(other: Integer<*>): I
    fun toWrappedBigInteger(): WrappedBigInteger
    fun toBigInteger(): BigInteger {
        return this.toWrappedBigInteger().value
    }
}

class WrappedInt(private val value: Int) : Integer<WrappedInt> {
    override operator fun plus(other: Integer<*>): WrappedInt {
        other as WrappedInt
        return WrappedInt(this.value + other.value)
    }

    override fun minus(other: Integer<*>): WrappedInt {
        other as WrappedInt
        return WrappedInt(this.value - other.value)
    }

    override fun unaryMinus(): WrappedInt {
        return WrappedInt(-this.value)
    }

    override fun times(other: Integer<*>): WrappedInt {
        other as WrappedInt
        return WrappedInt(this.value * other.value)
    }

    override fun toWrappedBigInteger(): WrappedBigInteger {
        return WrappedBigInteger(BigInteger.fromInt(this.value))
    }
}

class WrappedBigInteger(val value: BigInteger) : Integer<WrappedBigInteger> {
    override operator fun plus(other: Integer<*>): WrappedBigInteger {
        other as WrappedBigInteger
        return WrappedBigInteger(this.value + other.value)
    }

    override fun minus(other: Integer<*>): WrappedBigInteger {
        other as WrappedBigInteger
        return WrappedBigInteger(this.value - other.value)
    }

    override fun unaryMinus(): WrappedBigInteger {
        return WrappedBigInteger(-this.value)
    }

    override fun times(other: Integer<*>): WrappedBigInteger {
        other as WrappedBigInteger
        return WrappedBigInteger(this.value * other.value)
    }

    override fun toWrappedBigInteger(): WrappedBigInteger {
        return this
    }
}

class OverflowDetector {
    companion object {
        fun <I : Integer<I>> assertNoOverflow(n1: I, n2: I, operation: (m1: Integer<*>, m2: Integer<*>) -> Integer<*>) {
            val originalResult: Integer<*> = operation(n1, n2) // as I
            val bigIntegerResult: Integer<*> = operation(n1.toWrappedBigInteger(), n2.toWrappedBigInteger()) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException("Overflow!")
            }
        }

        fun <I : Integer<I>> assertNoOverflow(
            n1: I, n2: I, n3: I,
            operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>) -> Integer<*>) {
            val originalResult: Integer<*> = operation(n1, n2, n3) // as I
            val bigIntegerResult: Integer<*> = operation(
                n1.toWrappedBigInteger(), n2.toWrappedBigInteger(), n3.toWrappedBigInteger()
            ) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException("Overflow!")
            }
        }

        fun <I : Integer<I>> assertNoOverflow(
            n1: I, n2: I, n3: I, n4: I,
            operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>, m4: Integer<*>) -> Integer<*>) {
            val originalResult: Integer<*> = operation(n1, n2, n3, n4) // as I
            val bigIntegerResult: Integer<*> = operation(
                n1.toWrappedBigInteger(), n2.toWrappedBigInteger(), n3.toWrappedBigInteger(), n4.toWrappedBigInteger()
            ) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException("Overflow!")
            }
        }
    }
}
