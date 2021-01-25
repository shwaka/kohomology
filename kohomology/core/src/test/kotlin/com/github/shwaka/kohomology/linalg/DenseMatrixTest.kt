package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.F5
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.LongRationalField
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.field.arb
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import com.github.shwaka.kohomology.longRationalTag
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints

val denseMatrixTag = NamedTag("DenseMatrix")

fun <S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> matrixTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val field = matrixSpace.field
    val vectorSpace = matrixSpace.vectorSpace
    val zero = field.zero
    val one = field.one
    val two = field.fromInt(2)
    val three = field.fromInt(3)
    // val four = field.fromInt(4)
    val five = field.fromInt(5)
    val m = matrixSpace.fromRows(
        listOf(
            listOf(two, one),
            listOf(zero, -one)
        )
    )
    val n = matrixSpace.fromRows(
        listOf(
            listOf(one, one),
            listOf(-two, three)
        )
    )
    "((2, 1), (0, -1)) + ((1, 1), (-2, 3)) should be ((3, 2), (-2, 2))" {
        val expected = matrixSpace.fromRows(
            listOf(
                listOf(three, two),
                listOf(-two, two)
            )
        )
        (m + n) shouldBe expected
    }
    "((2, 1), (0, -1)) * (2, -1) should be (3, 1)" {
        val v = vectorSpace.fromValues(two, -one)
        val expected = vectorSpace.fromValues(three, one)
        (m * v) shouldBe expected
    }
    "((2, 1), (0, -1)) * ((1, 1), (-2, 3)) should be ((0, 5), (2, -3))" {
        val mn = matrixSpace.fromRows(
            listOf(
                listOf(zero, five),
                listOf(two, -three)
            )
        )
        (m * n) shouldBe mn
    }
    "toString and toPrettyString should not throw for square matrix of rank 2" {
        shouldNotThrowAny {
            m.toString()
            m.toPrettyString()
        }
    }
    "toString and toPrettyString should not throw for square matrix of shape 4x3" {
        shouldNotThrowAny {
            val mat = matrixSpace.fromRows(
                listOf(one, zero, zero),
                listOf(zero, one, zero),
                listOf(zero, one, zero),
                listOf(zero, zero, one)
            )
            mat.toString()
            mat.toPrettyString()
        }
    }
    "toString and toPrettyString should not throw for empty matrix" {
        shouldNotThrowAny {
            val empty = matrixSpace.fromFlatList(emptyList(), 0, 0)
            empty.toString()
            empty.toPrettyString()
        }
    }
    "fromRows and fromCols should give same matrices" {
        val rows = listOf(
            listOf(zero, one),
            listOf(two, three)
        )
        val cols = listOf(
            listOf(zero, two),
            listOf(one, three)
        )
        (matrixSpace.fromRows(rows) == matrixSpace.fromCols(cols)).shouldBeTrue()
    }
    "two variants of fromRows should give same matrices" {
        val row1 = listOf(zero, one)
        val row2 = listOf(two, three)
        (matrixSpace.fromRows(row1, row2) == matrixSpace.fromRows(listOf(row1, row2))).shouldBeTrue()
    }
    "fromVectors should work correctly" {
        val expectedMat = matrixSpace.fromRows(
            listOf(zero, one),
            listOf(two, three)
        )
        val v = vectorSpace.fromValues(zero, two)
        val w = vectorSpace.fromValues(one, three)
        (matrixSpace.fromVectors(listOf(v, w))) shouldBe expectedMat
    }
}

inline fun <S : Scalar<S>, reified V : NumVector<S, V>, M : Matrix<S, V, M>> matrixFromVectorTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val field = matrixSpace.field
    val vectorSpace = matrixSpace.vectorSpace
    val zero = field.zero
    "fromVectors(vararg) should work with reified type variables" {
        val v = vectorSpace.fromValues(zero, zero, zero)
        shouldNotThrowAny {
            matrixSpace.fromVectors(v, v)
        }
    }
}

fun <S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> denseMatrixOfRank2Test(
    matrixSpace: MatrixSpace<S, V, M>,
    max: Int = 100
) = stringSpec {
    // val vectorSpace = DenseNumVectorSpace.from(field)
    val field = matrixSpace.field
    val scalarArb = field.arb(Arb.int(-max..max))
    val matrixArb = matrixSpace.arb(scalarArb, 2, 2)
    "Property testing for matrix addition" {
        checkAll(matrixArb, matrixArb) { mat1, mat2 ->
            MatrixOfRank2(mat1 + mat2) shouldBe (MatrixOfRank2(mat1) + MatrixOfRank2(mat2))
        }
    }
    "Property testing for matrix subtraction" {
        checkAll(matrixArb, matrixArb) { mat1, mat2 ->
            MatrixOfRank2(mat1 - mat2) shouldBe (MatrixOfRank2(mat1) - MatrixOfRank2(mat2))
        }
    }
    "Property testing for unaryMinus of matrix" {
        checkAll(matrixArb) { mat ->
            MatrixOfRank2(-mat) shouldBe (-MatrixOfRank2(mat))
        }
    }
    "Property testing for det" {
        checkAll(matrixArb) { mat ->
            mat.det() shouldBe MatrixOfRank2(mat).det()
        }
    }
}

fun <S : Scalar<S>> determinantTest(field: Field<S>, n: Int, max: Int) = stringSpec {
    if (n < 0) throw IllegalArgumentException("Matrix size n should be non-negative")
    if (max < 0) throw IllegalArgumentException("max should be non-negative")
    val vectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    val scalarArb = field.arb(Arb.int(-max..max))
    "det and detByPermutations should be the same" {
        checkAll(Exhaustive.ints(1..n)) { k ->
            val matrixArb = matrixSpace.arb(scalarArb, k, k)
            checkAll(matrixArb) { mat ->
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
    tags(denseMatrixTag, intRationalTag)

    val vectorSpace = DenseNumVectorSpace.from(IntRationalField)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(denseMatrixOfRank2Test(matrixSpace, 10))
    // include(determinantTest(IntRationalField, 3, 5)) // overflow しがちなので除外
})

class LongRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag, longRationalTag)

    val vectorSpace = DenseNumVectorSpace.from(LongRationalField)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(denseMatrixOfRank2Test(matrixSpace))
    // include(determinantTest(LongRationalField, matrixSizeForDet, 5)) // overflow しがちなので除外
})

class BigRationalDenseMatrixTest : StringSpec({
    tags(denseMatrixTag, bigRationalTag)

    val vectorSpace = DenseNumVectorSpace.from(BigRationalField)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(denseMatrixOfRank2Test(matrixSpace))
    include(determinantTest(BigRationalField, matrixSizeForDet, maxValueForDet))

    "fromVectors should work correctly (use statically selected field)" {
        val zero = BigRationalField.zero
        val one = BigRationalField.one
        val two = BigRationalField.fromInt(2)
        val three = BigRationalField.fromInt(3)
        val expectedMat = matrixSpace.fromRows(
            listOf(zero, one),
            listOf(two, three)
        )
        val v = vectorSpace.fromValues(zero, two)
        val w = vectorSpace.fromValues(one, three)
        (matrixSpace.fromVectors(v, w)) shouldBe expectedMat
    }
})

class IntModpDenseMatrixTest : StringSpec({
    tags(denseMatrixTag, intModpTag)

    val vectorSpace = DenseNumVectorSpace.from(F5)
    val matrixSpace = DenseMatrixSpace(vectorSpace)
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(denseMatrixOfRank2Test(matrixSpace))
    include(determinantTest(F5, matrixSizeForDet, maxValueForDet))
})
