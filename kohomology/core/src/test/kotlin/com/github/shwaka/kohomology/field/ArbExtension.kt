package com.github.shwaka.kohomology.field

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

fun <S : Scalar<S>> Field<S>.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<S> {
    return intArb.map { n -> this.fromInt(n) }
}

fun IntRationalField.arb(intArb: Arb<Int> = Arb.int(-100..100)): Arb<IntRational> {
    return intArb.map { n -> this.fromInt(n) }
}

fun LongRationalField.arb(intArb: Arb<Int> = Arb.int(-100..100)): Arb<LongRational> {
    return intArb.map { n -> this.fromInt(n) }
}
