package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val vectorTag = NamedTag("Vector")

fun <S : Scalar<S>, V : NumVector<S, V>> vectorTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
    vectorSpace.numVectorSpace.withContext {
        "addition of Vector" {
            val numVector = numVectorSpace.fromValues(one, zero, one)
            val v = vectorSpace.fromNumVector(numVector)
            val expected = vectorSpace.fromNumVector(numVectorSpace.fromValues(two, zero, two))
            (v + v) shouldBe expected
        }
        "invalid length of values should throw" {
            shouldThrow<IllegalArgumentException> {
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

fun <S : Scalar<S>, V : NumVector<S, V>> vectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    data class BasisElm(val name: String) {
        fun toStringWithParen(): String = "(${this.name})"
        override fun toString(): String = this.name
    }

    numVectorSpace.withContext {
        "custom class for basis" {
            val x = BasisElm("x")
            val y = BasisElm("y")
            val vectorSpace = VectorSpace(numVectorSpace, listOf(x, y))
            val v = vectorSpace.fromCoeff(one, zero)
            shouldNotThrowAny {
                v.toString { it.toStringWithParen() }
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

class BigRationalVectorTest : StringSpec({
    tags(vectorTag, bigRationalTag)

    val numVectorSpace = DenseNumVectorSpaceOverBigRational
    include(vectorTest(numVectorSpace))
    include(vectorSpaceTest(numVectorSpace))
})
