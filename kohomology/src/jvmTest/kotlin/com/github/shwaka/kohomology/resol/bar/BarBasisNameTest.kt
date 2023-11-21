package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.util.pow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
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

        "BarBasisName of length 1 should have degree -1" {
            bar(t0).degree shouldBe (-1)
        }

        "boundary for BarBasisName of length 0 should throw an IllegalArgumentException" {
            shouldThrow<IllegalArgumentException> {
                bar().boundary(0)
            }
        }

        "check boundaries for BarBasisName of length 1" {
            shouldThrow<IllegalArgumentException> {
                bar(t0).boundary(-1)
            }
            bar(t0).boundary(0) shouldBe bar()
            bar(t0).boundary(1) shouldBe bar()
            shouldThrow<IllegalArgumentException> {
                bar(t0).boundary(2)
            }
        }

        "check boundaries for BarBasisName of length 2" {
            shouldThrow<IllegalArgumentException> {
                bar(t1, t2).boundary(-1)
            }
            bar(t1, t2).boundary(0) shouldBe bar(t2)
            bar(t1, t2).boundary(1) shouldBe bar(t3)
            bar(t1, t2).boundary(2) shouldBe bar(t1)
            shouldThrow<IllegalArgumentException> {
                bar(t1, t2).boundary(3)
            }
        }

        "check boundaries for BarBasisName of length 3" {
            shouldThrow<IllegalArgumentException> {
                bar(t3, t1, t2).boundary(-1)
            }
            bar(t3, t1, t2).boundary(0) shouldBe bar(t1, t2)
            bar(t3, t1, t2).boundary(1) shouldBe bar(t4, t2)
            bar(t3, t1, t2).boundary(2) shouldBe bar(t3, t3)
            bar(t3, t1, t2).boundary(3) shouldBe bar(t3, t1)
            shouldThrow<IllegalArgumentException> {
                bar(t3, t1, t2).boundary(4)
            }
        }

        "cyclicGroup.getAllBarBasisName(n) should have size 5^n" {
            (0..3).forAll { n ->
                cyclicGroup.getAllBarBasisName(n) shouldHaveSize 5.pow(n)
            }
        }

        "cyclicGroup.getAllBarBasisName(n) should throw IllegalArgumentException if n < 0" {
            (-5..-1).forAll { n ->
                shouldThrow<IllegalArgumentException> {
                    cyclicGroup.getAllBarBasisName(n)
                }
            }
        }
    }
})
