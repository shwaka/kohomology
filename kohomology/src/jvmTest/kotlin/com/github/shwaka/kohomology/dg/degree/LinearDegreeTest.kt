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
        val degreeMonoid = LinearDegreeMonoid(indeterminateList)

        "parity test" {
            degreeMonoid.fromList(listOf(1, 2)).let {
                it.isOdd().shouldBeTrue()
                it.isEven().shouldBeFalse()
            }
            degreeMonoid.fromList(listOf(0, 4)).let {
                it.isOdd().shouldBeFalse()
                it.isEven().shouldBeTrue()
            }
            degreeMonoid.fromList(listOf(1, 1)).let {
                shouldThrow<ArithmeticException> { it.isEven() }
                shouldThrow<ArithmeticException> { it.isOdd() }
            }
        }

        "(1 + 2N) + (2 + 3N) should be (3 + 5N)" {
            val degree1 = degreeMonoid.fromList(listOf(1, 2))
            val degree2 = degreeMonoid.fromList(listOf(2, 3))
            val expected = degreeMonoid.fromList(listOf(3, 5))
            degreeMonoid.context.run { degree1 + degree2 } shouldBe expected
        }

        "(1 + 2N).toString() should be \"1 + 2N\"" {
            val degree = degreeMonoid.fromList(listOf(1, 2))
            degree.toString() shouldBe "1 + 2N"
        }
    }
})
