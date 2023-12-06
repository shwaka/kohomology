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

    public data class False(public val cause: List<String>) : BooleanWithCause {
        init {
            require(cause.isNotEmpty()) {
                "cause must be non-empty for BooleanWithCause.False"
            }
        }

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

    public companion object {
        public fun fromCause(cause: List<String>): BooleanWithCause {
            return if (cause.isEmpty()) {
                BooleanWithCause.True
            } else {
                BooleanWithCause.False(cause)
            }
        }
    }
}
