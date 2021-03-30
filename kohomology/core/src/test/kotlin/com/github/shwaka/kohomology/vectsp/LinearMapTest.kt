package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val linearMapTag = NamedTag("LinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> linearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val vectorSpace1 = VectorSpace(numVectorSpace, listOf("a", "b"))
    val vectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
    matrixSpace.withContext {
        "linear map test" {
            val matrix = matrixSpace.fromRows(
                listOf(two, zero),
                listOf(one, one)
            )
            val f = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
            val v = vectorSpace1.fromCoeff(one, -one)
            val w = vectorSpace2.fromCoeff(two, zero)
            f(v) shouldBe w
        }
        "getZero should return the zero map" {
            val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
            val v = vectorSpace1.fromCoeff(one, two)
            f(v) shouldBe vectorSpace2.zeroVector
        }
        "getId should return the identity map" {
            val f = LinearMap.getId(vectorSpace1, matrixSpace)
            val v = vectorSpace1.fromCoeff(one, two)
            f(v) shouldBe v
        }
        "fromVectors test" {
            val v = vectorSpace2.fromCoeff(one, -one)
            val w = vectorSpace2.fromCoeff(two, zero)
            val matrix = matrixSpace.fromNumVectors(listOf(v, w).map { it.toNumVector() })
            val f = LinearMap.fromVectors(vectorSpace1, vectorSpace2, matrixSpace, listOf(v, w))
            val expected = LinearMap.fromMatrix(vectorSpace1, vectorSpace2, matrixSpace, matrix)
            f shouldBe expected
        }
    }
}

class BigRationalLinearMapTest : StringSpec({
    tags(linearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(linearMapTest(matrixSpace))
})
