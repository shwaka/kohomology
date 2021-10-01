package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
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

    "empty direct sum" - {
        val directSum = DirectSum<StringBasisName, S, V, M>(emptyList(), matrixSpace)

        "dimension should be zero" {
            directSum.dim shouldBe 0
        }
    }

    "direct sum with zero vector space" - {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val zeroVectorSpace = VectorSpace(numVectorSpace, emptyList<String>())
        val directSum = DirectSum(listOf(vectorSpace, zeroVectorSpace), matrixSpace)

        val (v1, v2) = vectorSpace.getBasis()
        val (d1, d2) = directSum.getBasis()

        "dimension should be the same" {
            directSum.dim shouldBe vectorSpace.dim
        }

        "inclusion from the non-zero vector space" {
            val inclusion0 = directSum.inclusion(0)
            inclusion0(v1) shouldBe d1
            inclusion0(v2) shouldBe d2
        }

        "inclusion from the zero vector space should be zero" {
            // zero vector space からの LinearMap は常に zero なことが保証されているけど、
            // エラーを出さずに LinearMap のインスタンスをちゃんと生成できることの確認も込めてテストする
            directSum.inclusion(1).isZero().shouldBeTrue()
        }
    }

    "direct sum of many vector spaces" - {
        val vectorSpace0 = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("w1", "w2", "w3"))
        val directSum = DirectSum(
            listOf(vectorSpace0, vectorSpace1, vectorSpace0, vectorSpace1),
            matrixSpace
        )

        val (v1, v2) = vectorSpace0.getBasis()
        val (w1, w2, w3) = vectorSpace1.getBasis()
        val (d1, d2, d3, d4, d5, d6, d7, d8, d9, d10) = directSum.getBasis()

        "dimension must be the sum" {
            directSum.dim shouldBe (2 * (vectorSpace0.dim + vectorSpace1.dim))
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

        "inclusion from the third component" {
            val inclusion2 = directSum.inclusion(2)
            inclusion2(v1) shouldBe d6
            inclusion2(v2) shouldBe d7
        }

        "inclusion from the fourth component" {
            val inclusion3 = directSum.inclusion(3)
            inclusion3(w1) shouldBe d8
            inclusion3(w2) shouldBe d9
            inclusion3(w3) shouldBe d10
        }
    }
}

class DirectSumTest : FreeSpec({
    tags(directSumTag, bigRationalTag)
    include(directSumTest(SparseMatrixSpaceOverBigRational))
})
