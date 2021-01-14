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
        val expectedMat = listOf(
            listOf(one, one),
            listOf(one, two)
        )
        mat1.exchangeRows(0, 1) shouldBe expectedMat
    }
    "add to another row" {
        val expectedMat = listOf(
            listOf(one, two),
            listOf(zero, -one)
        )
        mat1.addToAnotherRow(0, 1, -one) shouldBe expectedMat
    }
    "eliminateOtherRows" {
        val expectedMat = listOf(
            listOf(-one, zero),
            listOf(one, one)
        )
        mat1.eliminateOtherRows(1, 1) shouldBe expectedMat
    }
    "rowEchelonForm" {
        val expectedMat = listOf(
            listOf(one, zero),
            listOf(zero, -one)
        )
        val (rowEchelonForm, pivots) = mat1.rowEchelonFrom()
        rowEchelonForm shouldBe expectedMat
        pivots shouldBe listOf(0, 1)
    }
}

class IntRationalRowEchelonCalculatorTest : StringSpec({
    tags(rowEchelonCalculatorTag)
    include(rowEchelonCalculatorTest(IntRationalField))
})
