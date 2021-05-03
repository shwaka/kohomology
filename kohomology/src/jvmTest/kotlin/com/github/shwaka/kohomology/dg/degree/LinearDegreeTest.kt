package com.github.shwaka.kohomology.dg.degree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val degreeTag = NamedTag("Degree")

class LinearDegreeTest : FreeSpec({
    tags(degreeTag)

    "tests for LinearDegree" - {
        val indeterminateList = listOf(
            DegreeIndeterminate("N", 1),
        )
        val degreeGroup = LinearDegreeGroup(indeterminateList)

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

        "(1 + 2N).toString() should be \"1 + 2N\"" {
            val degree = degreeGroup.fromList(listOf(1, 2))
            degree.toString() shouldBe "1 + 2N"
        }
    }
})
