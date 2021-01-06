package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.NumericalDenseVector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NumericalVectorTest : StringSpec({
    "(0, 1) + (0, 1) should be (0, 2)" {
        val zero = IntRationalField.ZERO
        val one = IntRationalField.ONE
        val two = IntRationalField.fromInt(2)
        val v = NumericalDenseVector(listOf(zero, one))
        val w = NumericalDenseVector(listOf(zero, two))
        (v + v) shouldBe w
    }
})
