package com.github.shwaka.kohomology.field

import com.ionspin.kotlin.bignum.integer.BigInteger

interface Integer<I : Integer<I>> {
    // assertNoOverflow では Integer<*> にせざるを得ないので、ここも Integer<*> にせざるを得ない
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

class WrappedLong(private val value: Long) : Integer<WrappedLong> {
    override operator fun plus(other: Integer<*>): WrappedLong {
        other as WrappedLong
        return WrappedLong(this.value + other.value)
    }

    override fun minus(other: Integer<*>): WrappedLong {
        other as WrappedLong
        return WrappedLong(this.value - other.value)
    }

    override fun unaryMinus(): WrappedLong {
        return WrappedLong(-this.value)
    }

    override fun times(other: Integer<*>): WrappedLong {
        other as WrappedLong
        return WrappedLong(this.value * other.value)
    }

    override fun toWrappedBigInteger(): WrappedBigInteger {
        return WrappedBigInteger(BigInteger.fromLong(this.value))
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
        private fun getMessage(originalResult: Integer<*>, bigIntegerResult: Integer<*>): String {
            return "[Overflow] Original result (with overflow) is ${originalResult.toBigInteger()}, " +
                "but the correct result (without overflow) is ${bigIntegerResult.toBigInteger()}"
        }

        fun <I : Integer<I>> assertNoOverflow(n1: I, n2: I, operation: (m1: Integer<*>, m2: Integer<*>) -> Integer<*>) {
            val originalResult: Integer<*> = operation(n1, n2) // as I
            val bigIntegerResult: Integer<*> = operation(n1.toWrappedBigInteger(), n2.toWrappedBigInteger()) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException(this.getMessage(originalResult, bigIntegerResult))
            }
        }

        fun <I : Integer<I>> assertNoOverflow(
            n1: I,
            n2: I,
            n3: I,
            operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>) -> Integer<*>
        ) {
            val originalResult: Integer<*> = operation(n1, n2, n3) // as I
            val bigIntegerResult: Integer<*> = operation(
                n1.toWrappedBigInteger(),
                n2.toWrappedBigInteger(),
                n3.toWrappedBigInteger()
            ) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException(this.getMessage(originalResult, bigIntegerResult))
            }
        }

        fun <I : Integer<I>> assertNoOverflow(
            n1: I,
            n2: I,
            n3: I,
            n4: I,
            operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>, m4: Integer<*>) -> Integer<*>
        ) {
            val originalResult: Integer<*> = operation(n1, n2, n3, n4) // as I
            val bigIntegerResult: Integer<*> = operation(
                n1.toWrappedBigInteger(),
                n2.toWrappedBigInteger(),
                n3.toWrappedBigInteger(),
                n4.toWrappedBigInteger()
            ) // as WrappedBigInteger
            if (originalResult.toBigInteger() != bigIntegerResult.toBigInteger()) {
                throw ArithmeticException(this.getMessage(originalResult, bigIntegerResult))
            }
        }

        fun assertNoOverflow(n1: Int, n2: Int, operation: (m1: Integer<*>, m2: Integer<*>) -> Integer<*>) {
            val w1 = WrappedInt(n1)
            val w2 = WrappedInt(n2)
            assertNoOverflow(w1, w2, operation)
        }

        fun assertNoOverflow(n1: Int, n2: Int, n3: Int, operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>) -> Integer<*>) {
            val w1 = WrappedInt(n1)
            val w2 = WrappedInt(n2)
            val w3 = WrappedInt(n3)
            assertNoOverflow(w1, w2, w3, operation)
        }

        fun assertNoOverflow(n1: Int, n2: Int, n3: Int, n4: Int, operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>, m4: Integer<*>) -> Integer<*>) {
            val w1 = WrappedInt(n1)
            val w2 = WrappedInt(n2)
            val w3 = WrappedInt(n3)
            val w4 = WrappedInt(n4)
            assertNoOverflow(w1, w2, w3, w4, operation)
        }

        fun assertNoOverflow(n1: Long, n2: Long, operation: (m1: Integer<*>, m2: Integer<*>) -> Integer<*>) {
            val w1 = WrappedLong(n1)
            val w2 = WrappedLong(n2)
            assertNoOverflow(w1, w2, operation)
        }

        fun assertNoOverflow(n1: Long, n2: Long, n3: Long, operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>) -> Integer<*>) {
            val w1 = WrappedLong(n1)
            val w2 = WrappedLong(n2)
            val w3 = WrappedLong(n3)
            assertNoOverflow(w1, w2, w3, operation)
        }

        fun assertNoOverflow(n1: Long, n2: Long, n3: Long, n4: Long, operation: (m1: Integer<*>, m2: Integer<*>, m3: Integer<*>, m4: Integer<*>) -> Integer<*>) {
            val w1 = WrappedLong(n1)
            val w2 = WrappedLong(n2)
            val w3 = WrappedLong(n3)
            val w4 = WrappedLong(n4)
            assertNoOverflow(w1, w2, w3, w4, operation)
        }
    }
}
