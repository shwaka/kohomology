package com.github.shwaka.kohomology.field

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec

val overflowTag = NamedTag("Overflow")

class OverflowDetectorTest : StringSpec({
    tags(overflowTag)

    "Int.MAX_VALUE + 1 should cause overflow" {
        shouldThrow<ArithmeticException> {
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, 1) { x, y -> x + y }
        }
    }

    "Int.MAX_VALUE + 0 and Int.MAX_VALUE - 1 should not cause overflow" {
        shouldNotThrowAny {
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, 0) { x, y -> x + y }
            OverflowDetector.assertNoOverflow(Int.MAX_VALUE, -1) { x, y -> x + y }
        }
    }
})
