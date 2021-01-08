package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.DenseNumVector
import com.github.shwaka.kohomology.linalg.times
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.shouldBe

class IntRationalNumVectorTest : StringSpec({
    val zero = IntRationalField.ZERO
    val one = IntRationalField.ONE
    val two = IntRationalField.fromInt(2)
    "(0, 1) + (0, 1) should be (0, 2)" {
        val v = DenseNumVector(listOf(zero, one))
        val w = DenseNumVector(listOf(zero, two))
        (v + v) shouldBe w
    }
    "(1, 0) * 2 should be (2, 0)" {
        val v = DenseNumVector(listOf(one, zero))
        val w = DenseNumVector(listOf(two, zero))
        (v * two) shouldBe w
    }
    "2 * (1, 0) should be (2, 0)" {
        // This works only for IntRational
        val v = DenseNumVector(listOf(one, zero))
        val w = DenseNumVector(listOf(two, zero))
        (two * v) shouldBe w
    }
    "multiplying scalar from left should compile" {
        val fieldName = IntRationalField::class.java.simpleName
        val code =
            """
                import com.github.shwaka.kohomology.field.$fieldName
                import com.github.shwaka.kohomology.linalg.NumericalDenseVector
                import com.github.shwaka.kohomology.linalg.times
                val one = $fieldName.ONE
                val v = NumericalDenseVector(listOf(one, one))
                val w = one * v
            """
        code.shouldCompile()
    }
})
