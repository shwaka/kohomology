package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val vectorTag = NamedTag("Vector")

fun <S : Scalar<S>, V : NumVector<S, V>> vectorTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    val field = numVectorSpace.field
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    "addition of Vector" {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
        val numVector = numVectorSpace.fromValues(one, zero, one)
        val v = vectorSpace.fromNumVector(numVector)
        val expected = vectorSpace.fromNumVector(numVectorSpace.fromValues(two, zero, two))
        (v + v) shouldBe expected
    }
    "invalid length of values should throw" {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
        shouldThrow<IllegalArgumentException> {
            vectorSpace.fromCoeff(zero, zero)
        }
    }
}

class BigRationalVectorTest : StringSpec({
    tags(vectorTag, bigRationalTag)

    val numVectorSpace = DenseNumVectorSpace.from(BigRationalField)
    include(vectorTest(numVectorSpace))
})
