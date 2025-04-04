package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.isPrime
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

val finiteMonoidMapTag = NamedTag("FiniteMonoidMap")

class FiniteMonoidMapTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    "monoid map from cyclic group of order 6 to that of order 2" - {
        val source = CyclicGroup(6)
        val target = CyclicGroup(2)
        val values = (0 until 6).map { CyclicGroupElement(it % 2, 2) }
        val monoidMap = FiniteMonoidMap(source, target, values)

        "test invoke" {
            monoidMap(CyclicGroupElement(0, 6)) shouldBe CyclicGroupElement(0, 2)
            monoidMap(CyclicGroupElement(1, 6)) shouldBe CyclicGroupElement(1, 2)
            monoidMap(CyclicGroupElement(2, 6)) shouldBe CyclicGroupElement(0, 2)
            monoidMap(CyclicGroupElement(3, 6)) shouldBe CyclicGroupElement(1, 2)
        }

        "checkFiniteMonoidMapAxioms should not throw IllegalStateException" {
            shouldNotThrow<IllegalStateException> {
                monoidMap.checkFiniteMonoidMapAxioms()
            }
        }

        "isSurjective() should be true" {
            monoidMap.isSurjective().shouldBeTrue()
        }

        "isInjective() should be false" {
            monoidMap.isInjective().shouldBeFalse()
        }

        "isBijective() should be false" {
            monoidMap.isBijective().shouldBeFalse()
        }

        "test equals with the same MonoidMap" {
            val otherValues = (0 until 6).map { CyclicGroupElement(it % 2, 2) }
            val otherMonoidMap = FiniteMonoidMap(source, target, otherValues)
            monoidMap.equalsAsMap(otherMonoidMap).shouldBeTrue()
            otherMonoidMap.equalsAsMap(monoidMap).shouldBeTrue()
            (monoidMap == otherMonoidMap).shouldBeTrue()
            (otherMonoidMap == monoidMap).shouldBeTrue()
        }

        "test equals with a different MonoidMap" {
            val otherValues = (0 until 6).map { CyclicGroupElement(0, 2) }
            val otherMonoidMap = FiniteMonoidMap(source, target, otherValues)
            monoidMap.equalsAsMap(otherMonoidMap).shouldBeFalse()
            otherMonoidMap.equalsAsMap(monoidMap).shouldBeFalse()
            (monoidMap == otherMonoidMap).shouldBeFalse()
            (otherMonoidMap == monoidMap).shouldBeFalse()
        }
    }

    "monoid map from cyclic group of order 3 to that of order 6" - {
        val source = CyclicGroup(3)
        val target = CyclicGroup(6)
        val values = (0 until 3).map { CyclicGroupElement(it * 2, 6) }
        val monoidMap = FiniteMonoidMap(source, target, values)

        "test invoke" {
            monoidMap(CyclicGroupElement(0, 3)) shouldBe CyclicGroupElement(0, 6)
            monoidMap(CyclicGroupElement(1, 3)) shouldBe CyclicGroupElement(2, 6)
            monoidMap(CyclicGroupElement(2, 3)) shouldBe CyclicGroupElement(4, 6)
        }

        "checkFiniteMonoidMapAxioms should not throw IllegalStateException" {
            shouldNotThrow<IllegalStateException> {
                monoidMap.checkFiniteMonoidMapAxioms()
            }
        }

        "isSurjective() should be false" {
            monoidMap.isSurjective().shouldBeFalse()
        }

        "isInjective() should be true" {
            monoidMap.isInjective().shouldBeTrue()
        }

        "isBijective() should be false" {
            monoidMap.isBijective().shouldBeFalse()
        }

        "test equals with the same MonoidMap" {
            val otherValues = (0 until 3).map { CyclicGroupElement(it * 2, 6) }
            val otherMonoidMap = FiniteMonoidMap(source, target, otherValues)
            monoidMap.equalsAsMap(otherMonoidMap).shouldBeTrue()
            otherMonoidMap.equalsAsMap(monoidMap).shouldBeTrue()
            (monoidMap == otherMonoidMap).shouldBeTrue()
            (otherMonoidMap == monoidMap).shouldBeTrue()
        }

        "test equals with a different MonoidMap" {
            val otherValues = (0 until 3).map { CyclicGroupElement((it * 4) % 6, 6) }
            val otherMonoidMap = FiniteMonoidMap(source, target, otherValues)
            monoidMap.equalsAsMap(otherMonoidMap).shouldBeFalse()
            otherMonoidMap.equalsAsMap(monoidMap).shouldBeFalse()
            (monoidMap == otherMonoidMap).shouldBeFalse()
            (otherMonoidMap == monoidMap).shouldBeFalse()
        }
    }
})

class FiniteMonoidMapListAllMapsTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    "test listAllMapsNaive" - {
        "the number of monoid maps from ℤ/2 to ℤ/2 should be 2" {
            val monoid = CyclicGroup(2)
            FiniteMonoidMap.listAllMapsNaive(monoid, monoid) shouldHaveSize 2
        }

        "the number of monoid maps from ℤ/p to ℤ/p should be p" {
            // This takes 10s for p=7
            (2..5).filter { it.isPrime() }.forAll { p ->
                val monoid = CyclicGroup(p)
                FiniteMonoidMap.listAllMapsNaive(monoid, monoid) shouldHaveSize p
            }
        }
    }
})
