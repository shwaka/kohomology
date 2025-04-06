package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.isPrime
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class FiniteMonoidMapEnumeratorTest : FreeSpec({
    tags(finiteMonoidTag, finiteMonoidMapTag)

    "test FiniteMonoidMapEnumerator.Naive" - {
        "the number of monoid maps from ℤ/2 to ℤ/2 should be 2" {
            val monoid = CyclicGroup(2)
            FiniteMonoidMapEnumerator.Naive.listAllMaps(monoid, monoid) shouldHaveSize 2
        }

        "the number of monoid maps from ℤ/p to ℤ/p should be p" {
            (2..7).filter { it.isPrime() }.forAll { p ->
                val monoid = CyclicGroup(p)
                FiniteMonoidMapEnumerator.Naive.listAllMaps(monoid, monoid) shouldHaveSize p
            }
        }

        "the number of monoid maps from TruncatedAdditionMonoid(1) to itself should be 2" {
            val monoid = TruncatedAdditionMonoid(1)
            FiniteMonoidMapEnumerator.Naive.listAllMaps(monoid, monoid) shouldHaveSize 2
        }

        "any element of listAllMaps() must be a monoid map" - {
            suspend fun <E : FiniteMonoidElement> testForMonoid(monoid: FiniteMonoid<E>) {
                "test with $monoid" {
                    FiniteMonoidMapEnumerator.Naive.listAllMaps(monoid, monoid).forAll { finiteMonoidMap ->
                        shouldNotThrowAny {
                            finiteMonoidMap.checkFiniteMonoidMapAxioms()
                        }
                    }
                }
            }

            val monoids = listOf(
                CyclicGroup(2),
                CyclicGroup(3),
                TruncatedAdditionMonoid(2),
            )
            for (monoid in monoids) {
                testForMonoid(monoid)
            }
        }

        "listAllMaps() must contain id and trivialMap" - {
            suspend fun <E : FiniteMonoidElement> testForMonoid(monoid: FiniteMonoid<E>) {
                "test with $monoid" {
                    val list = FiniteMonoidMapEnumerator.Naive.listAllMaps(monoid, monoid)
                    list shouldContain FiniteMonoidMap.id(monoid)
                    list shouldContain FiniteMonoidMap.trivialMap(monoid)
                }
            }

            val monoids = listOf(
                CyclicGroup(2),
                CyclicGroup(3),
                TruncatedAdditionMonoid(2),
            )
            for (monoid in monoids) {
                testForMonoid(monoid)
            }
        }
    }

    "test implementations of FiniteMonoidMapEnumerator by comparing with Naive" - {
        val enumeratorList = listOf(
            FiniteMonoidMapEnumerator.Naive,
            FiniteMonoidMapEnumerator.UnitPreserving,
        )
        val stList = listOf(
            CyclicGroup(2) to CyclicGroup(2),
            CyclicGroup(3) to CyclicGroup(3),
            TruncatedAdditionMonoid(2) to TruncatedAdditionMonoid(2),
            CyclicGroup(2) to SymmetricGroup(3),
        )
        for (enumerator in enumeratorList) {
            "${enumerator::class.simpleName}.listAllMaps should return a list of the same size as Naive.listAllMaps" {
                stList.forAll { (source, target) ->
                    enumerator.listAllMaps(source, target) shouldHaveSize
                        FiniteMonoidMapEnumerator.Naive.listAllMaps(source, target).size
                }
            }

            "${enumerator::class.simpleName}.listAllMaps should return the same value (ignoring the order of elements) as Naive.listAllMaps" {
                stList.forAll { (source, target) ->
                    enumerator.listAllMaps(source, target).toSet() shouldBe
                        FiniteMonoidMapEnumerator.Naive.listAllMaps(source, target).toSet()
                }
            }
        }
    }
})
