package com.github.shwaka.kohomology

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next

/** [Arb] for lists of length=[n] */
fun <A> myArbList(arb: Arb<A>, n: Int): Arb<List<A>> {
    // Arb.list がおかしいので自作した
    return arbitrary { rs: RandomSource ->
        (0 until n).map { _ -> arb.next(rs) }
    }
}

/** [Arb] for lists of length∈[range] */
fun <A> myArbList(arb: Arb<A>, range: IntRange): Arb<List<A>> {
    return Arb.int(range).flatMap { n ->
        myArbList(arb, n)
    }
}
