package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

fun IntDegreeGroup.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<IntDegree> {
    return intArb.map { n -> IntDegree(n) }
}

class IntDegreeTest : FreeSpec({
    tags(degreeTag)

    "IntDegreeTest" - {
        degreeTestTemplate(IntDegreeGroup, IntDegreeGroup.arb())

        "test IntDegree.identifier.value" {
            IntDegree(0).identifier.value shouldBe "0"
            IntDegree(1).identifier.value shouldBe "1"
            IntDegree(3).identifier.value shouldBe "3"
            IntDegree(-1).identifier.value shouldBe "m1"
            IntDegree(-2).identifier.value shouldBe "m2"
        }
    }
})
