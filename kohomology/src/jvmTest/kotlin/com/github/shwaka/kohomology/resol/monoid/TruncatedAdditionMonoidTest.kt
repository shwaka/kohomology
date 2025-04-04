package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class TruncatedAdditionMonoidTest : FreeSpec({
    tags(finiteMonoidTag)

    val n = 5
    "test TruncatedAdditionMonoid($n)" - {
        val monoid = TruncatedAdditionMonoid(n)

        "monoid.checkMonoidAxioms() should not throw" {
            shouldNotThrow<IllegalStateException> {
                monoid.checkMonoidAxioms()
            }
        }

        "monoid.isCommutative should be true" {
            monoid.isCommutative.shouldBeTrue()
        }

        "monoid.unit.value should be 0" {
            monoid.unit.value shouldBe 0
        }

        "monoid.elements.last().value should be $n" {
            monoid.elements.last().value shouldBe n
        }

        "monoid.elements.last() should be absorbing" {
            val lastElement = monoid.elements.last()
            monoid.elements.forAll { element ->
                monoid.multiply(element, lastElement) shouldBe lastElement
                monoid.multiply(lastElement, element) shouldBe lastElement
            }
        }
    }
})
