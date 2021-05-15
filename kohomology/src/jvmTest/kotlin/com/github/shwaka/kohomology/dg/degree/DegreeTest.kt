package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

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
            }
        }
    }
}
