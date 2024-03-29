package com.github.shwaka.kohomology.specific

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int

fun <S : Scalar> Field<S>.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<S> {
    return Arb.bind(intArb, intArb) { n, m ->
        this.context.run {
            val a = n.toScalar()
            val b = m.toScalar()
            if (b == zero) a else a / b
        }
    }
}
