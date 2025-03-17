package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.util.Sign
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class GMonoidFromListTest : FreeSpec({
    tags(gMonoidTag)

    val n = 5

    "basis of cohomology of complex projective space of complex dimension $n" - {
        val elements = (0..n).map { i -> SimpleGMonoidElement("c$i", 2 * i) }
        val multiplicationTable: List<List<SignedOrZero<SimpleGMonoidElement<String, IntDegree>>>> =
            (0..n).map { i ->
                (0..n).map { j ->
                    if (i + j <= n) {
                        Signed(elements[i + j], Sign.PLUS)
                    } else {
                        Zero
                    }
                }
            }
        val monoid = GMonoidFromList(elements, IntDegreeGroup, multiplicationTable, isCommutative = true)

        "check multiplication" {
            for (i in 0..n) {
                for (j in 0..n) {
                    val expected = if (i + j <= n) {
                        Signed(elements[i + j], Sign.PLUS)
                    } else {
                        Zero
                    }
                    monoid.multiply(elements[i], elements[j]) shouldBe expected
                }
            }
        }

        "isCommutative should be true" {
            monoid.isCommutative.shouldBeTrue()
        }

        "check boundedness" {
            monoid.boundedness shouldBe Boundedness(upperBound = 2 * n, lowerBound = 0)
        }
    }

    "basis (v0+v1, v0, e) of the path algebra of the quiver v0→v1" - {
        // Note that the list of elements should contain the unit.
        // This is the reason why the first element is (v0 + v1), not v1.
        val elements = listOf(
            SimpleGMonoidElement("unit", 0),
            SimpleGMonoidElement("v0", 0),
            SimpleGMonoidElement("e", 1),
        )
        val (unit, v0, e) = elements
        val multiplicationTable: List<List<SignedOrZero<SimpleGMonoidElement<String, IntDegree>>>> =
            listOf(
                listOf(
                    unit, // unit * unit = unit
                    v0, // unit * v0 = v0
                    e, // unit * e = e
                ),
                listOf(
                    v0, // v0 * unit = v0
                    v0, // v0 * v0 = v0
                    e, // v0 * e = e
                ),
                listOf(
                    e, // e * unit = e
                    null, // e * v0 = 0
                    null, // e * e = 0
                ),
            ).map { multiplicationList ->
                multiplicationList.map { result ->
                    if (result == null) {
                        Zero
                    } else {
                        Signed(result, Sign.PLUS)
                    }
                }
            }
        val monoid = GMonoidFromList(elements, IntDegreeGroup, multiplicationTable, isCommutative = false)

        "check multiplication" {
            monoid.multiply(unit, v0) shouldBe Signed(v0, Sign.PLUS)
            monoid.multiply(v0, unit) shouldBe Signed(v0, Sign.PLUS)
            monoid.multiply(v0, e) shouldBe Signed(e, Sign.PLUS)
            monoid.multiply(e, v0) shouldBe Zero
        }

        "isCommutative should be false" {
            monoid.isCommutative.shouldBeFalse()
        }

        "check boundedness" {
            monoid.boundedness shouldBe Boundedness(upperBound = 1, lowerBound = 0)
        }
    }
})
