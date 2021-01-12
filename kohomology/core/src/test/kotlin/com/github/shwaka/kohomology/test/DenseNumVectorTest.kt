package com.github.shwaka.kohomology.test

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F7
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.times
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val denseNumVectorTag = NamedTag("DenseNumVector")

fun <S> denseNumVectorTest(field: Field<S>) = stringSpec {
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val vectorSpace = DenseNumVectorSpace(field, 2)
    "(0, 1) + (0, 1) should be (0, 2)" {
        val v = vectorSpace.get(zero, one)
        val w = vectorSpace.get(zero, two)
        (v + v) shouldBe w
    }
    "(1, 0) * 2 should be (2, 0)" {
        val v = vectorSpace.get(one, zero)
        val w = vectorSpace.get(two, zero)
        (v * two) shouldBe w
    }
    "2 * (1, 0) should be (2, 0)" {
        val v = vectorSpace.get(one, zero)
        val w = vectorSpace.get(two, zero)
        (two * v) shouldBe w
    }
    "(1, 0).dim should be 2" {
        val v = vectorSpace.get(one, zero)
        v.dim shouldBe 2
    }
    "(2-dim vector space).get(0) should throw" {
        shouldThrow<IllegalArgumentException> { vectorSpace.get(zero) }
    }
}

class IntRationalDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag)
    include(denseNumVectorTest(IntRationalField))
})

class BigRationalDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag)
    include(denseNumVectorTest(BigRationalField))
})

class IntModpDenseNumVectorTest : StringSpec({
    tags(denseNumVectorTag)
    include(denseNumVectorTest(F7))
})
