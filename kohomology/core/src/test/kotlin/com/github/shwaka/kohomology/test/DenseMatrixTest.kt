package com.github.shwaka.kohomology.test

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F5
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.linalg.DenseMatrix
import com.github.shwaka.kohomology.linalg.DenseMatrixSpace
import com.github.shwaka.kohomology.linalg.DenseNumVectorSpace
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

val denseMatrixTag = NamedTag("DenseMatrix")

fun <S> denseMatrixTest(field: Field<S>) = stringSpec {
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val three = field.fromInt(3)
    // val four = field.fromInt(4)
    val five = field.fromInt(5)
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
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
    "((2, 1), (0, -1)) + ((1, 1), (-2, 3)) should be ((3, 2), (-2, 2))" {
        val expected = matrixSpace.get(
            listOf(
                listOf(three, two),
                listOf(-two, two)
            )
        )
        (m + n) shouldBe expected
    }
    "((2, 1), (0, -1)) * (2, -1) should be (3, 1)" {
        val v = vectorSpace.get(two, -one)
        val expected = vectorSpace.get(three, one)
        (m * v) shouldBe expected
    }
    "((2, 1), (0, -1)) * ((1, 1), (-2, 3)) should be ((0, 5), (2, -3))" {
        val mn = matrixSpace.get(
            listOf(
                listOf(zero, five),
                listOf(two, -three)
            )
        )
        (m * n) shouldBe mn
    }
}

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]

data class IntMatrix(val a: Int, val b: Int, val c: Int, val d: Int) {
    fun <S> toDenseMatrix(matrixSpace: DenseMatrixSpace<S>): DenseMatrix<S> {
        val field = matrixSpace.field
        val (a, b, c, d) = listOf(this.a, this.b, this.c, this.d).map(field::fromInt)
        return matrixSpace.fromRows(
            listOf(a, b),
            listOf(c, d)
        )
    }
}

fun <S> generateMatricesOfRank2(
    field: Field<S>,
    elmList: List<Int>,
    expect: (intMat1: IntMatrix, intMat2: IntMatrix) -> IntMatrix
): Triple<DenseMatrix<S>, DenseMatrix<S>, DenseMatrix<S>> {
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    val (a, b, c, d, e, f, g, h) = elmList // .map(field::fromInt)
    val intMat1 = IntMatrix(a, b, c, d)
    val intMat2 = IntMatrix(e, f, g, h)
    val mat1 = intMat1.toDenseMatrix(matrixSpace)
    val mat2 = intMat2.toDenseMatrix(matrixSpace)
    val expected = expect(intMat1, intMat2).toDenseMatrix(matrixSpace)
    return Triple(mat1, mat2, expected)
}

fun <S> denseMatrixTestWithGenerators(field: Field<S>) = stringSpec {
    val min = -100
    val max = 100
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    "Property testing for matrix addition" {
        checkAll(Arb.list(Arb.int(min..max), 8..8)) { elmList ->
            val (mat1, mat2, expected) = generateMatricesOfRank2(field, elmList) { m, n ->
                IntMatrix(m.a + n.a, m.b + n.b, m.c + n.c, m.d + n.d)
            }
            (mat1 + mat2) shouldBe expected
        }
    }
}

class IntRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(IntRationalField))
    include(denseMatrixTestWithGenerators(IntRationalField))
})

class BigRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(BigRationalField))
    include(denseMatrixTestWithGenerators(BigRationalField))
})

class IntModpDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(F5))
    include(denseMatrixTestWithGenerators(F5))
})
