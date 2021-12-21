package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val directSumTag = NamedTag("DirectSum")

suspend inline fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.directSumErrorTest(
    directSum: DirectSum<B, S, V, M>
) {
    "inclusion(index) should throw IndexOutOfBoundsException if index < 0" {
        (-5 until 0).forAll { index ->
            shouldThrow<IndexOutOfBoundsException> {
                directSum.inclusion(index)
            }
        }
    }

    "inclusion(index) should not throw if 0 <= index < size" {
        (0 until directSum.size).forAll { index ->
            shouldNotThrowAny {
                directSum.inclusion(index)
            }
        }
    }

    "inclusion(index) should throw IndexOutOfBoundsException if index >= size" {
        (directSum.size until directSum.size + 5).forAll { index ->
            shouldThrow<IndexOutOfBoundsException> {
                directSum.inclusion(index)
            }
        }
    }

    "projection(index) should throw IndexOutOfBoundsException if index < 0" {
        (-5 until 0).forAll { index ->
            shouldThrow<IndexOutOfBoundsException> {
                directSum.projection(index)
            }
        }
    }

    "projection(index) should not throw if 0 <= index < size" {
        (0 until directSum.size).forAll { index ->
            shouldNotThrowAny {
                directSum.projection(index)
            }
        }
    }

    "projection(index) should throw IndexOutOfBoundsException if index >= size" {
        (directSum.size until directSum.size + 5).forAll { index ->
            shouldThrow<IndexOutOfBoundsException> {
                directSum.projection(index)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> directSumTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    "direct sum of two vector spaces with distinct basis names" - {
        val vectorSpace0 = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("w1", "w2", "w3"))
        val directSum = DirectSum(listOf(vectorSpace0, vectorSpace1), matrixSpace)

        val (v1, v2) = vectorSpace0.getBasis()
        val (w1, w2, w3) = vectorSpace1.getBasis()
        val (d1, d2, d3, d4, d5) = directSum.getBasis()

        directSumErrorTest(directSum)

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

        "projection to the first component" {
            val projection0 = directSum.projection(0)
            projection0(d1) shouldBe v1
            projection0(d2) shouldBe v2
            projection0(d3).isZero().shouldBeTrue()
            projection0(d4).isZero().shouldBeTrue()
            projection0(d5).isZero().shouldBeTrue()
        }

        "projection to the second component" {
            val projection1 = directSum.projection(1)
            projection1(d1).isZero().shouldBeTrue()
            projection1(d2).isZero().shouldBeTrue()
            projection1(d3) shouldBe w1
            projection1(d4) shouldBe w2
            projection1(d5) shouldBe w3
        }

        "fromVectorList test" {
            val expected = directSum.context.run {
                d1 + d4
            }
            directSum.fromVectorList(listOf(v1, w2)) shouldBe expected
        }

        "fromVectorList(vectorList) should throw IllegalArgumentException if the size of vectorList is incorrect" {
            val vectorList = listOf(vectorSpace0.zeroVector)
            shouldThrow<IllegalArgumentException> { directSum.fromVectorList(vectorList) }
        }

        "toVectorList test" {
            val vector = directSum.context.run { d1 - 2 * d3 + d4 }
            val expected1 = vectorSpace1.context.run { -2 * w1 + w2 }
            directSum.toVectorList(vector) shouldBe listOf(v1, expected1)
        }
    }

    "direct sum of a vector space with itself" - {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val directSum = DirectSum(listOf(vectorSpace, vectorSpace), matrixSpace)

        val (v1, v2) = vectorSpace.getBasis()
        val (d1, d2, d3, d4) = directSum.getBasis()

        directSumErrorTest(directSum)

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

        "projection to the first component" {
            val projection0 = directSum.projection(0)
            projection0(d1) shouldBe v1
            projection0(d2) shouldBe v2
            projection0(d3).isZero().shouldBeTrue()
            projection0(d4).isZero().shouldBeTrue()
        }

        "projection to the second component" {
            val projection1 = directSum.projection(1)
            projection1(d1).isZero().shouldBeTrue()
            projection1(d2).isZero().shouldBeTrue()
            projection1(d3) shouldBe v1
            projection1(d4) shouldBe v2
        }
    }

    "empty direct sum" - {
        val directSum = DirectSum<StringBasisName, S, V, M>(emptyList(), matrixSpace)

        directSumErrorTest(directSum)

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

        directSumErrorTest(directSum)

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

        "projection to the non-zero vector space" {
            val projection0 = directSum.projection(0)
            projection0(d1) shouldBe v1
            projection0(d2) shouldBe v2
        }

        "projection to the zero vector space should be zero" {
            // zero vector space への LinearMap は常に zero なことが保証されているけど、
            // エラーを出さずに LinearMap のインスタンスをちゃんと生成できることの確認も込めてテストする
            directSum.projection(1).isZero().shouldBeTrue()
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

        directSumErrorTest(directSum)

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

        "projection to the first component" {
            val projection0 = directSum.projection(0)
            projection0(d1) shouldBe v1
            projection0(d2) shouldBe v2
            projection0(d3).isZero().shouldBeTrue()
            projection0(d4).isZero().shouldBeTrue()
            projection0(d5).isZero().shouldBeTrue()
            projection0(d6).isZero().shouldBeTrue()
            projection0(d7).isZero().shouldBeTrue()
            projection0(d8).isZero().shouldBeTrue()
            projection0(d9).isZero().shouldBeTrue()
            projection0(d10).isZero().shouldBeTrue()
        }

        "projection to the second component" {
            val projection1 = directSum.projection(1)
            projection1(d1).isZero().shouldBeTrue()
            projection1(d2).isZero().shouldBeTrue()
            projection1(d3) shouldBe w1
            projection1(d4) shouldBe w2
            projection1(d5) shouldBe w3
            projection1(d6).isZero().shouldBeTrue()
            projection1(d7).isZero().shouldBeTrue()
            projection1(d8).isZero().shouldBeTrue()
            projection1(d9).isZero().shouldBeTrue()
            projection1(d10).isZero().shouldBeTrue()
        }

        "projection to the third component" {
            val projection2 = directSum.projection(2)
            projection2(d1).isZero().shouldBeTrue()
            projection2(d2).isZero().shouldBeTrue()
            projection2(d3).isZero().shouldBeTrue()
            projection2(d4).isZero().shouldBeTrue()
            projection2(d5).isZero().shouldBeTrue()
            projection2(d6) shouldBe v1
            projection2(d7) shouldBe v2
            projection2(d8).isZero().shouldBeTrue()
            projection2(d9).isZero().shouldBeTrue()
            projection2(d10).isZero().shouldBeTrue()
        }

        "projection to the fourth component" {
            val projection3 = directSum.projection(3)
            projection3(d1).isZero().shouldBeTrue()
            projection3(d2).isZero().shouldBeTrue()
            projection3(d3).isZero().shouldBeTrue()
            projection3(d4).isZero().shouldBeTrue()
            projection3(d5).isZero().shouldBeTrue()
            projection3(d6).isZero().shouldBeTrue()
            projection3(d7).isZero().shouldBeTrue()
            projection3(d8) shouldBe w1
            projection3(d9) shouldBe w2
            projection3(d10) shouldBe w3
        }
    }

    "direct sum with InternalPrintConfig" - {
        val vectorSpace0 = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("w1", "w2", "w3"))
        fun getInternalPrintConfig(
            @Suppress("UNUSED_PARAMETER") printConfig: PrintConfig
        ): InternalPrintConfig<DirectSumBasis<StringBasisName>, S> {
            return InternalPrintConfig(
                basisToString = { directSumBasis ->
                    "<${directSumBasis.index}, ${directSumBasis.basisName}>"
                }
            )
        }
        val directSum = DirectSum(listOf(vectorSpace0, vectorSpace1), matrixSpace, ::getInternalPrintConfig)

        val (d1, d2, d3, d4, d5) = directSum.getBasis()

        directSumErrorTest(directSum)

        directSum.context.run {
            "test toString()" {
                (d1 + d4).toString() shouldBe "<0, v1> + <1, w2>"
                (2 * d1 + d2).toString() shouldBe "2 <0, v1> + <0, v2>"
                (d3 - d5).toString() shouldBe "<1, w1> - <1, w3>"
            }
        }
    }
}

class DirectSumTest : FreeSpec({
    tags(directSumTag, bigRationalTag)
    include(directSumTest(SparseMatrixSpaceOverBigRational))
})
