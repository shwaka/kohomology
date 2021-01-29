package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

val gVectorTag = NamedTag("GVector")

fun <S : Scalar<S>, V : NumVector<S, V>> gVectorTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    val field = numVectorSpace.field
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val gVectorSpace = GVectorSpace(numVectorSpace) { deg ->
        val basis = (0 until deg).map { "v$it" }
        VectorSpace(numVectorSpace, basis)
    }
    "addition test" {
        val v = gVectorSpace.fromCoeff(listOf(one, zero), 2)
        val expected = gVectorSpace.fromCoeff(listOf(two, zero), 2)
        (v + v) shouldBe expected
        (v + v) shouldNotBe v
    }
}

fun <S : Scalar<S>, V : NumVector<S, V>> gVectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = stringSpec {
    // val field = numVectorSpace.field
    // val zero = field.zero
    // val one = field.one

    "get() should return the cache if exists" {
        val gVectorSpace = GVectorSpace(numVectorSpace) { deg ->
            val basis = (0 until deg).map { "v$it" }
            VectorSpace(numVectorSpace, basis)
        }
        val vectorSpace1 = gVectorSpace[1]
        gVectorSpace[1] shouldBeSameInstanceAs vectorSpace1
    }
}

class BigRationalGVectorSpaceTest : StringSpec({
    tags(gVectorTag, bigRationalTag)

    val numVectorSpace = DenseNumVectorSpace.from(BigRationalField)
    include(gVectorTest(numVectorSpace))
    include(gVectorSpaceTest(numVectorSpace))
})
