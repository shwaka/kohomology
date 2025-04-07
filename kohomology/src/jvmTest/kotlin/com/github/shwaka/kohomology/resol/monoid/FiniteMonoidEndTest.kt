package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FiniteMonoidEndTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    val monoidList = listOf(
        CyclicGroup(2),
        CyclicGroup(3),
        SymmetricGroup(3),
        TruncatedAdditionMonoid(2),
        TruncatedAdditionMonoid(3),
    )

    "check axioms of finite monoid" - {
        for (monoid in monoidList) {
            "FiniteMonoidEnd($monoid) should satisfy monoid axioms" {
                val end = FiniteMonoidEnd(monoid)
                shouldNotThrowAny {
                    end.checkMonoidAxioms()
                }
            }
        }
    }

    "check indices of elements" - {
        for (monoid in monoidList) {
            "check FiniteMonoidEnd($monoid).elements.map { it.index }" {
                val end = FiniteMonoidEnd(monoid)
                end.elements.map { it.index } shouldBe (0 until end.size).toList()
            }
        }
    }
})
