package com.github.shwaka.kohomology.dg.degree

import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int

fun IntDegreeGroup.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<IntDegree> {
    return Arb.bind(intArb) { n -> IntDegree(n) }
}

class IntDegreeTest : FreeSpec({
    tags(degreeTag)

    include(degreeTest(IntDegreeGroup, IntDegreeGroup.arb()))
})
