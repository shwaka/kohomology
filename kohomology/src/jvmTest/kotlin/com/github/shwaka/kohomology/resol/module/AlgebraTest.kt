package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val algebraTag = NamedTag("Algebra")

class AlgebraTest : FreeSpec({
    tags(algebraTag)

    "test with Q[Z/2]" - {
        val matrixSpace = SparseMatrixSpaceOverRational
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("e", "t"))
        val (e, t) = vectorSpace.getBasis()
        val multiplication = ValueBilinearMap(
            source1 = vectorSpace,
            source2 = vectorSpace,
            target = vectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(e, t), // e*(-)
                listOf(t, e), // t*(-)
            )
        )
        val algebra = Algebra(matrixSpace, vectorSpace, multiplication, unit = e, isCommutative = true)

        algebra.context.run {
            "algebra.isCommutative should be true" {
                algebra.isCommutative.shouldBeTrue()
            }

            "test multiplication" {
                (e * e) shouldBe e
                (e * t) shouldBe t
                (t * e) shouldBe t
                (t * t) shouldBe e
                ((e + t) * (e + t)) shouldBe (2 * (e + t))
            }

            "test pow" {
                algebra.zeroVector.pow(0) shouldBe e
                algebra.zeroVector.pow(1) shouldBe algebra.zeroVector
                algebra.zeroVector.pow(2) shouldBe algebra.zeroVector
                e.pow(0) shouldBe e
                e.pow(1) shouldBe e
                e.pow(2) shouldBe e
                t.pow(0) shouldBe e
                t.pow(1) shouldBe t
                t.pow(2) shouldBe e
                t.pow(1234) shouldBe e
                t.pow(12345) shouldBe t
                (e + t).pow(3) shouldBe (4 * (e + t))
            }
        }
    }
})
