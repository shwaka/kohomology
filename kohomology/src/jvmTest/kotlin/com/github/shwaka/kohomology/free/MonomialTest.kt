package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeMorphism
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.FreeMonoid
import com.github.shwaka.kohomology.free.monoid.FreeMonoidMorphismByDegreeChange
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.Signed
import com.github.shwaka.kohomology.free.monoid.Zero
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
                Indeterminate("x", positiveDegree),
                Indeterminate("y", negativeDegree)
            )
            shouldThrow<IllegalArgumentException> {
                FreeMonoid(indeterminateList)
            }
        }
    }

    "degree 0 is not allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", 0)
        )
        shouldThrow<IllegalArgumentException> {
            FreeMonoid(indeterminateList)
        }
    }

    "positive degrees should be allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("x", 2),
            Indeterminate("x", 3),
        )
        val monoid = FreeMonoid(indeterminateList)
        shouldNotThrowAny {
            monoid.listElements(0)
        }
    }

    "negative degrees should be allowed" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("x", -2),
            Indeterminate("x", -3),
        )
        val monoid = FreeMonoid(indeterminateList)
        shouldNotThrowAny {
            monoid.listElements(0)
        }
    }

    "two generators of even degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val monoid = FreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 0), Pair(2, 2), Pair(3, 0), Pair(4, 3)))
        checkAll(gen) { (degree, size) ->
            monoid.listElements(degree).size shouldBe size
        }
    }

    "two generators of negative even degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", -2),
            Indeterminate("y", -2),
        )
        val monoid = FreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 0), Pair(-2, 2), Pair(-3, 0), Pair(-4, 3)))
        checkAll(gen) { (degree, size) ->
            monoid.listElements(degree).size shouldBe size
        }
    }

    "two generators of odd degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
        )
        val monoid = FreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(1, 2), Pair(2, 1), Pair(3, 0), Pair(4, 0)))
        checkAll(gen) { (degree, size) ->
            monoid.listElements(degree).size shouldBe size
        }
    }

    "two generators of negative odd degrees" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("y", -1),
        )
        val monoid = FreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(Pair(0, 1), Pair(-1, 2), Pair(-2, 1), Pair(-3, 0), Pair(-4, 0)))
        checkAll(gen) { (degree, size) ->
            monoid.listElements(degree).size shouldBe size
        }
    }

    "polynomial algebra tensor exterior algebra" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 2),
        )
        val monoid = FreeMonoid(indeterminateList)
        val gen = exhaustive(listOf(0, 1, 2, 3, 4))
        checkAll(gen) { degree ->
            monoid.listElements(degree).size shouldBe 1
        }
    }

    "listElements() should return the empty list for a negative degree if the generators are positive" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 2),
        )
        val monoid = FreeMonoid(indeterminateList)
        checkAll(Arb.negativeInts()) { degree ->
            monoid.listElements(degree).isEmpty().shouldBeTrue()
        }
    }

    "listElements() should return the empty list for a positive degree if the generators are negative" {
        val indeterminateList = listOf(
            Indeterminate("x", -1),
            Indeterminate("y", -2),
        )
        val monoid = FreeMonoid(indeterminateList)
        checkAll(Arb.positiveInts()) { degree ->
            monoid.listElements(degree).isEmpty().shouldBeTrue()
        }
    }

    "listDegreesForAugmentedDegree() test" - {
        val degreeGroup = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1)
            )
        )
        val (n) = degreeGroup.generatorList
        degreeGroup.context.run {
            val indeterminateList = listOf(
                Indeterminate("a", 2 * n),
                Indeterminate("b", 2 * n),
                Indeterminate("x", 4 * n - 1),
                Indeterminate("y", 4 * n - 1),
                Indeterminate("z", 4 * n - 1),
                Indeterminate("w", 4 * n - 1),
            )
            val monoid = FreeMonoid(degreeGroup, indeterminateList)

            "listDegreesForAugmentedDegree() should return a list with distinct elements" {
                (0 until 20).forAll { degree ->
                    monoid.listDegreesForAugmentedDegree(degree).shouldBeUnique()
                }
            }
            "check results of listDegreesForAugmentedDegree() for specific degrees" {
                monoid.listDegreesForAugmentedDegree(2) shouldBe listOf(2 * n)
                monoid.listDegreesForAugmentedDegree(4) shouldBe listOf(4 * n)
                monoid.listDegreesForAugmentedDegree(6).shouldContainExactlyInAnyOrder(
                    listOf(8 * n - 2, 6 * n)
                )
                monoid.listDegreesForAugmentedDegree(12).shouldContainExactlyInAnyOrder(
                    listOf(16 * n - 4, 14 * n - 2, 12 * n)
                )
            }
        }
    }

    "multiplication test" {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
            Indeterminate("z", 2),
        )
        val monoid = FreeMonoid(indeterminateList)
        val x = Monomial(indeterminateList, listOf(1, 0, 0))
        val y = Monomial(indeterminateList, listOf(0, 1, 0))
        val z = Monomial(indeterminateList, listOf(0, 0, 1))
        val xy = Monomial(indeterminateList, listOf(1, 1, 0))
        val xz = Monomial(indeterminateList, listOf(1, 0, 1))
        val yz = Monomial(indeterminateList, listOf(0, 1, 1))
        val xyz = Monomial(indeterminateList, listOf(1, 1, 1))
        val yzz = Monomial(indeterminateList, listOf(0, 1, 2))
        monoid.multiply(x, y) shouldBe Signed(xy, Sign.PLUS)
        monoid.multiply(xy, xz) shouldBe Zero()
        monoid.multiply(xz, y) shouldBe Signed(xyz, Sign.PLUS)
        monoid.multiply(y, xz) shouldBe Signed(xyz, Sign.MINUS)
        monoid.multiply(z, yz) shouldBe Signed(yzz, Sign.PLUS)
    }

    "toString() and toTex() test" {
        val printConfigForTex = PrintConfig(PrintType.TEX)
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val unit = Monomial(indeterminateList, listOf(0, 0))
        unit.toString() shouldBe "1"
        unit.toString(printConfigForTex) shouldBe "1"
        val xy2 = Monomial(indeterminateList, listOf(1, 2))
        xy2.toString() shouldBe "xy^2"
        xy2.toString(printConfigForTex) shouldBe "xy^{2}"
    }

    "toString() and toTex() test for indeterminate names with 'tex'" {
        val printConfigForTex = PrintConfig(PrintType.TEX)
        val indeterminateList = listOf(
            Indeterminate("x", "X", 2),
            Indeterminate("y", "Y", 2),
        )
        val x = Monomial(indeterminateList, listOf(1, 0))
        x.toString() shouldBe "x"
        x.toString(printConfigForTex) shouldBe "X"
        val xy = Monomial(indeterminateList, listOf(1, 1))
        xy.toString() shouldBe "xy"
        xy.toString(printConfigForTex) shouldBe "XY"
        val xy2 = Monomial(indeterminateList, listOf(1, 2))
        xy2.toString() shouldBe "xy^2"
        xy2.toString(printConfigForTex) shouldBe "XY^{2}"
    }

    "Indeterminate.convertDegree() test" {
        val degreeGroup = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1)
            )
        )
        val (n) = degreeGroup.generatorList
        degreeGroup.context.run {
            val morphism = MultiDegreeMorphism(degreeGroup, degreeGroup, listOf(2 * n))
            val x = Indeterminate("x", 1 + n)
            x.degree shouldBe (1 + n)
            x.convertDegree(morphism).degree shouldBe (1 + 2 * n)
        }
    }

    "FreeMonoidMorphismByDegreeChange test" - {
        val degreeGroup1 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("K", 1)
            )
        )
        val (k) = degreeGroup1.generatorList
        val indeterminateList = degreeGroup1.context.run {
            listOf(
                Indeterminate("x", 2 * k),
                Indeterminate("y", 4 * k - 1),
            )
        }
        val monoid1 = FreeMonoid(degreeGroup1, indeterminateList)

        val degreeGroup2 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1),
                DegreeIndeterminate("M", 1),
            )
        )
        val (n, m) = degreeGroup2.generatorList

        val degreeMorphism = degreeGroup2.context.run {
            MultiDegreeMorphism(degreeGroup1, degreeGroup2, listOf(n + m))
        }
        val monoidMorphism = FreeMonoidMorphismByDegreeChange(monoid1, degreeMorphism)
        val monoid2 = monoidMorphism.target

        "x should be mapped to a monomial of degree (2n + 2m)" {
            val x = monoid1.fromExponentList(intArrayOf(1, 0))
            monoidMorphism(x).degree shouldBe degreeGroup2.context.run { 2 * n + 2 * m }
        }

        "y should be mapped to a monomial of degree (4n + 4m - 1)" {
            val y = monoid1.fromExponentList(intArrayOf(0, 1))
            monoidMorphism(y).degree shouldBe degreeGroup2.context.run { 4 * n + 4 * m - 1 }
        }

        "(x^2 y) should be mapped to a monomial of degree (8n + 8m - 1)" {
            val x2y = monoid1.fromExponentList(intArrayOf(2, 1))
            monoidMorphism(x2y).degree shouldBe degreeGroup2.context.run { 8 * n + 8 * m - 1 }
        }

        "FreeMonoidMorphismByDegreeChange should preserve exponentList" {
            val exponentLists = listOf(
                intArrayOf(1, 0),
                intArrayOf(0, 1),
                intArrayOf(1, 1),
                intArrayOf(3, 2),
            )
            exponentLists.forAll { exponentList ->
                monoidMorphism(monoid1.fromExponentList(exponentList)) shouldBe
                    monoid2.fromExponentList(exponentList)
            }
        }
    }
})
