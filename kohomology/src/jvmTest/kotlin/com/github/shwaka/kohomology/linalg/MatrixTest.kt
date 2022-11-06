package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.intModpTag
import com.github.shwaka.kohomology.intRationalTag
import com.github.shwaka.kohomology.longRationalTag
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverIntRational
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverLongRational
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.specific.arb
import com.github.shwaka.kohomology.util.Sign
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints

val matrixTag = NamedTag("Matrix")
val denseMatrixTag = NamedTag("DenseMatrix")
val sparseMatrixTag = NamedTag("SparseMatrix")

fun <S : Scalar> denseMatrixSpaceTest(matrixSpace: DenseMatrixSpace<S>) = freeSpec {
    "dense matrix space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        "factory should return the cache if exists" {
            DenseMatrixSpace.from(numVectorSpace) shouldBeSameInstanceAs matrixSpace
        }
    }
}

fun <S : Scalar> sparseMatrixSpaceTest(matrixSpace: SparseMatrixSpace<S>) = freeSpec {
    "sparse matrix space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        "factory should return the cache if exists" {
            SparseMatrixSpace.from(numVectorSpace) shouldBeSameInstanceAs matrixSpace
        }
    }

    matrixSpace.context.run {
        "rowMap for ((0, 0, 0), (0, 0, 0)) should be empty" {
            val m = listOf(
                listOf(zero, zero, zero),
                listOf(zero, zero, zero),
            ).toMatrix()
            m.rowMap.shouldBeEmpty()
            m.rowCount shouldBe 2
            m.colCount shouldBe 3
        }

        "rowMap for ((1, 0), (0, 0)) should have size 1" {
            val m = listOf(
                listOf(one, zero),
                listOf(zero, zero)
            ).toMatrix()
            m.rowMap.size shouldBe 1
            m.rowCount shouldBe 2
            m.colCount shouldBe 2
        }

        "rowMap for ((1, 0), (0, 2)) + ((-1, 0), (0, -2)) should be empty" {
            val m = listOf(
                listOf(one, zero),
                listOf(zero, two)
            ).toMatrix()
            val n = listOf(
                listOf(-one, zero),
                listOf(zero, -two)
            ).toMatrix()
            (m + n).rowMap.shouldBeEmpty()
        }

        "rowMap for ((1, 2), (3, 4)) * 0 should be empty" {
            val m = listOf(
                listOf(one, two),
                listOf(three, four)
            ).toMatrix()
            (m * zero).rowMap.shouldBeEmpty()
        }

        "computation of row echelon form for large identity matrix" {
            val dim = 5000
            val m = matrixSpace.getIdentity(dim)
            m.rowEchelonForm.matrix shouldBe m
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "matrix test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        matrixSpace.context.run {
            val m = listOf(
                listOf(two, one),
                listOf(zero, -one)
            ).toMatrix()
            val n = listOf(
                listOf(one, one),
                listOf(-two, three)
            ).toMatrix()

            "Matrices with same elements should return the same hashCode" {
                val m2 = listOf(
                    listOf(two, one),
                    listOf(zero, -one)
                ).toMatrix()
                m shouldNotBeSameInstanceAs m2
                m.hashCode() shouldBe m2.hashCode()
            }
            "index access should throw the matrix elements" {
                m[0, 0] shouldBe two
                m[1, 1] shouldBe -one
            }
            "invalid index access should throw an IndexOutOfBoundsException" {
                shouldThrow<IndexOutOfBoundsException> { m[2, 0] }
                shouldThrow<IndexOutOfBoundsException> { m[0, 2] }
                shouldThrow<IndexOutOfBoundsException> { m[2, 2] }
                shouldThrow<IndexOutOfBoundsException> { m[-1, 0] }
                shouldThrow<IndexOutOfBoundsException> { m[0, -1] }
                shouldThrow<IndexOutOfBoundsException> { m[-1, -1] }
            }
            "((2, 1), (0, -1)) + ((1, 1), (-2, 3)) should be ((3, 2), (-2, 2))" {
                val expected = listOf(
                    listOf(three, two),
                    listOf(-two, two)
                ).toMatrix()
                (m + n) shouldBe expected
            }
            "((2, 1), (0, -1)) * -2 should be ((-4, -2), (0, 2))" {
                val expected = listOf(
                    listOf(-four, -two),
                    listOf(zero, two)
                ).toMatrix()
                (m * (-two)) shouldBe expected
            }
            "((2, 1), (0, -1)) * (2, -1) should be (3, 1)" {
                val v = listOf(two, -one).toNumVector()
                val expected = listOf(three, one).toNumVector()
                (m * v) shouldBe expected
            }
            "((2, 1), (0, -1), (-2, 1)) * (2, -1) should be (3, 1, -5)" {
                val mat = listOf(
                    listOf(two, one),
                    listOf(zero, -one),
                    listOf(-two, one),
                ).toMatrix()
                val v = listOf(two, -one).toNumVector()
                val expected = listOf(three, one, -five).toNumVector()
                (mat * v) shouldBe expected
            }
            "((2, 1), (0, -1)) * ((1, 1), (-2, 3)) should be ((0, 5), (2, -3))" {
                val mn = listOf(
                    listOf(zero, five),
                    listOf(two, -three)
                ).toMatrix()
                (m * n) shouldBe mn
            }
            "((2, 1)) * ((1, 1), (-2, 3)) should be ((0, 5))" {
                val mat = listOf(
                    listOf(two, one),
                ).toMatrix()
                val expected = listOf(
                    listOf(zero, five),
                ).toMatrix()
                (mat * n) shouldBe expected
            }
            "(r×0-matrix) * (0×c-matrix) should be zero matrix of size r×c" {
                val r = 3
                val c = 2
                val zeroCols = matrixSpace.fromRowMap(emptyMap(), r, 0)
                val zeroRows = matrixSpace.fromRowMap(emptyMap(), 0, c)
                val multiplied = shouldNotThrowAny {
                    zeroCols * zeroRows
                }
                multiplied.rowCount shouldBe r
                multiplied.colCount shouldBe c
                multiplied.isZero().shouldBeTrue()
            }
            "(0×k-matrix) * (k×0-matrix) should be a matrix of size 0×0" {
                val k = 3
                val zeroRows = matrixSpace.fromRowMap(emptyMap(), 0, k)
                val zeroCols = matrixSpace.fromRowMap(emptyMap(), k, 0)
                val multiplied = shouldNotThrowAny {
                    zeroRows * zeroCols
                }
                multiplied.rowCount shouldBe 0
                multiplied.colCount shouldBe 0
                multiplied.isZero().shouldBeTrue()
            }
            "(0×k-matrix) * (k×l-matrix) should be a zero matrix of size 0×l" {
                val k = 3
                val l = 4
                val zeroRows = matrixSpace.fromRowMap(emptyMap(), 0, k)
                val mat = matrixSpace.fromRowList(
                    rowList = List(k) { List(l) { one } },
                    colCount = l
                )
                val multiplied = shouldNotThrowAny {
                    zeroRows * mat
                }
                multiplied.rowCount shouldBe 0
                multiplied.colCount shouldBe l
                multiplied.isZero().shouldBeTrue()
            }
            "(l×k-matrix) * (k×0-matrix) should be a zero matrix of size l×0" {
                val k = 3
                val l = 4
                val mat = matrixSpace.fromRowList(
                    rowList = List(l) { List(k) { one } },
                    colCount = k
                )
                val zeroCols = matrixSpace.fromRowMap(emptyMap(), k, 0)
                val multiplied = shouldNotThrowAny {
                    mat * zeroCols
                }
                multiplied.rowCount shouldBe l
                multiplied.colCount shouldBe 0
                multiplied.isZero().shouldBeTrue()
            }
            "(k×0-matrix) * (0-dim vector) should be a zero numVector of dim k" {
                val k = 3
                val mat = matrixSpace.fromRowMap(emptyMap(), k, 0)
                val numVector = numVectorSpace.fromValueMap(emptyMap(), 0)
                val multiplied = shouldNotThrowAny {
                    mat * numVector
                }
                multiplied.dim shouldBe k
                multiplied.isZero().shouldBeTrue()
            }
            "(0×k-matrix) * (k-dim vector) should be a numVector of dim 0" {
                val k = 3
                val mat = matrixSpace.fromRowMap(emptyMap(), 0, k)
                val numVector = numVectorSpace.fromValueMap(emptyMap(), k)
                val multiplied = shouldNotThrowAny {
                    mat * numVector
                }
                multiplied.dim shouldBe 0
                multiplied.isZero().shouldBeTrue()
            }
            "multiplication with Sign" {
                (m * Sign.PLUS) shouldBe m
                (Sign.PLUS * m) shouldBe m
                (m * Sign.MINUS) shouldBe -m
                (Sign.MINUS * m) shouldBe -m
            }
            "((2, 1), (0, -1)).isZero() should be false" {
                m.isZero().shouldBeFalse()
                m.isNotZero().shouldBeTrue()
            }
            "((0, 0), (0, 0)).izZero() should be true" {
                val mat = listOf(
                    listOf(zero, zero),
                    listOf(zero, zero),
                ).toMatrix()
                mat.isZero().shouldBeTrue()
                mat.isNotZero().shouldBeFalse()
            }
            "((1, 0), (0, 2)).isIdentity() should be false" {
                val mat = listOf(
                    listOf(one, zero),
                    listOf(zero, two),
                ).toMatrix()
                mat.isIdentity().shouldBeFalse()
                mat.isNotIdentity().shouldBeTrue()
            }
            "((1, 1), (0, 1)).isIdentity() should be false" {
                val mat = listOf(
                    listOf(one, one),
                    listOf(zero, one),
                ).toMatrix()
                mat.isIdentity().shouldBeFalse()
                mat.isNotIdentity().shouldBeTrue()
            }
            "((1, 0), (0, 0)).isIdentity() should be false" {
                val mat = listOf(
                    listOf(one, zero),
                    listOf(zero, zero),
                ).toMatrix()
                mat.isIdentity().shouldBeFalse()
                mat.isNotIdentity().shouldBeTrue()
            }
            "((1, 0, 0), (0, 1, 0)).isIdentity() should be false" {
                val mat = listOf(
                    listOf(one, zero, zero),
                    listOf(zero, one, zero),
                ).toMatrix()
                mat.isIdentity().shouldBeFalse()
                mat.isNotIdentity().shouldBeTrue()
            }
            "((1, 0), (0, 1)).isIdentity() should be true" {
                val mat = listOf(
                    listOf(one, zero),
                    listOf(zero, one),
                ).toMatrix()
                mat.isIdentity().shouldBeTrue()
                mat.isNotIdentity().shouldBeFalse()
            }
            "toString and toPrettyString should not throw for square matrix of rank 2" {
                shouldNotThrowAny {
                    m.toString()
                    m.toPrettyString()
                }
            }
            "toString and toPrettyString should not throw for square matrix of shape 4x3" {
                shouldNotThrowAny {
                    val mat = listOf(
                        listOf(one, zero, zero),
                        listOf(zero, one, zero),
                        listOf(zero, one, zero),
                        listOf(zero, zero, one)
                    ).toMatrix()
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
            "fromRowList and fromColList should give same matrices" {
                val rowList = listOf(
                    listOf(zero, one),
                    listOf(two, three)
                )
                val colList = listOf(
                    listOf(zero, two),
                    listOf(one, three)
                )
                (matrixSpace.fromRowList(rowList) == matrixSpace.fromColList(colList)).shouldBeTrue()
            }
            "fromRowMap should return the same matrix as fromRowList" {
                val rowList = listOf(
                    listOf(zero, one),
                    listOf(zero, zero),
                )
                val rowMap = mapOf(
                    0 to mapOf(1 to one)
                )
                matrixSpace.fromRowMap(rowMap, 2, 2) shouldBe matrixSpace.fromRowList(rowList)
                rowMap.toMatrix(2, 2) shouldBe matrixSpace.fromRowList(rowList)
            }
            "fromColMap should return the same matrix as fromColList" {
                val colList = listOf(
                    listOf(zero, one),
                    listOf(zero, zero),
                )
                val colMap = mapOf(
                    0 to mapOf(1 to one)
                )
                matrixSpace.fromColMap(colMap, 2, 2) shouldBe matrixSpace.fromColList(colList)
            }
            "fromFlatList should return the same matrix as fromRowList" {
                val rowList = listOf(
                    listOf(one, two),
                    listOf(three, four),
                )
                val flatList = listOf(one, two, three, four)
                matrixSpace.fromFlatList(flatList, 2, 2) shouldBe matrixSpace.fromRowList(rowList)
                flatList.toMatrix(2, 2) shouldBe matrixSpace.fromRowList(rowList)
            }
            "fromVectors should work correctly" {
                val expectedMat = listOf(
                    listOf(zero, one),
                    listOf(two, three)
                ).toMatrix()
                val v = listOf(zero, two).toNumVector()
                val w = listOf(one, three).toNumVector()
                (matrixSpace.fromNumVectorList(listOf(v, w))) shouldBe expectedMat
            }
            "reduced row echelon form of an invertible matrix should be the unit matrix" {
                val mat = if (matrixSpace.field.characteristic != 3) {
                    listOf(
                        listOf(two, one),
                        listOf(one, -one)
                    ).toMatrix()
                } else {
                    // The above matrix is NOT invertible in F3
                    listOf(
                        listOf(two, one),
                        listOf(one, one)
                    ).toMatrix()
                }
                val expectedMat = matrixSpace.getIdentity(2)
                mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
            }
            "reduced row echelon form of ((1, 0, 0), (1, 0, 0), (0, 1, 0))" {
                // Replicate java.util.ConcurrentModificationException in the computation of
                // sparse row echelon form with in-place operations on MutableMap
                val mat = listOf(
                    listOf(one, zero, zero),
                    listOf(one, zero, zero),
                    listOf(zero, one, zero),
                ).toMatrix()
                val expectedMat = listOf(
                    listOf(one, zero, zero),
                    listOf(zero, one, zero),
                    listOf(zero, zero, zero),
                ).toMatrix()
                mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
            }
            "reduced row echelon form of non-invertible matrix" {
                val mat = listOf(
                    listOf(zero, zero, one),
                    listOf(-two, -one, zero),
                    listOf(two, one, zero)
                ).toMatrix()
                val expectedMat = if (field.characteristic == 2) {
                    listOf(
                        listOf(zero, one, zero),
                        listOf(zero, zero, one),
                        listOf(zero, zero, zero)
                    ).toMatrix()
                } else {
                    listOf(
                        listOf(one, one / two, zero),
                        listOf(zero, zero, one),
                        listOf(zero, zero, zero)
                    ).toMatrix()
                }
                mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
            }
            "reduced row echelon form of a matrix containing a zero row" {
                val mat = listOf(
                    listOf(one, two, three),
                    listOf(zero, zero, zero),
                    listOf(one, one, one),
                ).toMatrix()
                val expectedMat = listOf(
                    listOf(one, zero, -one),
                    listOf(zero, one, two),
                    listOf(zero, zero, zero),
                ).toMatrix()
                mat.rowEchelonForm.reducedMatrix shouldBe expectedMat
            }
            "transpose of ((1, 2), (3, 4)) should be ((1, 3), (2, 4))" {
                val mat = listOf(
                    listOf(one, two),
                    listOf(three, four)
                ).toMatrix()
                val expectedMat = listOf(
                    listOf(one, three),
                    listOf(two, four)
                ).toMatrix()
                mat.transpose() shouldBe expectedMat
            }
            "inner product of (1, 2) and (3, 4) w.r.t ((-1, 0), (-2, 2)) should be 1" {
                val mat = listOf(
                    listOf(-one, zero),
                    listOf(-two, two)
                ).toMatrix()
                val v = listOf(one, two).toNumVector()
                val w = listOf(three, four).toNumVector()
                mat.innerProduct(v, w) shouldBe one
            }
            "kernel of zero matrix should have the standard basis" {
                val dim = 5
                val mat = matrixSpace.getZero(dim)
                val expected = (0 until dim).map { numVectorSpace.getOneAtIndex(it, dim) }
                mat.computeKernelBasis() shouldBe expected
            }
            "compute kernel of ((1, 1), (2, 2))" {
                val mat = listOf(
                    listOf(one, one),
                    listOf(two, two)
                ).toMatrix()
                val kernelBasis = mat.computeKernelBasis()
                kernelBasis.size shouldBe 1
                (mat * kernelBasis[0]).isZero().shouldBeTrue()
            }
            "compute kernel of ((1, 2, 3), (0, 0, 0), (0, 0, 0))" {
                val mat = listOf(
                    listOf(one, two, three),
                    listOf(zero, zero, zero),
                    listOf(zero, zero, zero),
                ).toMatrix()
                val kernelBasis = mat.computeKernelBasis()
                kernelBasis.size shouldBe 2
                kernelBasis.forAll { v ->
                    (mat * v).isZero().shouldBeTrue()
                }
            }
            "compute kernel of ((1, 1), (1, 3))" {
                val mat = listOf(
                    listOf(one, one),
                    listOf(one, three),
                ).toMatrix()
                val kernelBasis = mat.computeKernelBasis()
                if (matrixSpace.field.characteristic == 2) {
                    kernelBasis.size shouldBe 1
                    (mat * kernelBasis[0]).isZero().shouldBeTrue()
                } else {
                    kernelBasis.size shouldBe 0
                }
            }
            "image of zero matrix should be zero" {
                val dim = 5
                val mat = matrixSpace.getZero(dim)
                mat.computeImageBasis().shouldBeEmpty()
            }
            "image of the identity matrix should have the standard basis" {
                val dim = 5
                val mat = matrixSpace.getIdentity(dim)
                val expected = (0 until dim).map { numVectorSpace.getOneAtIndex(it, dim) }
                mat.computeImageBasis() shouldBe expected
            }
            "compute image of ((1, 1), (2, 2))" {
                val mat = listOf(
                    listOf(one, one),
                    listOf(two, two)
                ).toMatrix()
                val imageBasis = mat.computeImageBasis()
                imageBasis.size shouldBe 1
                val expected = listOf(
                    listOf(one, two).toNumVector()
                )
                imageBasis shouldBe expected
            }
            "join of ((1, 2), (3, 4)) and ((-1, -2), (-3, -4)) should be ((1, 2, -1, -2), (3, 4, -3, -4))" {
                val mat1 = listOf(
                    listOf(one, two),
                    listOf(three, four),
                ).toMatrix()
                val mat2 = -mat1
                val expected = listOf(
                    listOf(one, two, -one, -two),
                    listOf(three, four, -three, -four),
                ).toMatrix()
                listOf(mat1, mat2).join() shouldBe expected
            }
            "test rowSlice and colSlice" {
                val intRange = 1..2
                val matrix = (0 until 4).map { row ->
                    (0 until 4).map { col -> (row * 4 + col).toScalar() }
                }.toMatrix()
                val expectedRowSlice = listOf(
                    listOf(4, 5, 6, 7).map { it.toScalar() },
                    listOf(8, 9, 10, 11).map { it.toScalar() },
                ).toMatrix()
                val expectedColSlice = listOf(
                    listOf(1, 2).map { it.toScalar() },
                    listOf(5, 6).map { it.toScalar() },
                    listOf(9, 10).map { it.toScalar() },
                    listOf(13, 14).map { it.toScalar() },
                ).toMatrix()
                matrix.rowSlice(intRange) shouldBe expectedRowSlice
                matrix.colSlice(intRange) shouldBe expectedColSlice
            }
            "findPreimage(zero) should return zero" {
                val mat = listOf(
                    listOf(one, two, three),
                    listOf(-one, four, zero),
                ).toMatrix()
                val zero2 = numVectorSpace.getZero(2)
                val zero3 = numVectorSpace.getZero(3)
                mat.findPreimage(zero2) shouldBe zero3
            }
            "findPreimage should throw InvalidSizeException when a numVector of wrong size is given" {
                val zero3 = numVectorSpace.getZero(3)
                shouldThrow<InvalidSizeException> {
                    m.findPreimage(zero3)
                }
            }
            "findPreimage should return null for numVector not in the image" {
                val mat = listOf(
                    listOf(two, -two),
                    listOf(one, -one)
                ).toMatrix()
                val numVector = listOf(one, one).toNumVector()
                mat.findPreimage(numVector) shouldBe null
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> rowEchelonFormGenTest(
    matrixSpace: MatrixSpace<S, V, M>,
    rowCount: Int,
    colCount: Int,
    max: Int = 100,
) = freeSpec {
    "test for row echelon form with generator (rowCount = $rowCount, colCount = $colCount)" - {
        val field = matrixSpace.field
        val scalarArb = field.arb(Arb.int(-max..max))
        val matrixArb = matrixSpace.arb(scalarArb, rowCount, colCount)
        "multiplying reducedTransformation should give the reduced row echelon form" {
            checkAll(matrixArb) { mat ->
                matrixSpace.context.run {
                    val reducedRowEchelonForm: M = mat.rowEchelonForm.reducedMatrix
                    val reducedTransformation: M = mat.rowEchelonForm.reducedTransformation
                    (reducedTransformation * mat) shouldBe reducedRowEchelonForm
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findPreimageGenTest(
    matrixSpace: MatrixSpace<S, V, M>,
    dimSource: Int,
    dimTarget: Int,
    max: Int = 100,
) = freeSpec {
    "test for findPreimage with generator (dimSource = $dimSource, dimTarget = $dimTarget)" - {
        val field = matrixSpace.field
        val scalarArb = field.arb(Arb.int(-max..max))
        val numVectorArb = matrixSpace.numVectorSpace.arb(scalarArb, dimSource)
        val matrixArb = matrixSpace.arb(scalarArb, rowCount = dimTarget, colCount = dimSource)
        "findPreimage should return an element of preimage" {
            checkAll(numVectorArb, matrixArb) { sourceNumVector, matrix ->
                matrixSpace.context.run {
                    val targetNumVector = matrix * sourceNumVector
                    val preimageNumVector = matrix.findPreimage(targetNumVector)
                    preimageNumVector shouldNotBe null
                    preimageNumVector as V
                    (matrix * preimageNumVector) shouldBe targetNumVector
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> matrixOfRank2Test(
    matrixSpace: MatrixSpace<S, V, M>,
    max: Int = 100
) = freeSpec {
    "test for matrices of rank2" - {
        // val vectorSpace = DenseNumVectorSpace.from(field)
        val field = matrixSpace.field
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
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> determinantTest(matrixSpace: MatrixSpace<S, V, M>, n: Int, max: Int) = freeSpec {
    "determinant test" - {
        if (n < 0) throw IllegalArgumentException("Matrix size n should be non-negative")
        if (max < 0) throw IllegalArgumentException("max should be non-negative")
        val field = matrixSpace.field
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
        "determinant of matrices of rank 2 should be ad-bc" {
            val matrixArb = matrixSpace.arb(scalarArb, 2, 2)
            checkAll(matrixArb) { mat ->
                matrixSpace.context.run {
                    mat.det() shouldBe MatrixOfRank2(matrixSpace, mat).det()
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> notImplementedDeterminantTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "determinant should throw NotImplementedError" {
        matrixSpace.context.run {
            val matrix = listOf(
                listOf(one, two),
                listOf(three, four)
            ).toMatrix()
            shouldThrow<NotImplementedError> {
                matrix.det()
            }
        }
    }
}

const val maxValueForDet = 100
const val matrixSizeForDet = 4
// 5 でも一応できるけど、
// - Rational の test に2秒くらいかかる
// - LongRational の test が(乱数次第で)たまに overflow する

class IntRationalDenseMatrixTest : FreeSpec({
    tags(matrixTag, denseMatrixTag, intRationalTag)

    val matrixSpace = DenseMatrixSpaceOverIntRational
    include(denseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace, 10))
    // include(determinantTest(IntRationalField, 3, 5)) // overflow しがちなので除外
    // include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    // include(rowEchelonFormGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "DenseMatrixSpace(IntRationalField)"
    }
})

class LongRationalDenseMatrixTest : FreeSpec({
    tags(matrixTag, denseMatrixTag, longRationalTag)

    val matrixSpace = DenseMatrixSpaceOverLongRational
    include(denseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    // include(determinantTest(LongRationalField, matrixSizeForDet, 5)) // overflow しがちなので除外
    // include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    // include(rowEchelonFormGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "DenseMatrixSpace(LongRationalField)"
    }
})

class RationalDenseMatrixTest : FreeSpec({
    tags(matrixTag, denseMatrixTag, rationalTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(denseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "DenseMatrixSpace(RationalField)"
    }
})

class IntMod2DenseMatrixTest : FreeSpec({
    tags(matrixTag, denseMatrixTag, intModpTag)

    val matrixSpace = DenseMatrixSpaceOverF2
    include(denseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class IntMod5DenseMatrixTest : FreeSpec({
    tags(matrixTag, denseMatrixTag, intModpTag)

    val matrixSpace = DenseMatrixSpaceOverF5
    include(denseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class RationalSparseMatrixTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, rationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(sparseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "SparseMatrixSpace(RationalField)"
    }
})

class RationalDecomposedSparseMatrixTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, rationalTag)

    val matrixSpace = DecomposedSparseMatrixSpace.from(SparseNumVectorSpaceOverRational)
    // include(sparseMatrixSpaceTest(RationalField))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    // include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet)) // sign is not implemented
    include(notImplementedDeterminantTest(matrixSpace)) // This should be replaced if sign is implemented
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))

    "test toString()" {
        matrixSpace.toString() shouldBe "DecomposedSparseMatrixSpace(RationalField)"
    }
})

class RationalSparseMatrixNonInPlaceCalculatorTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, rationalTag)

    val matrixSpace = SparseMatrixSpace(
        SparseNumVectorSpaceOverRational,
        SparseRowEchelonFormCalculator(SparseNumVectorSpaceOverRational.field),
    )
    // include(sparseMatrixSpaceTest(matrixSpace)) // fails around cache
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class RationalSparseMatrixInPlaceCalculatorTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, rationalTag)

    val matrixSpace = SparseMatrixSpace(
        SparseNumVectorSpaceOverRational,
        InPlaceSparseRowEchelonFormCalculator(SparseNumVectorSpaceOverRational.field),
    )
    // include(sparseMatrixSpaceTest(matrixSpace)) // fails around cache
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class IntMod2SparseMatrixTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, intModpTag)

    val matrixSpace = SparseMatrixSpaceOverF2
    include(sparseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class IntMod3SparseMatrixTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, intModpTag)

    val matrixSpace = SparseMatrixSpaceOverF3
    include(sparseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})

class IntMod5SparseMatrixTest : FreeSpec({
    tags(matrixTag, sparseMatrixTag, intModpTag)

    val matrixSpace = SparseMatrixSpaceOverF5
    include(sparseMatrixSpaceTest(matrixSpace))
    include(matrixTest(matrixSpace))
    include(matrixOfRank2Test(matrixSpace))
    include(determinantTest(matrixSpace, matrixSizeForDet, maxValueForDet))
    include(rowEchelonFormGenTest(matrixSpace, 3, 3))
    include(rowEchelonFormGenTest(matrixSpace, 4, 3))
    include(findPreimageGenTest(matrixSpace, 3, 3))
    include(findPreimageGenTest(matrixSpace, 4, 3))
})
