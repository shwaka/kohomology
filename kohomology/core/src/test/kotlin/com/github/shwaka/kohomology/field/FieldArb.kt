package com.github.shwaka.kohomology.field

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int

fun <S : Scalar<S>> Field<S>.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<S> {
    return Arb.bind(intArb, intArb) { n, m ->
        this.withContext {
            val a = n.toScalar()
            val b = m.toScalar()
            if (b == zero) a else a / b
        }
    }
}
