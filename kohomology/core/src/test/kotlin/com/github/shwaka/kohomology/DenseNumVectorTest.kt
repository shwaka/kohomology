package com.github.shwaka.kohomology

import com.github.shwaka.kohomology.field.*
import com.github.shwaka.kohomology.linalg.DenseNumVector
import com.github.shwaka.kohomology.linalg.times
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.shouldBe

fun <S> denseNumVectorTest(field: Field<S>, fieldString: String? = null) = stringSpec {
    val zero = field.ZERO
    val one = field.ONE
    val two = field.fromInt(2)
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
    "multiplying scalar from left should compile" {
        val fieldName = field::class.java.simpleName
        val fieldStringNonNull = fieldString ?: fieldName
        val code =
            """
                import com.github.shwaka.kohomology.field.$fieldName
                import com.github.shwaka.kohomology.linalg.DenseNumVector
                import com.github.shwaka.kohomology.linalg.times
                val one = $fieldStringNonNull.ONE
                val v = DenseNumVector(listOf(one, one))
                val w = one * v
            """
        code.shouldCompile()
    }
}

class IntRationalDenseNumVectorTest : StringSpec({
    include(denseNumVectorTest(IntRationalField))
    "2 * (1, 0) should be (2, 0)" {
        val field = IntRationalField
        val zero = field.ZERO
        val one = field.ONE
        val two = field.fromInt(2)
        val v = DenseNumVector(listOf(one, zero))
        val w = DenseNumVector(listOf(two, zero))
        (two * v) shouldBe w
    }
})

class BigRationalDenseNumVectorTest : StringSpec({
    include(denseNumVectorTest(BigRationalField))
})

class IntModpDenseNumVectorTest : StringSpec({
    val f7 = Fp.get(7)
    include(denseNumVectorTest(f7, "Fp.get(7)"))
})
