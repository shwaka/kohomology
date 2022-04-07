package com.github.shwaka.kohomology.specific.integer

internal interface IntegerContext<T> {
    val zero: T
    val one: T
    fun add(a: T, b: T): T
    fun subtract(a: T, b: T): T
    fun multiply(a: T, b: T): T
    fun multiply(a: T, b: Int): T
    fun divide(a: T, b: T): T
    fun remainder(a: T, b: T): T
    fun abs(a: T): T
    fun greaterOrEqual(greater: T, less: T): Boolean
    fun sign(a: T): Int

    operator fun T.plus(other: T): T {
        return this@IntegerContext.add(this, other)
    }
    operator fun T.minus(other: T): T {
        return this@IntegerContext.subtract(this, other)
    }
    operator fun T.times(other: T): T {
        return this@IntegerContext.multiply(this, other)
    }
    operator fun T.times(other: Int): T {
        return this@IntegerContext.multiply(this, other)
    }
    operator fun T.div(other: T): T {
        return this@IntegerContext.divide(this, other)
    }
    operator fun T.rem(other: T): T {
        return this@IntegerContext.remainder(this, other)
    }

    fun gcd(a: T, b: T): T {
        require(a != this.zero && b != this.zero) {
            "gcd is not defined for 0"
        }
        val aAbs = this.abs(a)
        val bAbs = this.abs(b)
        return if (this.greaterOrEqual(aAbs, bAbs)) {
            this.gcdInternal(aAbs, bAbs)
        } else {
            this.gcdInternal(bAbs, aAbs)
        }
    }

    private fun gcdInternal(a: T, b: T): T {
        // arguments should satisfy a >= b >= 0
        if (b == this.zero) return a
        return this.gcdInternal(b, a % b)
    }

    fun reduce(numerator: T, denominator: T): Pair<T, T> {
        if (numerator == this.zero)
            return Pair(this.zero, this.one)
        val g = this.gcd(numerator, denominator)
        val num = numerator * this.sign(denominator) / g
        val den = this.abs(denominator) / g
        return Pair(num, den)
    }
}
