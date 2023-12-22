package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MonoidRingTest : FreeSpec({
    tags(algebraTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    "monoid ring of Z/3 with coefficients in Q" - {
        val monoidRing = MonoidRing(
            CyclicGroup(3),
            matrixSpace,
        )

        val (e, t1, t2) = monoidRing.getBasis()

        "monoidRing.dim should be 3" {
            monoidRing.dim shouldBe 3
        }

        "monoidRing.isCommutative should be true" {
            monoidRing.isCommutative.shouldBeTrue()
        }

        monoidRing.context.run {
            "test vector space structure" {
                (e + t1) shouldBe (t1 + e)
                (2 * (t1 - t2)) shouldBe (2 * t1 - 2 * t2)
            }

            "test multiplication" {
                (t1 * t2) shouldBe e
                (e * t2) shouldBe t2
                (t1 * (e + 2 * t2)) shouldBe (t1 + 2 * e)
            }

            "test pow" {
                (e - t1).pow(0) shouldBe e
                monoidRing.zeroVector.pow(0) shouldBe e
                (t1 + t2).pow(3) shouldBe (2 * e + 3 * t1 + 3 * t2)
            }
        }
    }
})
