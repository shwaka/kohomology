package com.github.shwaka.kohomology.field

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec

val overflowTag = NamedTag("Overflow")

class OverflowDetectorTest : StringSpec({
    tags(overflowTag)

    val maxInt = WrappedInt(Int.MAX_VALUE)
    "Int.MAX_VALUE + 1 should cause overflow" {
        val one = WrappedInt(1)
        shouldThrow<ArithmeticException> {
            OverflowDetector.assertNoOverflow(maxInt, one) { x, y -> x + y }
        }
    }

    "Int.MAX_VALUE + 0 and Int.MAX_VALUE - 1 should not cause overflow" {
        val zero = WrappedInt(0)
        val minusOne = WrappedInt(-1)
        shouldNotThrowAny {
            OverflowDetector.assertNoOverflow(maxInt, zero) { x, y -> x + y }
            OverflowDetector.assertNoOverflow(maxInt, minusOne) { x, y -> x + y }
        }
    }
})
