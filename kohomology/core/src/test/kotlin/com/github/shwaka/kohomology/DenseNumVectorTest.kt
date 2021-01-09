package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F7
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.DenseNumVector
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.times
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

fun <S> denseNumVectorTest(field: Field<S>) = stringSpec {
    val zero = field.ZERO
    val one = field.ONE
    val two = field.fromInt(2)
    val vectorSpace = DenseNumVectorSpace(field)
    "(0, 1) + (0, 1) should be (0, 2)" {
        val v = vectorSpace.get(listOf(zero, one))
        val w = vectorSpace.get(listOf(zero, two))
        (v + v) shouldBe w
    }
    "(1, 0) * 2 should be (2, 0)" {
        val v = vectorSpace.get(listOf(one, zero))
        val w = vectorSpace.get(listOf(two, zero))
        (v * two) shouldBe w
    }
    "2 * (1, 0) should be (2, 0)" {
        val v = vectorSpace.get(listOf(one, zero))
        val w = vectorSpace.get(listOf(two, zero))
        (two * v) shouldBe w
    }
}

class IntRationalDenseNumVectorTest : StringSpec({
    include(denseNumVectorTest(IntRationalField))
})

class BigRationalDenseNumVectorTest : StringSpec({
    include(denseNumVectorTest(BigRationalField))
})

class IntModpDenseNumVectorTest : StringSpec({
    include(denseNumVectorTest(F7))
})
