package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class FiniteMonoidAutTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    "check axioms of finite monoid" - {
        val monoidList = listOf(
            CyclicGroup(2),
            CyclicGroup(3),
            SymmetricGroup(3),
            TruncatedAdditionMonoid(2),
            TruncatedAdditionMonoid(3),
        )
        for (monoid in monoidList) {
            "FiniteMonoidAut($monoid) should satisfy monoid axioms" {
                val aut = FiniteMonoidAut(monoid)
                shouldNotThrowAny {
                    aut.checkMonoidAxioms()
                }
            }
        }
    }

    "check indices of elements" - {
        val monoidList = listOf(
            CyclicGroup(2),
            CyclicGroup(3),
            SymmetricGroup(3),
            TruncatedAdditionMonoid(2),
            TruncatedAdditionMonoid(3),
        )
        for (monoid in monoidList) {
            "check FiniteMonoidAut($monoid).elements.map { it.index }" {
                val aut = FiniteMonoidAut(monoid)
                aut.elements.map { it.index } shouldBe (0 until aut.size).toList()
            }
        }
    }

    "check indices after inclusion" - {
        val monoidList = listOf(
            CyclicGroup(2),
            CyclicGroup(3),
            SymmetricGroup(3),
            TruncatedAdditionMonoid(2),
            TruncatedAdditionMonoid(3),
        )
        for (monoid in monoidList) {
            "check indices of elements of FiniteMonoidAut($monoid) after inclusion" {
                @Suppress("UNCHECKED_CAST")
                val aut = FiniteMonoidAut(monoid) as FiniteMonoidAut<FiniteMonoidElement>
                val end = aut.end
                aut.elements.forAll { autElement ->
                    val endElement = aut.inclusionToEnd(autElement)
                    endElement.index shouldBe end.elements.indexOfFirst { it.asMap == endElement.asMap }
                }
            }
        }
    }

    "check orders of Aut" {
        val monoidList = listOf(
            CyclicGroup(2) to 1,
            CyclicGroup(3) to 2,
            SymmetricGroup(3) to 6,
            TruncatedAdditionMonoid(2) to 1,
            TruncatedAdditionMonoid(3) to 1,
        )
        monoidList.forAll { (monoid, autOrder) ->
            FiniteMonoidAut(monoid).size shouldBe autOrder
        }
    }

    "check isomorphism type of Aut" {
        val monoidList = listOf(
            CyclicGroup(3) to CyclicGroup(2),
            CyclicGroup(5) to CyclicGroup(4),
            SymmetricGroup(3) to SymmetricGroup(3),
        )
        monoidList.forAll { (monoid, autExpected) ->
            FiniteMonoidAut(monoid).isIsomorphicTo(autExpected).shouldBeTrue()
        }
    }
})
