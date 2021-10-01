package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val directSumTag = NamedTag("DirectSum")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> directSumTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    "direct sum of two vector spaces with distinct basis names" - {
        val vectorSpace0 = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("w1", "w2", "w3"))
        val directSum = DirectSum(listOf(vectorSpace0, vectorSpace1), matrixSpace)

        val (v1, v2) = vectorSpace0.getBasis()
        val (w1, w2, w3) = vectorSpace1.getBasis()
        val (d1, d2, d3, d4, d5) = directSum.getBasis()

        "dimension should be the sum" {
            directSum.dim shouldBe (vectorSpace0.dim + vectorSpace1.dim)
        }

        "inclusion from the first component" {
            val inclusion0 = directSum.inclusion(0)
            inclusion0(v1) shouldBe d1
            inclusion0(v2) shouldBe d2
        }

        "inclusion from the second component" {
            val inclusion1 = directSum.inclusion(1)
            inclusion1(w1) shouldBe d3
            inclusion1(w2) shouldBe d4
            inclusion1(w3) shouldBe d5
        }

        "inclusion should throw an exception on a different component" {
            val inclusion0 = directSum.inclusion(0)
            shouldThrow<IllegalArgumentException> {
                inclusion0(w1)
            }
        }
    }

    "direct sum of a vector space with itself" - {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val directSum = DirectSum(listOf(vectorSpace, vectorSpace), matrixSpace)

        val (v1, v2) = vectorSpace.getBasis()
        val (d1, d2, d3, d4) = directSum.getBasis()

        "dimension should be the sum" {
            directSum.dim shouldBe (2 * vectorSpace.dim)
        }

        "inclusion from the first component" {
            val inclusion0 = directSum.inclusion(0)
            inclusion0(v1) shouldBe d1
            inclusion0(v2) shouldBe d2
        }

        "inclusion from the second component" {
            val inclusion1 = directSum.inclusion(1)
            inclusion1(v1) shouldBe d3
            inclusion1(v2) shouldBe d4
        }
    }
}

class DirectSumTest : FreeSpec({
    tags(directSumTag, bigRationalTag)
    include(directSumTest(SparseMatrixSpaceOverBigRational))
})
