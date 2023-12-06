package com.github.shwaka.kohomology.util

public sealed interface BooleanWithCause {
    public operator fun times(other: BooleanWithCause): BooleanWithCause
    public fun toBoolean(): Boolean

    public object True : BooleanWithCause {
        override fun times(other: BooleanWithCause): BooleanWithCause {
            return when (other) {
                is BooleanWithCause.True -> BooleanWithCause.True
                is BooleanWithCause.False -> other
            }
        }

        override fun toBoolean(): Boolean = true
    }

    public class False(public val cause: List<String>) : BooleanWithCause {
        override fun times(other: BooleanWithCause): BooleanWithCause {
            return when (other) {
                is BooleanWithCause.True -> this
                is BooleanWithCause.False -> BooleanWithCause.False(
                    this.cause + other.cause
                )
            }
        }

        override fun toBoolean(): Boolean = false
    }
}
