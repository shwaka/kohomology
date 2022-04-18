package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
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
        val vectorSpace3 = VectorSpace(numVectorSpace, listOf("s", "t"))
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
            "getIdentity should return the identity map" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
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
                f.isNotZero().shouldBeFalse()
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
                f.isNotZero().shouldBeTrue()
            }
            "(identity map).isIdentity() should be true" {
                val f = LinearMap.getIdentity(vectorSpace1, matrixSpace)
                f.isIdentity().shouldBeTrue()
                f.isNotIdentity().shouldBeFalse()
            }
            "(non-identity map).isNotIdentity() should be false" {
                val matrix = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, one),
                        listOf(zero, one),
                    )
                )
                val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
                f.isIdentity().shouldBeFalse()
                f.isNotIdentity().shouldBeTrue()
            }
            "test addition" {
                val matrix1 = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, zero),
                        listOf(-one, two),
                    )
                )
                val matrix2 = matrixSpace.fromRowList(
                    listOf(
                        listOf(-two, one),
                        listOf(one, zero),
                    )
                )
                val expectedMatrix = matrixSpace.context.run {
                    matrix1 + matrix2
                }
                val f1 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix1)
                val f2 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix2)
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, expectedMatrix)
                (f1 + f2) shouldBe expected
            }
            "test composition" {
                val matrix1 = matrixSpace.fromRowList(
                    listOf(
                        listOf(one, zero),
                        listOf(-one, two),
                    )
                )
                val matrix2 = matrixSpace.fromRowList(
                    listOf(
                        listOf(-two, one),
                        listOf(one, zero),
                    )
                )
                val expectedMatrix = matrixSpace.context.run {
                    matrix2 * matrix1
                }
                val f1 = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix1)
                val f2 = LinearMap.fromMatrix(vectorSpace2, vectorSpace3, matrixSpace, matrix2)
                val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace3, matrixSpace, expectedMatrix)
                (f2 * f1) shouldBe expected
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

class RationalLinearMapTest : FreeSpec({
    tags(linearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpaceOverRational
    include(linearMapTest(matrixSpace))
    include(linearMapEdgeCaseTest(matrixSpace))
})
