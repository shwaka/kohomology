package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.intRationalTag
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val rowEchelonCalculatorTag = NamedTag("RowEchelonCalculator")
//
// fun <S : Scalar<S>> rowEchelonCalculatorTest(field: Field<S>) = stringSpec {
//     val zero = field.zero
//     val one = field.one
//     val two = field.fromInt(2)
//     // val four = field.fromInt(4)
//     val mat1 = listOf(
//         listOf(one, two),
//         listOf(one, one)
//     )
//     "exchange rows" {
//         val expectedMat = listOf(
//             listOf(one, one),
//             listOf(one, two)
//         )
//         mat1.exchangeRows(0, 1) shouldBe expectedMat
//     }
//     "add to another row" {
//         val expectedMat = listOf(
//             listOf(one, two),
//             listOf(zero, -one)
//         )
//         mat1.addToAnotherRow(0, 1, -one) shouldBe expectedMat
//     }
//     "eliminateOtherRows" {
//         val expectedMat = listOf(
//             listOf(-one, zero),
//             listOf(one, one)
//         )
//         mat1.eliminateOtherRows(1, 1) shouldBe expectedMat
//     }
//     "rowEchelonForm 1" {
//         val expectedMat = listOf(
//             listOf(one, zero),
//             listOf(zero, -one)
//         )
//         val (rowEchelonForm, pivots, _) = mat1.rowEchelonForm()
//         rowEchelonForm shouldBe expectedMat
//         pivots shouldBe listOf(0, 1)
//     }
//     "rowEchelonForm 2" {
//         val mat = listOf(
//             listOf(one, one, zero),
//             listOf(two, two, zero),
//             listOf(zero, one, two)
//         )
//         val expectedMat = listOf(
//             listOf(one, zero, -two),
//             listOf(zero, one, two),
//             listOf(zero, zero, zero)
//         )
//         val (rowEchelonForm, pivots, _) = mat.rowEchelonForm()
//         rowEchelonForm shouldBe expectedMat
//         pivots shouldBe listOf(0, 1)
//     }
// }
//
// class IntRationalRowEchelonCalculatorTest : StringSpec({
//     tags(rowEchelonCalculatorTag, intRationalTag)
//     include(rowEchelonCalculatorTest(IntRationalField))
// })
