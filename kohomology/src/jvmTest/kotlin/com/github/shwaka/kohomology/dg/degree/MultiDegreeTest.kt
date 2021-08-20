package com.github.shwaka.kohomology.dg.degree

import com.github.shwaka.kohomology.myArbList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map

fun MultiDegreeGroup.arb(intArb: Arb<Int> = Arb.int(Int.MIN_VALUE..Int.MAX_VALUE)): Arb<MultiDegree> {
    return myArbList(intArb, this.indeterminateList.size + 1).map { coeffList ->
        MultiDegree(this, coeffList[0], coeffList.drop(1).toIntArray())
    }
}

class MultiDegreeTest : FreeSpec({
    tags(degreeTag)

    "tests for MultiDegree" - {
        val indeterminateList = listOf(
            DegreeIndeterminate("N", 1),
        )
        val degreeGroup = MultiDegreeGroup(indeterminateList)

        degreeTestTemplate(degreeGroup, degreeGroup.arb())

        "parity test" {
            degreeGroup.fromList(listOf(1, 2)).let {
                it.isOdd().shouldBeTrue()
                it.isEven().shouldBeFalse()
            }
            degreeGroup.fromList(listOf(0, 4)).let {
                it.isOdd().shouldBeFalse()
                it.isEven().shouldBeTrue()
            }
            degreeGroup.fromList(listOf(1, 1)).let {
                shouldThrow<ArithmeticException> { it.isEven() }
                shouldThrow<ArithmeticException> { it.isOdd() }
            }
        }

        "(1 + 2N) + (2 + 3N) should be (3 + 5N)" {
            val degree1 = degreeGroup.fromList(listOf(1, 2))
            val degree2 = degreeGroup.fromList(listOf(2, 3))
            val expected = degreeGroup.fromList(listOf(3, 5))
            degreeGroup.context.run {
                (degree1 + degree2) shouldBe expected
            }
        }

        "(1) + (2N) should be (1 + 2N)" {
            val n = degreeGroup.fromList(listOf(0, 1))
            degreeGroup.context.run {
                (1 + 2 * n) shouldBe degreeGroup.fromList(listOf(1, 2))
            }
        }

        "(1) - (2N) should be (1 - 2N)" {
            val n = degreeGroup.fromList(listOf(0, 1))
            degreeGroup.context.run {
                (1 - 2 * n) shouldBe degreeGroup.fromList(listOf(1, -2))
            }
        }

        "(2N) - (1) should be (-1 + 2N)" {
            val n = degreeGroup.fromList(listOf(0, 1))
            degreeGroup.context.run {
                (2 * n - 1) shouldBe degreeGroup.fromList(listOf(-1, 2))
            }
        }

        "test toString()" - {
            "(1 + 2N).toString() should be \"1 + 2N\"" {
                val degree = degreeGroup.fromList(listOf(1, 2))
                degree.toString() shouldBe "1 + 2N"
            }

            "(3 + 0N).toString() should be \"3\"" {
                val degree = degreeGroup.fromList(listOf(3, 0))
                degree.toString() shouldBe "3"
            }

            "(2 + 1N).toString() should be \"2 + N\"" {
                val degree = degreeGroup.fromList(listOf(2, 1))
                degree.toString() shouldBe "2 + N"
            }

            "(0 + 4N).toString() should be \"4N\"" {
                val degree = degreeGroup.fromList(listOf(0, 4))
                degree.toString() shouldBe "4N"
            }

            "(0 + 0N).toString() should be \"0\"" {
                val degree = degreeGroup.fromList(listOf(0, 0))
                degree.toString() shouldBe "0"
            }
        }
    }
})
