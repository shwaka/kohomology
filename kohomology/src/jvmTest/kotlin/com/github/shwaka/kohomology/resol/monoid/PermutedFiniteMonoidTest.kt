package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class PermutedFiniteMonoidTest : FreeSpec({
    tags(finiteMonoidTag)

    val cyclicGroup = CyclicGroup(5)
    val (zero, one, two, three, four) = cyclicGroup.elements
    val permutedElements = listOf(zero, two, one, four, three)
    val permutedFiniteMonoid = PermutedFiniteMonoid(cyclicGroup, permutedElements)

    "permutedFiniteMonoid should satisfy the monoid axioms" {
        shouldNotThrow<IllegalStateException> {
            permutedFiniteMonoid.checkMonoidAxioms()
        }
    }

    "permutedFiniteMonoid.elements should be the permuted list" {
        permutedFiniteMonoid.elements shouldBe permutedElements
    }

    "mapFromOriginalMonoid should satisfy the axioms for monoid map" {
        shouldNotThrow<IllegalStateException> {
            permutedFiniteMonoid.mapFromOriginalMonoid.checkFiniteMonoidMapAxioms()
        }
    }

    "mapFromOriginalMonoid should be bijective" {
        permutedFiniteMonoid.mapFromOriginalMonoid.isBijective().shouldBeTrue()
    }

    "mapToOriginalMonoid should satisfy the axioms for monoid map" {
        shouldNotThrow<IllegalStateException> {
            permutedFiniteMonoid.mapToOriginalMonoid.checkFiniteMonoidMapAxioms()
        }
    }

    "mapToOriginalMonoid should be bijective" {
        permutedFiniteMonoid.mapToOriginalMonoid.isBijective().shouldBeTrue()
    }
})
