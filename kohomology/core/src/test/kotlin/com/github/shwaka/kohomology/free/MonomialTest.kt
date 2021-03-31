package com.github.shwaka.kohomology.free

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val monomialTestTag = NamedTag("Monomial")

class MonomialTest : StringSpec({
    tags(monomialTestTag)

    "negative degrees are not allowed" {
        checkAll(Arb.negativeInts(), Arb.int()) { indeterminateDegree, degreeForListAll ->
            val indeterminateList = listOf(
                Indeterminate("x", indeterminateDegree)
            )
            shouldThrow<IllegalArgumentException> {
                Monomial.listAll(indeterminateList, degreeForListAll)
            }
        }
    }

    "degree 0 is not allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", 0)
        )
        shouldThrow<IllegalArgumentException> {
            Monomial.listAll(indeterminateList, 0)
        }
    }

    "two generators of even degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 0), Pair(2, 2), Pair(3, 0), Pair(4, 3)))
        checkAll(gen) { (degree, size) ->
            Monomial.listAll(indeterminateList, degree).size shouldBe size
        }
    }

    "polynomial algebra tensor exterior algebra" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 2),
        )
        val gen = exhaustive(listOf(0, 1, 2, 3, 4))
        checkAll(gen) { degree ->
            Monomial.listAll(indeterminateList, degree).size shouldBe 1
        }
    }

    "multiplication test" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
            Indeterminate("z", 2),
        )
        val x = Monomial(indeterminateList, listOf(1, 0, 0))
        val y = Monomial(indeterminateList, listOf(0, 1, 0))
        val z = Monomial(indeterminateList, listOf(0, 0, 1))
        val xy = Monomial(indeterminateList, listOf(1, 1, 0))
        val xz = Monomial(indeterminateList, listOf(1, 0, 1))
        val yz = Monomial(indeterminateList, listOf(0, 1, 1))
        val xyz = Monomial(indeterminateList, listOf(1, 1, 1))
        val yzz = Monomial(indeterminateList, listOf(0, 1, 2))
        (x * y) shouldBe Pair(xy, 1)
        (xy * xz) shouldBe null
        (xz * y) shouldBe Pair(xyz, 1)
        (y * xz) shouldBe Pair(xyz, -1)
        (z * yz) shouldBe Pair(yzz, 1)
    }
})
