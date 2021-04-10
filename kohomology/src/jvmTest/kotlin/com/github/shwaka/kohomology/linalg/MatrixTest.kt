package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import com.github.shwaka.kohomology.longRationalTag
import com.github.shwaka.kohomology.specific.BigRationalField
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverIntRational
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverLongRational
import com.github.shwaka.kohomology.specific.F2
import com.github.shwaka.kohomology.specific.F5
import com.github.shwaka.kohomology.specific.IntRationalField
import com.github.shwaka.kohomology.specific.LongRationalField
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.arb
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints

val matrixTag = NamedTag("Matrix")
val denseMatrixTag = NamedTag("DenseMatrix")
val sparseMatrixTag = NamedTag("SparseMatrix")

fun <S : Scalar> denseMatrixSpaceTest(field: Field<S>) = stringSpec {
    val numVectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace.from(numVectorSpace)
    "factory should return the cache if exists" {
        DenseMatrixSpace.from(numVectorSpace) shouldBeSameInstanceAs matrixSpace
    }
}

fun <S : Scalar> sparseMatrixSpaceTest(field: Field<S>) = stringSpec {
    val numVectorSpace = SparseNumVectorSpace.from(field)
    val matrixSpace = SparseMatrixSpace.from(numVectorSpace)
    "factory should return the cache if exists" {
        SparseMatrixSpace.from(numVectorSpace) shouldBeSameInstanceAs matrixSpace
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    matrixSpace.context.run {
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
        "((2, 1), (0, -1)) * -2 should be ((-4, -2), (0, 2))" {
            val expected = matrixSpace.fromRows(
                listOf(
                    listOf(-four, -two),
                    listOf(zero, two)
                )
            )
            (m * (-two)) shouldBe expected
        }
        "((2, 1), (0, -1)) * (2, -1) should be (3, 1)" {
            val v = numVectorSpace.fromValues(listOf(two, -one))
            val expected = numVectorSpace.fromValues(listOf(three, one))
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
                    listOf(
                        listOf(one, zero, zero),
                        listOf(zero, one, zero),
                        listOf(zero, one, zero),
                        listOf(zero, zero, one)
                    )
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
            (matrixSpace.fromRows(listOf(row1, row2)) == matrixSpace.fromRows(listOf(row1, row2))).shouldBeTrue()
        }
        "fromVectors should work correctly" {
            val expectedMat = matrixSpace.fromRows(
                listOf(
                    listOf(zero, one),
                    listOf(two, three)
                )
            )
            val v = numVectorSpace.fromValues(listOf(zero, two))
            val w = numVectorSpace.fromValues(listOf(one, three))
            (matrixSpace.fromNumVectors(listOf(v, w))) shouldBe expectedMat
        }
        "reduced row echelon form of an invertible matrix should be the unit matrix" {
            // m = ((2, 1), (0, -1)) is NOT invertible in F2
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(two, one),
                    listOf(one, -one)
                )
            )
            val expectedMat = matrixSpace.getId(2)
            mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
        }
        "reduced row echelon form of non-invertible matrix" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(zero, zero, one),
                    listOf(-two, -one, zero),
                    listOf(two, one, zero)
                )
            )
            val expectedMat = if (field.characteristic == 2) {
                matrixSpace.fromRows(
                    listOf(
                        listOf(zero, one, zero),
                        listOf(zero, zero, one),
                        listOf(zero, zero, zero)
                    )
                )
            } else {
                matrixSpace.fromRows(
                    listOf(
                        listOf(one, one / two, zero),
                        listOf(zero, zero, one),
                        listOf(zero, zero, zero)
                    )
                )
            }
            mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
        }
        "transpose of ((1, 2), (3, 4)) should be ((1, 3), (2, 4))" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(one, two),
                    listOf(three, four)
                )
            )
            val expectedMat = matrixSpace.fromRows(
                listOf(
                    listOf(one, three),
                    listOf(two, four)
                )
            )
            mat.transpose() shouldBe expectedMat
        }
        "inner product of (1, 2) and (3, 4) w.r.t ((-1, 0), (-2, 2)) should be 1" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(-one, zero),
                    listOf(-two, two)
                )
            )
            val v = numVectorSpace.fromValues(listOf(one, two))
            val w = numVectorSpace.fromValues(listOf(three, four))
            mat.innerProduct(v, w) shouldBe one
        }
        "kernel of zero matrix should have the standard basis" {
            val dim = 5
            val mat = matrixSpace.getZero(dim)
            val expected = (0 until 5).map { numVectorSpace.getOneAtIndex(it, 5) }
            mat.computeKernelBasis() shouldBe expected
        }
        "compute kernel of ((1, 1), (2, 2))" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(one, one),
                    listOf(two, two)
                )
            )
            val kernelBasis = mat.computeKernelBasis()
            kernelBasis.size shouldBe 1
            (mat * kernelBasis[0]).isZero().shouldBeTrue()
        }
        "compute kernel of ((1, 2, 3), (0, 0, 0), (0, 0, 0))" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(one, two, three),
                    listOf(zero, zero, zero),
                    listOf(zero, zero, zero),
                )
            )
            val kernelBasis = mat.computeKernelBasis()
            kernelBasis.size shouldBe 2
            for (v in kernelBasis) {
                (mat * v).isZero().shouldBeTrue()
            }
        }
        "compute kernel of ((1, 1), (1, 3))" {
            val mat = matrixSpace.fromRows(
                listOf(
                    listOf(one, one),
                    listOf(one, three),
                )
            )
            val kernelBasis = mat.computeKernelBasis()
            if (matrixSpace.field.characteristic == 2) {
                kernelBasis.size shouldBe 1
                (mat * kernelBasis[0]).isZero().shouldBeTrue()
            } else {
                kernelBasis.size shouldBe 0
            }
        }
        "join of ((1, 2), (3, 4)) and ((-1, -2), (-3, -4)) should be ((1, 2, -1, -2), (3, 4, -3, -4))" {
            val mat1 = matrixSpace.fromRows(
                listOf(
                    listOf(one, two),
                    listOf(three, four),
                )
            )
            val mat2 = -mat1
            val expected = matrixSpace.fromRows(
                listOf(
                    listOf(one, two, -one, -two),
                    listOf(three, four, -three, -four),
                )
            )
            listOf(mat1, mat2).join() shouldBe expected
        }
        "test rowSlice and colSlice" {
            val intRange = 1..2
            val matrix = matrixSpace.fromRows(
                (0 until 4).map { row ->
                    (0 until 4).map { col -> (row * 4 + col).toScalar() }
                }
            )
            val expectedRowSlice = matrixSpace.fromRows(
                listOf(
                    listOf(4, 5, 6, 7).map { it.toScalar() },
                    listOf(8, 9, 10, 11).map { it.toScalar() },
                )
            )
            val expectedColSlice = matrixSpace.fromRows(
                listOf(
                    listOf(1, 2).map { it.toScalar() },
                    listOf(5, 6).map { it.toScalar() },
                    listOf(9, 10).map { it.toScalar() },
                    listOf(13, 14).map { it.toScalar() },
                )
            )
            matrix.rowSlice(intRange) shouldBe expectedRowSlice
            matrix.colSlice(intRange) shouldBe expectedColSlice
        }
    }
}

