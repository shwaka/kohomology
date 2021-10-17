package com.github.shwaka.kohomology.util

public enum class Sign(private val intValue: Int) {
    PLUS(1),
    MINUS(-1);

    public operator fun times(other: Sign): Sign {
        return if (this == other) {
            Sign.PLUS
        } else {
            Sign.MINUS
        }
    }

    public operator fun times(n: Int): Int {
        return this.toInt() * n
    }

    public operator fun Int.times(sign: Sign): Int {
        return sign * this
    }

    public fun pow(exponent: Int): Sign {
        return when (this) {
            Sign.PLUS -> Sign.PLUS
            Sign.MINUS -> if (exponent.isEven()) Sign.PLUS else Sign.MINUS
        }
    }

    public operator fun unaryMinus(): Sign {
        return when (this) {
            Sign.PLUS -> Sign.MINUS
            Sign.MINUS -> Sign.PLUS
        }
    }

    public fun toInt(): Int {
        return this.intValue
    }

    public companion object {
        /** Returns [Sign.PLUS] if [n] is even and otherwise [Sign.MINUS]. */
        public fun fromIntParity(n: Int): Sign {
            return if (n.isEven()) {
                Sign.PLUS
            } else {
                Sign.MINUS
            }
        }

        public fun fromParity(even: Boolean): Sign {
            return if (even) {
                Sign.PLUS
            } else {
                Sign.MINUS
            }
        }
    }
}
