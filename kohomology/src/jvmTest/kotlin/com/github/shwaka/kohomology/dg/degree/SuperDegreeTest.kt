package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.spec.style.FreeSpec
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
    }
})
