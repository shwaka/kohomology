package com.github.shwaka.kohomology.free

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val monomialTestTag = NamedTag("Monomial")

class MonomialTest : FreeSpec({
    tags(monomialTestTag)

    "indeterminate list with mixed degrees is not allowed" {
        checkAll(Arb.positiveInts(), Arb.negativeInts()) { positiveDegree, negativeDegree ->
            val indeterminateList = listOf(
                GeneralizedIndeterminate("x", positiveDegree),
                GeneralizedIndeterminate("y", negativeDegree)
            )
            shouldThrow<IllegalArgumentException> {
                GeneralizedFreeMonoid(indeterminateList)
            }
        }
    }

    "degree 0 is not allowed" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 0)
        )
        shouldThrow<IllegalArgumentException> {
            GeneralizedFreeMonoid(indeterminateList)
        }
    }

    "positive degrees should be allowed" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 1),
            GeneralizedIndeterminate("x", 2),
            GeneralizedIndeterminate("x", 3),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        shouldNotThrowAny {
            monoid.listAll(0)
        }
    }

    "negative degrees should be allowed" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", -1),
            GeneralizedIndeterminate("x", -2),
            GeneralizedIndeterminate("x", -3),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        shouldNotThrowAny {
            monoid.listAll(0)
        }
    }

    "two generators of even degrees" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 2),
            GeneralizedIndeterminate("y", 2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 0), Pair(2, 2), Pair(3, 0), Pair(4, 3)))
        checkAll(gen) { (degree, size) ->
            monoid.listAll(degree).size shouldBe size
        }
    }

    "two generators of negative even degrees" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", -2),
            GeneralizedIndeterminate("y", -2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 0), Pair(-2, 2), Pair(-3, 0), Pair(-4, 3)))
        checkAll(gen) { (degree, size) ->
            monoid.listAll(degree).size shouldBe size
        }
    }

    "two generators of odd degrees" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 1),
            GeneralizedIndeterminate("y", 1),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 2), Pair(2, 1), Pair(3, 0), Pair(4, 0)))
        checkAll(gen) { (degree, size) ->
            monoid.listAll(degree).size shouldBe size
        }
    }

    "two generators of negative odd degrees" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", -1),
            GeneralizedIndeterminate("y", -1),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 2), Pair(-2, 1), Pair(-3, 0), Pair(-4, 0)))
        checkAll(gen) { (degree, size) ->
            monoid.listAll(degree).size shouldBe size
        }
    }

    "polynomial algebra tensor exterior algebra" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 1),
            GeneralizedIndeterminate("y", 2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(0, 1, 2, 3, 4))
        checkAll(gen) { degree ->
            monoid.listAll(degree).size shouldBe 1
        }
    }

    "listAll should return the empty list for a negative degree if the generators are positive" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 1),
            GeneralizedIndeterminate("y", 2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        checkAll(Arb.negativeInts()) { degree ->
            monoid.listAll(degree).isEmpty().shouldBeTrue()
        }
    }

    "listAll should return the empty list for a positive degree if the generators are negative" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", -1),
            GeneralizedIndeterminate("y", -2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        checkAll(Arb.positiveInts()) { degree ->
            monoid.listAll(degree).isEmpty().shouldBeTrue()
        }
    }

    "multiplication test" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 1),
            GeneralizedIndeterminate("y", 1),
            GeneralizedIndeterminate("z", 2),
        )
        val monoid = GeneralizedFreeMonoid(indeterminateList)
        val x = GeneralizedMonomial(indeterminateList, listOf(1, 0, 0))
        val y = GeneralizedMonomial(indeterminateList, listOf(0, 1, 0))
        val z = GeneralizedMonomial(indeterminateList, listOf(0, 0, 1))
        val xy = GeneralizedMonomial(indeterminateList, listOf(1, 1, 0))
        val xz = GeneralizedMonomial(indeterminateList, listOf(1, 0, 1))
        val yz = GeneralizedMonomial(indeterminateList, listOf(0, 1, 1))
        val xyz = GeneralizedMonomial(indeterminateList, listOf(1, 1, 1))
        val yzz = GeneralizedMonomial(indeterminateList, listOf(0, 1, 2))
        monoid.multiply(x, y) shouldBe NonZero(Pair(xy, 1))
        monoid.multiply(xy, xz) shouldBe Zero()
        monoid.multiply(xz, y) shouldBe NonZero(Pair(xyz, 1))
        monoid.multiply(y, xz) shouldBe NonZero(Pair(xyz, -1))
        monoid.multiply(z, yz) shouldBe NonZero(Pair(yzz, 1))
    }

    "toString() and toTex() test" {
        val indeterminateList = listOf(
            GeneralizedIndeterminate("x", 2),
            GeneralizedIndeterminate("y", 2),
        )
        val unit = GeneralizedMonomial(indeterminateList, listOf(0, 0))
        unit.toString() shouldBe "1"
        unit.toTex() shouldBe "1"
        val xy2 = GeneralizedMonomial(indeterminateList, listOf(1, 2))
        xy2.toString() shouldBe "xy^2"
        xy2.toTex() shouldBe "xy^{2}"
    }
})
