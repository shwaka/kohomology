package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val linearMapTag = NamedTag("LinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> linearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "linear map test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
        matrixSpace.context.run {
            "check value" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, zero),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val v = vectorSpace1.fromCoeffList(listOf(one, -one))
                val w = vectorSpace2.fromCoeffList(listOf(two, zero))
                f(v) shouldBe w
            }
            "getZero should return the zero map" {
                val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
                val v = vectorSpace1.fromCoeffList(listOf(one, two))
                f(v) shouldBe vectorSpace2.zeroVector
            }
            "getId should return the identity map" {
                val f = LinearMap.getId(vectorSpace1, matrixSpace)
                val v = vectorSpace1.fromCoeffList(listOf(one, two))
                f(v) shouldBe v
            }
            "fromVectors test" {
                val v = vectorSpace2.fromCoeffList(listOf(one, -one))
                val w = vectorSpace2.fromCoeffList(listOf(two, zero))
                val matrix = matrixSpace.fromNumVectorList(listOf(v, w).map { it.toNumVector() })
                val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, listOf(v, w))
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f shouldBe expected
            }
            "imageContains test" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, two),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                val v = vectorSpace2.fromCoeffList(listOf(-four, -two))
                val w = vectorSpace2.fromCoeffList(listOf(one, -one))
                f.imageContains(vectorSpace2.zeroVector).shouldBeTrue()
                f.imageContains(v).shouldBeTrue()
                f.imageContains(w).shouldBeFalse()
            }
            "(zero map).isZero() should be true" {
                val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
                f.isZero().shouldBeTrue()
            }
            "(non-zero map).isZero() should be false" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(two, zero),
                        listOf(one, one)
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f.isZero().shouldBeFalse()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> linearMapEdgeCaseTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "linear map test concerning 0-dim vector space" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))
        val (a, b) = vectorSpace.getBasis()
        // val zeroVectorSpace = VectorSpace<StringBasisName, S, V>(numVectorSpace, listOf())
        val zeroVectorSpace = VectorSpace(numVectorSpace, listOf())
        val zeroVector = zeroVectorSpace.zeroVector
        matrixSpace.context.run {
            "linear map to zero" {
                val f = LinearMap.fromVectors(vectorSpace, zeroVectorSpace, matrixSpace, listOf(zeroVector, zeroVector))
                f(a).isZero().shouldBeTrue()
                f(b).isZero().shouldBeTrue()
                f.isZero().shouldBeTrue()
            }
            "linear map from zero" {
                val g = LinearMap.fromVectors(zeroVectorSpace, vectorSpace, matrixSpace, listOf())
                g(zeroVector).isZero().shouldBeTrue()
                g.isZero().shouldBeTrue()
            }
        }
    }
}

class BigRationalLinearMapTest : FreeSpec({
    tags(linearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(linearMapTest(matrixSpace))
    include(linearMapEdgeCaseTest(matrixSpace))
})
