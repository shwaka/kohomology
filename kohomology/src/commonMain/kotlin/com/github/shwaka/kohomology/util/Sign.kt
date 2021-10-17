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

    public fun toInt(): Int {
        return this.intValue
    }

    public companion object {
        public fun fromInt(n: Int): Sign {
            return if (n.isEven()) {
                Sign.PLUS
            } else {
                Sign.MINUS
            }
        }
    }
}
