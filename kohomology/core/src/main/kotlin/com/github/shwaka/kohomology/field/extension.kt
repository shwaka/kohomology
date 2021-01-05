package com.github.shwaka.kohomology.field

fun Int.pow(exponent: Int): Int {
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
