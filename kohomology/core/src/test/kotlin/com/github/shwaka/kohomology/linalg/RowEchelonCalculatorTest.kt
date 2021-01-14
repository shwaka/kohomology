package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val rowEchelonCalculatorTag = NamedTag("RowEchelonCalculator")

fun <S : Scalar<S>> rowEchelonCalculatorTest(field: Field<S>) = stringSpec {
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val mat1 = listOf(
        listOf(one, two),
        listOf(one, one)
    )
    "exchange rows" {
        val mat2 = listOf(
            listOf(one, one),
            listOf(one, two)
        )
        RowEchelonCalculator.exchange(mat1, 0, 1) shouldBe mat2
    }
    "add to another row" {
        val mat3 = listOf(
            listOf(one, two),
            listOf(zero, -one)
        )
        RowEchelonCalculator.add(mat1, 0, 1, -one) shouldBe mat3
    }
}

class IntRationalRowEchelonCalculatorTest : StringSpec({
    tags(rowEchelonCalculatorTag)
    include(rowEchelonCalculatorTest(IntRationalField))
})
