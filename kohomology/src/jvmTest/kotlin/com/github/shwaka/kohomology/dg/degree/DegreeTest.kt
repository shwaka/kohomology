package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

suspend inline fun <D : Degree> FreeScope.degreeTestTemplate(degreeGroup: DegreeGroup<D>, degreeArb: Arb<D>) {
    "Requirements for $degreeGroup" - {
        degreeGroup.context.run {
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
        }
    }
}
