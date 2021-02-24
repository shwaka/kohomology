package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F7
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

val denseNumVectorTag = NamedTag("DenseNumVector")

fun <S : Scalar<S>> denseNumVectorTest(field: Field<S>) = stringSpec {
    val numVectorSpace = DenseNumVectorSpace.from(field)
    numVectorSpace.withContext {
        "factory should return the cache if exists" {
            DenseNumVectorSpace.from(field) shouldBeSameInstanceAs numVectorSpace
        }
        "(0, 1) + (0, 1) should be (0, 2)" {
            val v = numVectorSpace.fromValues(zero, one)
            val w = numVectorSpace.fromValues(zero, two)
            (v + v) shouldBe w
        }
        "(1, 0) * 2 should be (2, 0)" {
            val v = numVectorSpace.fromValues(one, zero)
            val w = numVectorSpace.fromValues(two, zero)
            (v * two) shouldBe w
            (v * 2) shouldBe w
        }
        "2 * (1, 0) should be (2, 0)" {
            val v = numVectorSpace.fromValues(one, zero)
            val w = numVectorSpace.fromValues(two, zero)
            (two * v) shouldBe w
            (2 * v) shouldBe w
        }
        "(1, 0).dim should be 2" {
            val v = numVectorSpace.fromValues(one, zero)
            v.dim shouldBe 2
        }
        "vectorSpace.getZero(3) should be (0, 0, 0)" {
            val v = numVectorSpace.getZero(3)
            val w = numVectorSpace.fromValues(zero, zero, zero)
            v shouldBe w
        }
        "(0, 0, 0) should be different from (0, 0)" {
            val v = numVectorSpace.getZero(3)
            val w = numVectorSpace.getZero(2)
            v shouldNotBe w
        }
        "vectorSpace.get() and vectorSpace.getZero(0) should return the same element" {
            val v = numVectorSpace.fromValues()
            val w = numVectorSpace.getZero(0)
            v shouldBe w
        }
    }
}

class IntRationalDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag, intRationalTag)
    include(denseNumVectorTest(IntRationalField))
})

class BigRationalDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag, bigRationalTag)
    include(denseNumVectorTest(BigRationalField))
})

class IntModpDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag, intModpTag)
    include(denseNumVectorTest(F7))
})
