package com.github.shwaka.kohomology.specific

import com.ionspin.kotlin.bignum.integer.BigInteger
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.math.BigInteger as JavaBigInteger

val gcdTag = NamedTag("Gcd")

data class GcdData(val a: Int, val b: Int, val gcd: Int)

fun <N> gcdTest(
    gcdFunction: (N, N) -> N,
    convertInt: (Int) -> N,
) = freeSpec {
    "gcd for positive integers" {
        val dataList = listOf(
            GcdData(1, 1, 1),
            GcdData(2, 1, 1),
            GcdData(1, 2, 1),
            GcdData(4, 2, 2),
            GcdData(2, 4, 2),
            GcdData(10, 15, 5),
            GcdData(90, 36, 18),
        )
        dataList.forAll { gcdData ->
            gcdFunction(
                convertInt(gcdData.a),
                convertInt(gcdData.b),
            ) shouldBe convertInt(gcdData.gcd)
        }
    }
}

class GcdTest : FreeSpec({
    tags(gcdTag)

    include(gcdTest(::gcd) { BigInteger(it) })
})

class IntGcdTest : FreeSpec({
    tags(gcdTag)

    include(gcdTest(::intGcd) { it })
})

class LongGcdTest : FreeSpec({
    tags(gcdTag)

    include(gcdTest(::longGcd) { it.toLong() })
})

class JavaGcdTest : FreeSpec({
    tags(gcdTag)

    include(gcdTest(::javaGcd) { JavaBigInteger.valueOf(it.toLong()) })
})
