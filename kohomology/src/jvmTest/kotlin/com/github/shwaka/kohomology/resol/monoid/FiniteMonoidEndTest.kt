package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec

class FiniteMonoidEndTest : FreeSpec({
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
            "FiniteMonoidEnd($monoid) should satisfy monoid axioms" {
                val end = FiniteMonoidEnd(monoid)
                shouldNotThrowAny {
                    end.checkMonoidAxioms()
                }
            }
        }
    }
})
