package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.list.component6
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException

val cyclicGroupTag = NamedTag("CyclicGroup")

class CyclicGroupTest : FreeSpec({
    tags(finiteMonoidTag, cyclicGroupTag)

    "test cyclic group of order 5" - {
        val cyclicGroup = CyclicGroup(5)

        "monoid.checkGroupAxioms() should not throw" {
            shouldNotThrow<IllegalStateException> {
                cyclicGroup.checkGroupAxioms()
            }
        }

        "cyclicGroup.isCommutative should be true" {
            cyclicGroup.isCommutative.shouldBeTrue()
        }

        "cyclicGroup.unit.value should be 0" {
            cyclicGroup.unit.value shouldBe 0
        }

        "t^1 * t^2 should be t^3" {
            cyclicGroup.multiply(
                cyclicGroup.elements[1],
                cyclicGroup.elements[2]
            ) shouldBe cyclicGroup.elements[3]
        }

        "t^1 * t^4 should be the unit" {
            cyclicGroup.multiply(
                cyclicGroup.elements[1],
                cyclicGroup.elements[4]
            ) shouldBe cyclicGroup.unit
        }

        "(t^2)^(-1) should be t^3" {
            cyclicGroup.invert(cyclicGroup.elements[2]) shouldBe
                cyclicGroup.elements[3]
        }

        "t.pow(-1) should be t^4" {
            cyclicGroup.context.run {
                cyclicGroup.elements[1].pow(-1) shouldBe cyclicGroup.elements[4]
            }
        }

        "test multiplication table" {
            cyclicGroup.multiplicationTable shouldBe
                listOf(
                    listOf(0, 1, 2, 3, 4),
                    listOf(1, 2, 3, 4, 0),
                    listOf(2, 3, 4, 0, 1),
                    listOf(3, 4, 0, 1, 2),
                    listOf(4, 0, 1, 2, 3),
                ).map { row ->
                    row.map { n: Int ->
                        CyclicGroupElement(n, 5)
                    }
                }
        }
    }

    "test getMonoidMap" - {
        val cyclicGroupOfOrder4 = CyclicGroup(4)
        val cyclicGroupOfOrder6 = CyclicGroup(6)

        val (t0, t1, t2, t3) = cyclicGroupOfOrder4.elements
        val (s0, s1, s2, s3, s4, s5) = cyclicGroupOfOrder6.elements

        "test with Z/6→Z/4 sending 1 to 2" {
            val monoidMap = cyclicGroupOfOrder6.getMonoidMap(cyclicGroupOfOrder4, t2)
            monoidMap.source shouldBe cyclicGroupOfOrder6
            monoidMap.target shouldBe cyclicGroupOfOrder4
            monoidMap(s0) shouldBe t0
            monoidMap(s1) shouldBe t2
            monoidMap(s2) shouldBe t0
            monoidMap(s3) shouldBe t2
            monoidMap(s4) shouldBe t0
            monoidMap(s5) shouldBe t2
        }

        "test invalid input" {
            shouldThrow<IllegalArgumentException> {
                cyclicGroupOfOrder6.getMonoidMap(cyclicGroupOfOrder4, t1)
            }
            shouldThrow<IllegalArgumentException> {
                cyclicGroupOfOrder6.getMonoidMap(cyclicGroupOfOrder4, t3)
            }
        }
    }
})
