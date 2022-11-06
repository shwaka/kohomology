package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

val intExtensionTag = NamedTag("IntExtension")

class IntExtensionTest : FreeSpec({
    tags(intExtensionTag)

    "test Int.pow(exponent)" - {
        "1.pow(exponent) should be 1" {
            Arb.int(0, Int.MAX_VALUE).forAll { exponent ->
                1.pow(exponent) == 1
            }
        }
        "n.pow(0) should be 1" {
            Arb.int(0, Int.MAX_VALUE).forAll { base ->
                base.pow(0) == 1
            }
        }
        "n.pow(1) should be n" {
            Arb.int(0, Int.MAX_VALUE).forAll { base ->
                base.pow(1) == base
            }
        }
        "check for valid arguments" {
            listOf(
                Triple(2, 0, 1),
                Triple(2, 2, 4),
                Triple(2, 3, 8),
                Triple(3, 3, 27),
                Triple(3, 4, 81),
                Triple(-2, 0, 1),
                Triple(-2, 1, -2),
                Triple(-2, 2, 4),
                Triple(-2, 3, -8),
            ).forAll { (base, exponent, expected) ->
                base.pow(exponent) shouldBe expected
            }
        }
        "check error" {
            shouldThrow<ArithmeticException> {
                1.pow(-1)
            }
            shouldThrow<ArithmeticException> {
                1.pow(-2)
            }
        }
    }

    "test Int.isEven() and Int.isOdd()" {
        Arb.int().forAll { n -> (2 * n).isEven() }
        Arb.int().forAll { n -> !(2 * n).isOdd() }
        Arb.int().forAll { n -> !(2 * n + 1).isEven() }
        Arb.int().forAll { n -> (2 * n + 1).isOdd() }
    }

    "test Int.isPrime()" {
        listOf(
            Pair(0, false),
            Pair(1, false),
            Pair(2, true),
            Pair(3, true),
            Pair(4, false),
            Pair(5, true),
            Pair(6, false),
            Pair(7, true),
            Pair(8, false),
            Pair(9, false),
            Pair(11, true),
            Pair(13, true),
            Pair(17, true),
            Pair(57, false),
        ).forAll { (p, expected) ->
            p.isPrime() shouldBe expected
        }
    }

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
