package com.github.shwaka.kohomology.bar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val barTag = NamedTag("Bar")

class BarBasisNameTest : FreeSpec({
    tags(barTag)

    "test BarBasisName with cyclic group of order 5" - {
        val cyclicGroup = CyclicGroup(5)
        val (t0, t1, t2, t3, t4) = cyclicGroup.elements

        fun bar(vararg elements: CyclicGroupElement): BarBasisName<CyclicGroupElement> {
            return BarBasisName(cyclicGroup, elements.toList())
        }

        "empty BarBasisName should have degree 0" {
            bar().degree shouldBe 0
        }

        "boundary for BarBasisName of length 0 should throw an IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                bar().boundary(0)
            }
        }

        "check boundaries for BarBasisName of length 1" {
            bar(t0).boundary(0) shouldBe bar()
            bar(t0).boundary(1) shouldBe bar()
        }

        "check boundaries for BarBasisName of length 2" {
            bar(t1, t2).boundary(0) shouldBe bar(t2)
            bar(t1, t2).boundary(1) shouldBe bar(t3)
            bar(t1, t2).boundary(2) shouldBe bar(t1)
        }

        "check boundaries for BarBasisName of length 3" {
            bar(t3, t1, t2).boundary(0) shouldBe bar(t1, t2)
            bar(t3, t1, t2).boundary(1) shouldBe bar(t4, t2)
            bar(t3, t1, t2).boundary(2) shouldBe bar(t3, t3)
            bar(t3, t1, t2).boundary(3) shouldBe bar(t3, t1)
        }
    }
})
