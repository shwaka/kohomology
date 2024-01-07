package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class OppositeFiniteMonoidTest : FreeSpec({
    tags(finiteMonoidTag)

    val symmetricGroup = SymmetricGroup(4)
    val oppositeFiniteMonoid = OppositeFiniteMonoid(symmetricGroup)

    "oppositeFiniteMonoid should satisfy the monoid axioms" {
        shouldNotThrow<IllegalStateException> {
            oppositeFiniteMonoid.checkMonoidAxioms()
        }
    }

    "elements should be the same as the original one" {
        oppositeFiniteMonoid.elements shouldBe symmetricGroup.elements
    }

    "multiplication should be the reversed one" {
        oppositeFiniteMonoid.elements.forAll { a ->
            oppositeFiniteMonoid.elements.forAll { b ->
                oppositeFiniteMonoid.multiply(a, b) shouldBe symmetricGroup.multiply(b, a)
            }
        }
    }
})
