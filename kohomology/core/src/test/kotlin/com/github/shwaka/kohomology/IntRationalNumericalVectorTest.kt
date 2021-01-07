package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.NumericalDenseVector
import com.github.shwaka.kohomology.linalg.times
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IntRationalNumericalVectorTest : StringSpec({
    val zero = IntRationalField.ZERO
    val one = IntRationalField.ONE
    val two = IntRationalField.fromInt(2)
    "(0, 1) + (0, 1) should be (0, 2)" {
        val v = NumericalDenseVector(listOf(zero, one))
        val w = NumericalDenseVector(listOf(zero, two))
        (v + v) shouldBe w
    }
    "(1, 0) * 2 should be (2, 0)" {
        val v = NumericalDenseVector(listOf(one, zero))
        val w = NumericalDenseVector(listOf(two, zero))
        (v * two) shouldBe w
    }
    "2 * (1, 0) should be (2, 0)" {
        // This works only for IntRational
        val v = NumericalDenseVector(listOf(one, zero))
        val w = NumericalDenseVector(listOf(two, zero))
        (two * v) shouldBe w
    }
})
