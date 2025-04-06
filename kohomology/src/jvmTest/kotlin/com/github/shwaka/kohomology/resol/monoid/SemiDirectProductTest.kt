package com.github.shwaka.kohomology.resol.monoid

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue

class SemiDirectProductTest : FreeSpec({
    tags(finiteMonoidTag)

    "SemiDirectProduct with Aut should be a monoid" {
        val monoids = listOf(
            CyclicGroup(2),
            CyclicGroup(3),
            CyclicGroup(6),
            SymmetricGroup(3),
            TruncatedAdditionMonoid(2),
            TruncatedAdditionMonoid(3),
        )
        monoids.forAll { monoid ->
            val aut = FiniteMonoidAut(monoid)
            val sdp = SemiDirectProduct(aut.asAction())
            shouldNotThrowAny {
                sdp.checkMonoidAxioms()
            }
        }
    }

    "(ℤ/3 ⋊ ℤ/2) should be the symmetric group of order 3" {
        val monoid = CyclicGroup(3)
        val aut = FiniteMonoidAut(monoid)
        val sdp = SemiDirectProduct(aut.asAction())
        sdp.isIsomorphicTo(SymmetricGroup(3)).shouldBeTrue()
    }
})