inline fun <S : Scalar, reified V : NumVector<S>, M : Matrix<S, V>> matrixFromVectorTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val field = matrixSpace.context.run { field }
    val vectorSpace = matrixSpace.numVectorSpace
    val zero = field.zero
    "fromVectors(vararg) should work with reified type variables" {
        val v = vectorSpace.fromValues(listOf(zero, zero, zero))
        shouldNotThrowAny {
            matrixSpace.fromNumVectors(listOf(v, v))
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixOfRank2Test(
    matrixSpace: MatrixSpace<S, V, M>,
    max: Int = 100
) = stringSpec {
    // val vectorSpace = DenseNumVectorSpace.from(field)
    val field = matrixSpace.context.run { field }
    val scalarArb = field.arb(Arb.int(-max..max))
    val matrixArb = matrixSpace.arb(scalarArb, 2, 2)
    "Property testing for matrix addition" {
        checkAll(matrixArb, matrixArb) { mat1, mat2 ->
            matrixSpace.context.run {
                MatrixOfRank2(matrixSpace, mat1 + mat2) shouldBe (MatrixOfRank2(matrixSpace, mat1) + MatrixOfRank2(matrixSpace, mat2))
            }
        }
    }
    "Property testing for matrix subtraction" {
        checkAll(matrixArb, matrixArb) { mat1, mat2 ->
            matrixSpace.context.run {
                MatrixOfRank2(matrixSpace, mat1 - mat2) shouldBe (MatrixOfRank2(matrixSpace, mat1) - MatrixOfRank2(matrixSpace, mat2))
            }
        }
    }
    "Property testing for unaryMinus of matrix" {
        checkAll(matrixArb) { mat ->
            matrixSpace.context.run {
                MatrixOfRank2(matrixSpace, -mat) shouldBe (-MatrixOfRank2(matrixSpace, mat))
            }
        }
    }
    "Property testing for det" {
        checkAll(matrixArb) { mat ->
            matrixSpace.context.run {
                mat.det() shouldBe MatrixOfRank2(matrixSpace, mat).det()
            }
        }
    }
}

fun <S : Scalar> determinantTest(field: Field<S>, n: Int, max: Int) = stringSpec {
    if (n < 0) throw IllegalArgumentException("Matrix size n should be non-negative")
    if (max < 0) throw IllegalArgumentException("max should be non-negative")
    val numVectorSpace = DenseNumVectorSpace.from(field)
    val matrixSpace = DenseMatrixSpace(numVectorSpace)
    val scalarArb = field.arb(Arb.int(-max..max))
    "det and detByPermutations should be the same" {
        checkAll(Exhaustive.ints(1..n)) { k ->
            val matrixArb = matrixSpace.arb(scalarArb, k, k)
            checkAll(matrixArb) { mat ->
                matrixSpace.context.run {
                    mat.det() shouldBe mat.detByPermutations()
                }
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
    tags(matrixTag, denseMatrixTag, intRationalTag)

    val matrixSpace = DenseMatrixSpaceOverIntRational
    include(denseMatrixSpaceTest(IntRationalField))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace, 10))
    // include(determinantTest(IntRationalField, 3, 5)) // overflow しがちなので除外
})

class LongRationalDenseMatrixTest : StringSpec({
    tags(matrixTag, denseMatrixTag, longRationalTag)

    val matrixSpace = DenseMatrixSpaceOverLongRational
    include(denseMatrixSpaceTest(LongRationalField))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    // include(determinantTest(LongRationalField, matrixSizeForDet, 5)) // overflow しがちなので除外
})

class BigRationalDenseMatrixTest : StringSpec({
    tags(matrixTag, denseMatrixTag, bigRationalTag)

    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(denseMatrixSpaceTest(BigRationalField))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(BigRationalField, matrixSizeForDet, maxValueForDet))
})

class IntMod2DenseMatrixTest : StringSpec({
    tags(matrixTag, denseMatrixTag, intModpTag)

    val matrixSpace = DenseMatrixSpaceOverF2
    include(denseMatrixSpaceTest(F2))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(F2, matrixSizeForDet, maxValueForDet))
})

class IntMod5DenseMatrixTest : StringSpec({
    tags(matrixTag, denseMatrixTag, intModpTag)

    val matrixSpace = DenseMatrixSpaceOverF5
    include(denseMatrixSpaceTest(F5))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(F5, matrixSizeForDet, maxValueForDet))
})

class BigRationalSparseMatrixTest : StringSpec({
    tags(matrixTag, sparseMatrixTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(sparseMatrixSpaceTest(BigRationalField))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(BigRationalField, matrixSizeForDet, maxValueForDet))
})

class IntMod2SparseMatrixTest : StringSpec({
    tags(matrixTag, sparseMatrixTag, intModpTag)

    val matrixSpace = SparseMatrixSpaceOverF2
    include(sparseMatrixSpaceTest(F2))
    include(matrixTest(matrixSpace))
    include(matrixFromVectorTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(F2, matrixSizeForDet, maxValueForDet))
})
