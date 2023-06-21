package com.github.shwaka.kohomology.specific

import com.ionspin.kotlin.bignum.integer.BigInteger
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import java.math.BigInteger as JavaBigInteger

val gcdTag = NamedTag("Gcd")

data class GcdData(val a: Int, val b: Int, val gcd: Int)

fun <N> gcdTest(
    gcdFunction: (N, N) -> N,
    fromInt: (Int) -> N,
    isPositive: N.() -> Boolean,
) = freeSpec {
    "gcd should always return positive integer" {
        // When N=Int, this fails for a=b=-2147483648
        // Hence we restrict the range.
        val range = -10000..10000
        checkAll(Arb.int(range), Arb.int(range)) { a, b ->
            if ((a != 0) && (b != 0)) {
                gcdFunction(
                    fromInt(a),
                    fromInt(b),
                ).isPositive().shouldBeTrue()
            }
        }
    }

    "gcd(n, 0) should throw ArithmeticException" {
        checkAll(Arb.int()) { n ->
            shouldThrow<ArithmeticException> {
                gcdFunction(
                    fromInt(n),
                    fromInt(0),
                )
            }
        }
    }

    "gcd(0, n) should throw ArithmeticException" {
        checkAll(Arb.int()) { n ->
            shouldThrow<ArithmeticException> {
                gcdFunction(
                    fromInt(0),
                    fromInt(n),
                )
            }
        }
    }

    val dataList = listOf(
        GcdData(1, 1, 1),
        GcdData(2, 1, 1),
        GcdData(1, 2, 1),
        GcdData(4, 2, 2),
        GcdData(2, 4, 2),
        GcdData(10, 15, 5),
        GcdData(90, 36, 18),
    )

    "gcd for positive integers" {
        dataList.forAll { gcdData ->
            gcdFunction(
                fromInt(gcdData.a),
                fromInt(gcdData.b),
            ) shouldBe fromInt(gcdData.gcd)
        }
    }

    "gcd for positive and negative integers" {
        dataList.forAll { gcdData ->
            gcdFunction(
                fromInt(-gcdData.a),
                fromInt(gcdData.b),
            ) shouldBe fromInt(gcdData.gcd)
            gcdFunction(
                fromInt(gcdData.a),
                fromInt(-gcdData.b),
            ) shouldBe fromInt(gcdData.gcd)
        }
    }

    "gcd for negative integers" {
        dataList.forAll { gcdData ->
            gcdFunction(
                fromInt(-gcdData.a),
                fromInt(-gcdData.b),
            ) shouldBe fromInt(gcdData.gcd)
        }
    }
}

class GcdTest : FreeSpec({
    tags(gcdTag)

    include(
        gcdTest(
            ::gcd,
            fromInt = { BigInteger(it) },
            isPositive = { this.isPositive }
        )
    )
})

class IntGcdTest : FreeSpec({
    tags(gcdTag)

    include(
        gcdTest(
            ::intGcd,
            fromInt = { it },
            isPositive = { this > 0 },
        )
    )
})

class LongGcdTest : FreeSpec({
    tags(gcdTag)

    include(
        gcdTest(
            ::longGcd,
            fromInt = { it.toLong() },
            isPositive = { this > 0 },
        )
    )
})

class JavaGcdTest : FreeSpec({
    tags(gcdTag)

    include(
        gcdTest(
            ::javaGcd,
            fromInt = { JavaBigInteger.valueOf(it.toLong()) },
            isPositive = { this > JavaBigInteger.valueOf(0L) }
        )
    )
})
