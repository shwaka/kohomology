package com.github.shwaka.kohomology.field

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec

val overflowTag = NamedTag("Overflow")

class OverflowDetectorTest : StringSpec({
    tags(overflowTag)

    "Int.MAX_VALUE + 1 should cause overflow for Int" {
        shouldThrow<ArithmeticException> {
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, 1) { x, y -> x + y }
        }
    }

    "Int.MAX_VALUE + 0 and Int.MAX_VALUE - 1 should not cause overflow for Int" {
        shouldNotThrowAny {
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, 0) { x, y -> x + y }
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, -1) { x, y -> x + y }
        }
    }

    "Long.MAX_VALUE + 1 should cause overflow for Long" {
        shouldThrow<ArithmeticException> {
            OverflowDetector.assertNoOverflow(Long.MAX_VALUE, 1L) { x, y -> x + y }
        }
    }

    "Long.MAX_VALUE + 0 and Long.MAX_VALUE - 1 should not cause overflow for Long" {
        shouldNotThrowAny {
            OverflowDetector.assertNoOverflow(Long.MAX_VALUE, 0L) { x, y -> x + y }
            OverflowDetector.assertNoOverflow(Long.MAX_VALUE, -1L) { x, y -> x + y }
        }
    }

    "Int.MAX_VALUE + Int.MAX_VALUE should not cause overflow for Long" {
        shouldNotThrowAny {
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE.toLong(), Int.MAX_VALUE.toLong()) { x, y -> x + y }
        }
    }
})
