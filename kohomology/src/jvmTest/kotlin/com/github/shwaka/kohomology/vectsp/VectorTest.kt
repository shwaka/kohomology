package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val vectorTag = NamedTag("Vector")

fun <S : Scalar, V : NumVector<S>> vectorTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
    vectorSpace.context.run {
        "addition of Vector" {
            val numVector = numVectorSpace.fromValueList(listOf(one, zero, one))
            val v = vectorSpace.fromNumVector(numVector)
            val expected = vectorSpace.fromNumVector(numVectorSpace.fromValueList(listOf(two, zero, two)))
            (v + v) shouldBe expected
        }
        "invalid length of values should throw" {
            shouldThrow<InvalidSizeException> {
                vectorSpace.fromCoeff(zero, zero)
            }
        }
        "multiplication of scalar" {
            val v = vectorSpace.fromCoeff(zero, two, -one)
            val expected = vectorSpace.fromCoeff(zero, four, -two)
            (v * 2) shouldBe expected
            (v * two) shouldBe expected
        }
        "multiplication of scalar with extension functions" {
            val v = vectorSpace.fromCoeff(zero, two, -one)
            val expected = vectorSpace.fromCoeff(zero, four, -two)
            (2 * v) shouldBe expected
            (two * v) shouldBe expected
        }
    }
}

fun <S : Scalar, V : NumVector<S>> vectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    data class BasisElm(val name: String) : BasisName {
        override fun toString(): String = this.name
    }

    numVectorSpace.context.run {
        "custom class for basis" {
            val x = BasisElm("x")
            val y = BasisElm("y")
            val vectorSpace = VectorSpace(numVectorSpace, listOf(x, y))
            val v = vectorSpace.fromCoeff(one, zero)
            shouldNotThrowAny {
                v.toString()
            }
        }

        "getBasis should return the correct basis" {
            val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
            val v = vectorSpace.fromCoeff(one, zero)
            val w = vectorSpace.fromCoeff(zero, one)
            vectorSpace.getBasis() shouldBe listOf(v, w)
        }
    }
}

@Suppress("UNUSED_VARIABLE")
fun <S : Scalar, V : NumVector<S>> manyBasisTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    "destructuring declaration should work for basis with many elements" {
        val basisNames = (1..15).map { "v$it" }
        val vectorSpace = VectorSpace(numVectorSpace, basisNames)
        shouldNotThrowAny {
            val (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15) = vectorSpace.getBasis()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> isBasisTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
    val (v, w) = vectorSpace.getBasis()
    vectorSpace.context.run {
        "isBasis should return true for a correct basis" {
            vectorSpace.isBasis(listOf(v, w), matrixSpace).shouldBeTrue()
            vectorSpace.isBasis(listOf(v - w, -2 * v + w), matrixSpace).shouldBeTrue()
        }
        "isBasis should return false for non-basis" {
            vectorSpace.isBasis(listOf(), matrixSpace).shouldBeFalse()
            vectorSpace.isBasis(listOf(v), matrixSpace).shouldBeFalse()
            vectorSpace.isBasis(listOf(v, v), matrixSpace).shouldBeFalse()
            vectorSpace.isBasis(listOf(v, w, v + w), matrixSpace).shouldBeFalse()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> isBasisForZeroTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val vectorSpace = VectorSpace(numVectorSpace, listOf<String>())
    vectorSpace.context.run {
        "empty list should be a basis of the zero vector space" {
            vectorSpace.isBasis(listOf(), matrixSpace).shouldBeTrue()
        }
        "non-empty list should not be a basis of the zero vector space" {
            val zeroVector = vectorSpace.zeroVector
            vectorSpace.isBasis(listOf(zeroVector), matrixSpace).shouldBeFalse()
            vectorSpace.isBasis(listOf(zeroVector, zeroVector), matrixSpace).shouldBeFalse()
        }
    }
}

class BigRationalVectorTest : StringSpec({
    tags(vectorTag, bigRationalTag)

    val numVectorSpace = DenseNumVectorSpaceOverBigRational
    include(vectorTest(numVectorSpace))
    include(vectorSpaceTest(numVectorSpace))
    include(manyBasisTest(numVectorSpace))

    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(isBasisTest(matrixSpace))
    include(isBasisForZeroTest(matrixSpace))
})
