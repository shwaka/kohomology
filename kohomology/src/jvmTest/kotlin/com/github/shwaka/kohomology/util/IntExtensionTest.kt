package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.forAll

val intExtensionTag = NamedTag("IntExtension")

class IntExtensionTest : FreeSpec({
    tags(intExtensionTag)

    "test Int.isPowerOf(n)" - {
        "n.isPowerOf(n) must be true" {
            Arb.int(2, Int.MAX_VALUE).forAll { n ->
                n.isPowerOf(n)
            }
        }

        "test for valid arguments" {
            listOf(
                Triple(4, 2, true),
                Triple(8, 2, true),
                Triple(6, 2, false),
                Triple(27, 3, true),
                Triple(18, 3, false),
                Triple(36, 6, true),
                Triple(18, 6, false),
            ).forAll { (k, n, expected) ->
                k.isPowerOf(n) shouldBe expected
            }
        }

        "should throw IllegalArgumentException for invalid arguments" {
            shouldThrow<IllegalArgumentException> {
                0.isPowerOf(2)
            }
            shouldThrow<IllegalArgumentException> {
                (-2).isPowerOf(2)
            }
            shouldThrow<IllegalArgumentException> {
                2.isPowerOf(1)
            }
            shouldThrow<IllegalArgumentException> {
                2.isPowerOf(0)
            }
            shouldThrow<IllegalArgumentException> {
                2.isPowerOf(-2)
            }
        }
    }
})
