package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.map

fun SuperDegreeGroup.arb(): Arb<SuperDegree> {
    return Arb.bool().map { bool ->
        if (bool) EvenSuperDegree else OddSuperDegree
    }
}

class SuperDegreeTest : FreeSpec({
    tags(degreeTag)

    "SuperDegreeTest" - {
        degreeTestTemplate(SuperDegreeGroup, SuperDegreeGroup.arb())

        SuperDegreeGroup.context.run {
            "1 + 1 should be 0" {
                (OddSuperDegree + OddSuperDegree) shouldBe EvenSuperDegree
            }
            "1 * 2 should be 0" {
                (OddSuperDegree * 2) shouldBe EvenSuperDegree
            }
        }

        "test SuperDegree.identifier.value" {
            OddSuperDegree.identifier.value shouldBe "1"
            EvenSuperDegree.identifier.value shouldBe "0"
        }
    }
})
