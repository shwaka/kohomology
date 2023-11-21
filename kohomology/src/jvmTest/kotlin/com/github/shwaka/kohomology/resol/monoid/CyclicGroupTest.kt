package com.github.shwaka.kohomology.resol.monoid

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val cyclicGroupTag = NamedTag("CyclicGroup")

class CyclicGroupTest : FreeSpec({
    tags(cyclicGroupTag)

    "test cyclic group of order 5" - {
        val cyclicGroup = CyclicGroup(5)

        "cyclicGroup.isCommutative should be true" {
            cyclicGroup.isCommutative.shouldBeTrue()
        }

        "cyclicGroup.unit.value should be 0" {
            cyclicGroup.unit.value shouldBe 0
        }

        "t^1 * t^2 should be t^3" {
            cyclicGroup.multiply(
                cyclicGroup.elements[1],
                cyclicGroup.elements[2]
            ) shouldBe cyclicGroup.elements[3]
        }

        "t^1 * t^4 should be the unit" {
            cyclicGroup.multiply(
                cyclicGroup.elements[1],
                cyclicGroup.elements[4]
            ) shouldBe cyclicGroup.unit
        }

        "(t^2)^(-1) should be t^3" {
            cyclicGroup.invert(cyclicGroup.elements[2]) shouldBe
                cyclicGroup.elements[3]
        }
    }
})
