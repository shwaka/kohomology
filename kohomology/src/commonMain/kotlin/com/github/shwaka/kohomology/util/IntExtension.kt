package com.github.shwaka.kohomology.util

public fun Int.pow(exponent: Int): Int {
    return when {
        exponent == 0 -> 1
        exponent == 1 -> this
        exponent > 1 -> {
            val half = this.pow(exponent / 2)
            val rem = if (exponent % 2 == 1) this else 1
            half * half * rem
        }
        exponent < 0 -> throw ArithmeticException("exponent should be non-negative, but $exponent was given")
        else -> throw Exception("This can't happen!")
    }
}

public fun Int.isEven(): Boolean {
    return this % 2 == 0
}

public fun Int.isOdd(): Boolean {
    // We cannot use 'this % 2 == 1' since:
    // -1 % 2 = -1
    // -2 % 2 = 0
    return this % 2 != 0
}

public fun Int.isPrime(): Boolean {
    if (this <= 1) return false
    var n = 2
    while (n * n <= this) {
        if (this % n == 0) return false
        n++
    }
    return true
}
