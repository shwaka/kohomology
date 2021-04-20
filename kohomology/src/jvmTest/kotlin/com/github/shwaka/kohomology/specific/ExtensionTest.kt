package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.util.isPrime
import com.github.shwaka.kohomology.util.positiveRem
import com.github.shwaka.kohomology.util.pow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val extensionTag = NamedTag("Extension")

class IntPowTest : FreeSpec({
    tags(extensionTag)

    "3^0 should be 1" {
        3.pow(0) shouldBe 1
    }
    "3^1 should be 3" {
        3.pow(1) shouldBe 3
    }
    "2^3 should be 8" {
        2.pow(3) shouldBe 8
    }
    "0^2 should be 0" {
        0.pow(2) shouldBe 0
    }
    "0^0 should be 1" {
        0.pow(0) shouldBe 1
    }
    "(-2)^3 should be -8" {
        (-2).pow(3) shouldBe (-8)
    }
})

class PositiveRemTest : FreeSpec({
    tags(extensionTag)

    "positive remainder" {
        forAll(
            row(3, 5, 3),
            row(-1, 5, 4),
            row(-6, 5, 4),
            row(0, 7, 0),
            row(7, 7, 0),
            row(-7, 7, 0),
            row(-3, 7, 4),
            row(5, 7, 5)
        ) { a, mod, expected ->
            a.positiveRem(mod) shouldBe expected
        }
    }
})

class IsPrimeTest : FreeSpec({
    tags(extensionTag)

    "3 should be prime" {
        3.isPrime().shouldBeTrue()
    }
    "6 should not be prime" {
        6.isPrime().shouldBeFalse()
    }
    "1 should not be prime" {
        1.isPrime().shouldBeFalse()
    }
})
