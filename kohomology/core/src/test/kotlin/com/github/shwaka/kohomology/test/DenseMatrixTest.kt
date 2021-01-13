package com.github.shwaka.kohomology.test

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val denseMatrixTag = NamedTag("DenseMatrix")

fun <S> denseMatrixTest(field: Field<S>) = stringSpec {
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val three = field.fromInt(3)
    val four = field.fromInt(4)
    val five = field.fromInt(5)
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    "((1, 1), (2, 0)) * (2, -1) should be (1, 4)" {
        val m = matrixSpace.get(
            listOf(
                listOf(one, one),
                listOf(two, zero)
            )
        )
        val v = vectorSpace.get(two, -one)
        val expected = vectorSpace.get(one, four)
        (m * v) shouldBe expected
    }
    "((2, 1), (0, -1)) * ((1, 1), (-2, 3)) should be ((0, 5), (2, -3))" {
        val m = matrixSpace.get(
            listOf(
                listOf(two, one),
                listOf(zero, -one)
            )
        )
        val n = matrixSpace.get(
            listOf(
                listOf(one, one),
                listOf(-two, three)
            )
        )
        val mn = matrixSpace.get(
            listOf(
                listOf(zero, five),
                listOf(two, -three)
            )
        )
        (m * n) shouldBe mn
    }
}

class IntRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(IntRationalField))
})
