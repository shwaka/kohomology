package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.isPrime
import com.github.shwaka.kohomology.field.pow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class IntPowTest : StringSpec({
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

class IsPrimeTest : StringSpec({
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
