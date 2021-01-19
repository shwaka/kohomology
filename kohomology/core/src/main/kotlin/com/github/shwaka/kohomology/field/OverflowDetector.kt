package com.github.shwaka.kohomology.field

import com.ionspin.kotlin.bignum.integer.BigInteger

interface Integer<I : Integer<I>> {
    operator fun plus(other: Integer<*>): I
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

    override fun toWrappedBigInteger(): WrappedBigInteger {
        return WrappedBigInteger(BigInteger.fromInt(this.value))
    }
}

class WrappedBigInteger(val value: BigInteger) : Integer<WrappedBigInteger> {
    override operator fun plus(other: Integer<*>): WrappedBigInteger {
        other as WrappedBigInteger
        return WrappedBigInteger(this.value + other.value)
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
    }
}
