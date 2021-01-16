package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F5
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.LongRationalField
import com.github.shwaka.kohomology.field.Scalar
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints

val denseMatrixTag = NamedTag("DenseMatrix")

fun <S : Scalar<S>> denseMatrixTest(field: Field<S>) = stringSpec {
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
    "toString and toPrettyString should not throw" {
        shouldNotThrowAny {
            m.toString()
            m.toPrettyString()
            val empty = matrixSpace.fromRows()
            empty.toString()
            empty.toPrettyString()
            val rank3Mat = matrixSpace.fromRows(
                listOf(one, zero, zero),
                listOf(zero, one, zero),
                listOf(zero, one, zero),
                listOf(zero, zero, one)
            )
            rank3Mat.toString()
            rank3Mat.toPrettyString()
        }
    }
}

fun <S : Scalar<S>> denseMatrixTestWithArb(field: Field<S>) = stringSpec {
    val min = -100
    val max = 100
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    val testGenerator = IntMatrixTestGenerator(matrixSpace)
    "Property testing for matrix addition" {
        checkAll(Arb.list(Arb.int(min..max), 8..8)) { elmList ->
            val (mat1, mat2, expected) = testGenerator.generate2Arg(elmList) { m, n -> m + n }
            (mat1 + mat2) shouldBe expected
        }
    }
    "Property testing for matrix subtraction" {
        checkAll(Arb.list(Arb.int(min..max), 8..8)) { elmList ->
            val (mat1, mat2, expected) = testGenerator.generate2Arg(elmList) { m, n -> m - n }
            (mat1 - mat2) shouldBe expected
        }
    }
    "Property testing for unaryMinus of matrix" {
        checkAll(Arb.list(Arb.int(min..max), 4..4)) { elmList ->
            val (mat: DenseMatrix<S>, expected: DenseMatrix<S>) = testGenerator.generate1Arg(elmList) { m -> -m }
            (-mat) shouldBe expected
        }
    }
    "Property testing for det" {
        checkAll(Arb.list(Arb.int(min..max), 4..4)) { elmList ->
            val (mat: DenseMatrix<S>, expected: S) = testGenerator.generate1ArgToInt(elmList) { m -> m.det() }
            (mat.det()) shouldBe expected
        }
    }
}

fun <S : Scalar<S>> determinantTest(field: Field<S>, n: Int, max: Int) = stringSpec {
    if (n < 0) throw IllegalArgumentException("Matrix size n should be non-negative")
    if (max < 0) throw IllegalArgumentException("max should be non-negative")
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    "det and detByPermutations should be the same" {
        // val n2 = n * n
        checkAll(Exhaustive.ints(1..n)) { k ->
            val k2 = k * k
            checkAll(Arb.list(Arb.int(-max..max), k2..k2)) { elmList ->
                val mat: DenseMatrix<S> = matrixSpace.fromFlatList(elmList.map { a -> field.fromInt(a) }, k, k)
                mat.det() shouldBe mat.detByPermutations()
            }
        }
    }
}

const val maxValueForDet = 100
const val matrixSizeForDet = 4
// 5 でも一応できるけど、
// - BigRational の test に2秒くらいかかる
// - LongRational の test が(乱数次第で)たまに overflow する

class IntRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(IntRationalField))
    include(denseMatrixTestWithArb(IntRationalField))
    include(determinantTest(IntRationalField, 4, 10)) // これ以上大きくすると det() の計算で overflow する
})

class LongRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(LongRationalField))
    include(denseMatrixTestWithArb(LongRationalField))
    include(determinantTest(LongRationalField, matrixSizeForDet, maxValueForDet))
})

class BigRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(BigRationalField))
    include(denseMatrixTestWithArb(BigRationalField))
    include(determinantTest(BigRationalField, matrixSizeForDet, maxValueForDet))
})

class IntModpDenseMatrixTest : StringSpec({
    tags(denseMatrixTag)
    include(denseMatrixTest(F5))
    include(denseMatrixTestWithArb(F5))
    include(determinantTest(F5, matrixSizeForDet, maxValueForDet))
})
