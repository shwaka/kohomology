package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val linearMapTag = NamedTag("LinearMap")

fun <S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> linearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val vectorSpace = matrixSpace.numVectorSpace
    val field = matrixSpace.field
    val vectorSpace1 = VectorSpace(vectorSpace, listOf("a", "b"))
    val vectorSpace2 = VectorSpace(vectorSpace, listOf("x", "y"))
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    "linear map test" {
        val matrix = matrixSpace.fromRows(
            listOf(two, zero),
            listOf(one, one)
        )
        val f = LinearMap(vectorSpace1, vectorSpace2, matrix)
        val v = vectorSpace1.fromCoeff(one, -one)
        val w = vectorSpace2.fromCoeff(two, zero)
        f(v) shouldBe w
    }
    "getZero should return the zero map" {
        val f = LinearMap.getZero(vectorSpace1, vectorSpace2, matrixSpace)
        val v = vectorSpace1.fromCoeff(one, two)
        f(v) shouldBe vectorSpace2.zero
    }
    "getId should return the identity map" {
        val f = LinearMap.getId(vectorSpace1, matrixSpace)
        val v = vectorSpace1.fromCoeff(one, two)
        f(v) shouldBe v
    }
}

class BigRationalLinearMapTest : StringSpec({
    tags(linearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpace(DenseNumVectorSpace.from(BigRationalField))
    include(linearMapTest(matrixSpace))
})
