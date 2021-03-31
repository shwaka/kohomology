package com.github.shwaka.kohomology.free

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val monomialTestTag = NamedTag("Monomial")

class MonomialTest : StringSpec({
    tags(monomialTestTag)

    "indeterminate list with mixed degrees is not allowed" {
        checkAll(Arb.positiveInts(), Arb.negativeInts(), Arb.int()) { positiveDegree, negativeDegree, degreeForListAll ->
            val indeterminateList = listOf(
                Indeterminate("x", positiveDegree),
                Indeterminate("y", negativeDegree)
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

    "positive degrees should be allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("x", 2),
            Indeterminate("x", 3),
        )
        shouldNotThrowAny {
            Monomial.listAll(indeterminateList, 0)
        }
    }

    "negative degrees should be allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("x", -2),
            Indeterminate("x", -3),
        )
        shouldNotThrowAny {
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

    "two generators of negative even degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", -2),
            Indeterminate("y", -2),
        )
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 0), Pair(-2, 2), Pair(-3, 0), Pair(-4, 3)))
        checkAll(gen) { (degree, size) ->
            Monomial.listAll(indeterminateList, degree).size shouldBe size
        }
    }

    "two generators of odd degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
        )
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 2), Pair(2, 1), Pair(3, 0), Pair(4, 0)))
        checkAll(gen) { (degree, size) ->
            Monomial.listAll(indeterminateList, degree).size shouldBe size
        }
    }

    "two generators of negative odd degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("y", -1),
        )
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 2), Pair(-2, 1), Pair(-3, 0), Pair(-4, 0)))
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

    "listAll should return the empty list for a negative degree if the generators are positive" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 2),
        )
        checkAll(Arb.negativeInts()) { degree ->
            Monomial.listAll(indeterminateList, degree).isEmpty().shouldBeTrue()
        }
    }

    "listAll should return the empty list for a positive degree if the generators are negative" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("y", -2),
        )
        checkAll(Arb.positiveInts()) { degree ->
            Monomial.listAll(indeterminateList, degree).isEmpty().shouldBeTrue()
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
