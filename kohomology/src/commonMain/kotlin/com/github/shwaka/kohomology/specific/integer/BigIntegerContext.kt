package com.github.shwaka.kohomology.specific.integer

import com.ionspin.kotlin.bignum.integer.BigInteger

internal object BigIntegerContext : IntegerContext<BigInteger> {
    override val zero: BigInteger = BigInteger.ZERO
    override val one: BigInteger = BigInteger.ONE
    override fun add(a: BigInteger, b: BigInteger): BigInteger {
        return a + b
    }
    override fun subtract(a: BigInteger, b: BigInteger): BigInteger {
        return a - b
    }
    override fun multiply(a: BigInteger, b: BigInteger): BigInteger {
        return a * b
    }
    override fun multiply(a: BigInteger, b: Int): BigInteger {
        return a * b
    }
    override fun divide(a: BigInteger, b: BigInteger): BigInteger {
        return a / b
    }
    override fun remainder(a: BigInteger, b: BigInteger): BigInteger {
        return a % b
    }
    override fun abs(a: BigInteger): BigInteger {
        return a.abs()
    }
    override fun greaterOrEqual(greater: BigInteger, less: BigInteger): Boolean {
        return greater >= less
    }
    override fun sign(a: BigInteger): Int {
        return a.signum()
    }
}
