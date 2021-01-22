package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F7
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar
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
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val vectorSpace = DenseNumVectorSpace.from(field)
    "factory should return the cache if exists" {
        DenseNumVectorSpace.from(field) shouldBeSameInstanceAs vectorSpace
    }
    "(0, 1) + (0, 1) should be (0, 2)" {
        val v = vectorSpace.get(zero, one)
        val w = vectorSpace.get(zero, two)
        (v + v) shouldBe w
    }
    "(1, 0) * 2 should be (2, 0)" {
        val v = vectorSpace.get(one, zero)
        val w = vectorSpace.get(two, zero)
        (v * two) shouldBe w
        (v * 2) shouldBe w
    }
    "2 * (1, 0) should be (2, 0)" {
        val v = vectorSpace.get(one, zero)
        val w = vectorSpace.get(two, zero)
        (two * v) shouldBe w
        (2 * v) shouldBe w
    }
    "(1, 0).dim should be 2" {
        val v = vectorSpace.get(one, zero)
        v.dim shouldBe 2
    }
    "vectorSpace.getZero(3) should be (0, 0, 0)" {
        val v = vectorSpace.getZero(3)
        val w = vectorSpace.get(zero, zero, zero)
        v shouldBe w
    }
    "(0, 0, 0) should be different from (0, 0)" {
        val v = vectorSpace.getZero(3)
        val w = vectorSpace.getZero(2)
        v shouldNotBe w
    }
    // "(2-dim vector space).get(0) should throw" {
    //     shouldThrow<IllegalArgumentException> { vectorSpace.get(zero) }
    // }
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
