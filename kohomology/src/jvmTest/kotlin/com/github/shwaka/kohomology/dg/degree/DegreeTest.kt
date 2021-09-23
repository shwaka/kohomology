package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

val degreeTag = NamedTag("Degree")

suspend inline fun <D : Degree> FreeScope.degreeTestTemplate(
    degreeGroup: DegreeGroup<D>,
    degreeArb: Arb<D>,
    intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)
) {
    "Requirements for $degreeGroup" - {
        degreeGroup.context.run {
            "group axioms" - {
                "zero should be the unit of addition" {
                    checkAll(degreeArb) { a ->
                        (a + zero) shouldBe a
                        (zero + a) shouldBe a
                    }
                }
                "addition should be associative" {
                    checkAll(degreeArb, degreeArb, degreeArb) { a, b, c ->
                        ((a + b) + c) shouldBe (a + (b + c))
                    }
                }
                "subtraction (a - a) should be zero" {
                    checkAll(degreeArb) { a ->
                        (a - a) shouldBe zero
                    }
                }
            }
            "fromInt() should be a group homomorphism" {
                checkAll(intArb, intArb) { n, m ->
                    fromInt(n + m) shouldBe (fromInt(n) + fromInt(m))
                }
            }
            "zero.isZero() should be true" {
                zero.isZero().shouldBeTrue()
            }
            "(degree.isZero() == true) implies (degree == zero)" {
                checkAll(degreeArb) { a ->
                    if (a.isZero())
                        a shouldBe zero
                }
            }
            "contains() should return true for elements of a degree group" {
                checkAll(degreeArb) { a ->
                    (a in degreeGroup).shouldBeTrue()
                }
            }
            "listOf(a, b, c).sum() should be (a + b + c)" {
                checkAll(degreeArb, degreeArb, degreeArb) { a, b, c ->
                    listOf(a, b, c).sum() shouldBe (a + b + c)
                }
            }
            "emptyList().sum() should be 0" {
                emptyList<D>().sum().isZero().shouldBeTrue()
            }
            if (degreeGroup is AugmentedDegreeGroup) {
                // The existing context is not smart-casted
                "augmentation() should be a group homomorphism" {
                    checkAll(degreeArb, degreeArb) { a, b ->
                        degreeGroup.augmentation(a + b) shouldBe
                            (degreeGroup.augmentation(a) + degreeGroup.augmentation(b))
                    }
                }
                "augmentation(zero) should be 0" {
                    degreeGroup.augmentation(degreeGroup.zero) shouldBe 0
                }
                "augmentation of each element in listAllDegrees(augmentedDegree) should be augmentedDegree" {
                    // can't use intArb since it may contain Int.MAX_VALUE, which is too large
                    checkAll(Arb.int(-20..20)) { augmentedDegree ->
                        val degreeList = degreeGroup.listAllDegrees(augmentedDegree)
                        if (degreeList.isNotEmpty())
                            checkAll(degreeList.exhaustive()) { degree ->
                                degreeGroup.augmentation(degree) shouldBe augmentedDegree
                            }
                    }
                }
            }
        }
    }
}
