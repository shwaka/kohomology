package com.github.shwaka.kohomology.util

import com.ionspin.kotlin.bignum.integer.BigInteger
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

private fun BigInteger.factorial(): BigInteger {
    require(this >= 0)
    return if (this.isZero()) {
        BigInteger.ONE
    } else {
        this * (this - 1).factorial()
    }
}

private fun numOfShuffles(counts: List<Int>): Int {
    val numerator = BigInteger(counts.sum()).factorial()
    val denominator = counts.map { i -> BigInteger(i).factorial() }.fold(BigInteger.ONE) { a, b -> a * b }
    return (numerator / denominator).intValue(exactRequired = true)
}

class ShufflesTest : FreeSpec({
    "shuffles([p,q]) should have size (p+q)!/(p!q!)" {
        listOf(
            listOf(2, 1),
            listOf(1, 3),
            listOf(2, 2, 2),
            listOf(3, 1, 2),
            listOf(3, 0, 1),
            listOf(4),
        ).forAll { counts ->
            shuffles(counts) shouldHaveSize numOfShuffles(counts)
        }
    }

    "shuffles([2,1]) should be [[0,0,1], [0,1,0], [1,0,0]]" {
        shuffles(listOf(2, 1)) shouldBe listOf(
            listOf(1, 0, 0),
            listOf(0, 1, 0),
            listOf(0, 0, 1),
        )
    }
})
