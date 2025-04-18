package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.isPrime
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
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

        "test composition" {
            val anotherTarget = CyclicGroup(6)
            val anotherValues = (0 until 2).map { CyclicGroupElement(it * 3, 6) }
            val anotherMonoidMap = FiniteMonoidMap(target, anotherTarget, anotherValues)
            source.elements.forAll { element ->
                val composedMap = anotherMonoidMap * monoidMap
                composedMap(element) shouldBe anotherMonoidMap(monoidMap(element))
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

        "inv() should throw IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                monoidMap.inv()
            }
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

        "monoidMap.toString() should be \"FiniteMonoidMap(t^0->t^0, t^1->t^1, t^2->t^0, t^3->t^1, t^4->t^0, t^5->t^1)\"" {
            monoidMap.toString() shouldBe "FiniteMonoidMap(t^0->t^0, t^1->t^1, t^2->t^0, t^3->t^1, t^4->t^0, t^5->t^1)"
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

        "inv() should throw IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                monoidMap.inv()
            }
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

        "monoidMap.toString() should be \"FiniteMonoidMap(t^0->t^0, t^1->t^2, t^2->t^4)\"" {
            monoidMap.toString() shouldBe "FiniteMonoidMap(t^0->t^0, t^1->t^2, t^2->t^4)"
        }
    }

    "test isomorphism from ℤ/8 to itself" - {
        val monoid = CyclicGroup(8)
        val values = (0 until 8).map { CyclicGroupElement((it * 3) % 8, 8) }
        val monoidMap = FiniteMonoidMap(monoid, monoid, values)

        "monoidMap.isBijective() should be true" {
            monoidMap.isBijective().shouldBeTrue()
        }

        "monoidMap.inv() should not throw any" {
            shouldNotThrowAny {
                monoidMap.inv()
            }
        }

        "monoidMap.inv() should be the inverse map" {
            val inv = monoidMap.inv()
            monoid.elements.forAll { element ->
                monoidMap(inv(element)) shouldBe element
            }
            monoid.elements.forAll { element ->
                inv(monoidMap(element)) shouldBe element
            }
        }
    }

    "test FiniteMonoidMap.id and FiniteMonoidMap.trivialMap" - {
        suspend fun <E : FiniteMonoidElement> testForMonoid(monoid: FiniteMonoid<E>) {
            "FiniteMonoidMap.id($monoid) should map any element to itself" {
                val map = FiniteMonoidMap.id(monoid)
                monoid.elements.forAll { element ->
                    map(element) shouldBe element
                }
            }

            "FiniteMonoidMap.trivialMap($monoid) should map any element to unit" {
                val map = FiniteMonoidMap.trivialMap(monoid)
                monoid.elements.forAll { element ->
                    map(element) shouldBe monoid.unit
                }
            }
        }
        val monoids = listOf(
            CyclicGroup(2),
            CyclicGroup(3),
            CyclicGroup(6),
            SymmetricGroup(3),
        )
        for (monoid in monoids) {
            testForMonoid(monoid)
        }
    }

    "test listAllMaps" - {
        "the number of monoid maps from ℤ/2 to ℤ/2 should be 2" {
            val monoid = CyclicGroup(2)
            FiniteMonoidMap.listAllMaps(monoid, monoid) shouldHaveSize 2
        }

        "the number of monoid maps from ℤ/p to ℤ/p should be p" {
            (2..7).filter { it.isPrime() }.forAll { p ->
                val monoid = CyclicGroup(p)
                FiniteMonoidMap.listAllMaps(monoid, monoid) shouldHaveSize p
            }
        }
    }

    "test isIsomorphicTo" - {
        "any monoid should be isomorphic to itself" {
            val monoids = listOf(
                CyclicGroup(2),
                CyclicGroup(3),
                CyclicGroup(6),
                SymmetricGroup(3),
                TruncatedAdditionMonoid(2),
                TruncatedAdditionMonoid(3),
            )
            monoids.forAll { monoid ->
                monoid.isIsomorphicTo(monoid).shouldBeTrue()
            }
        }

        "test with non-isomorphic monoids" {
            val monoids = listOf(
                CyclicGroup(2) to CyclicGroup(3),
                CyclicGroup(2) to CyclicGroup(6),
                CyclicGroup(6) to SymmetricGroup(2),
                TruncatedAdditionMonoid(2) to CyclicGroup(2),
            )
            monoids.forAll { (monoid1, monoid2) ->
                monoid1.isIsomorphicTo(monoid2).shouldBeFalse()
            }
        }
    }
})
