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
    val four = field.fromInt(4)
    "((1, 1), (2, 0)) * (2, -1) should be (1, 4)" {
        val matrixSpace = DenseMatrixSpace(field)
        val vectorSpace = DenseNumVectorSpace.from(field)
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
}

class IntRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(IntRationalField))
})
