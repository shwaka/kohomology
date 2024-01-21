package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

val vectorTag = NamedTag("Vector")

fun <S : Scalar, V : NumVector<S>> vectorTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "vector test" - {
        val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
        vectorSpace.context.run {
            "fromCoeffMap() should give the same vector as fromCoeffList()" {
                val v1 = vectorSpace.fromCoeffList(listOf(zero, one, two))
                val v2 = vectorSpace.fromCoeffMap(mapOf(1 to one, 2 to two))
                v2 shouldBe v1
            }
            "fromBasisMap(vector.toBasisMap()) should be vector" {
                listOf(
                    listOf(zero, one, two),
                    listOf(one, one, one),
                    listOf(zero, zero, zero),
                    listOf(-one, two, -three),
                ).forAll { coeffList ->
                    val vector = vectorSpace.fromCoeffList(coeffList)
                    vectorSpace.fromBasisMap(vector.toBasisMap()) shouldBe vector
                }
            }
            "fromBasisMap(basisMap).toBasisMap() should be basisMap" {
                listOf(
                    mapOf("a" to one, "b" to two),
                    mapOf("a" to one, "b" to two, "c" to three),
                    mapOf("a" to one, "b" to one, "c" to one),
                    mapOf(),
                ).forAll { basisMapWithStringKeys ->
                    val basisMap = basisMapWithStringKeys.mapKeys { (key, _) -> StringBasisName(key) }
                    vectorSpace.fromBasisMap(basisMap).toBasisMap() shouldBe basisMap
                }
            }
            "Vectors with same coefficients should return the same hashCode" {
                val v1 = vectorSpace.fromCoeffList(listOf(zero, one, two))
                val v2 = vectorSpace.fromCoeffList(listOf(zero, one, two))
                v1 shouldNotBeSameInstanceAs v2
                v1.hashCode() shouldBe v2.hashCode()
            }
            "addition of Vector" {
                val numVector = listOf(one, zero, one).toNumVector()
                val v = vectorSpace.fromNumVector(numVector)
                val expected = vectorSpace.fromNumVector(listOf(two, zero, two).toNumVector())
                (v + v) shouldBe expected
            }
            "invalid length of values should throw" {
                shouldThrow<InvalidSizeException> {
                    vectorSpace.fromCoeffList(listOf(zero, zero))
                }
            }
            "multiplication of scalar" {
                val v = vectorSpace.fromCoeffList(listOf(zero, two, -one))
                val expected = vectorSpace.fromCoeffList(listOf(zero, four, -two))
                (v * 2) shouldBe expected
                (v * two) shouldBe expected
            }
            "multiplication of Sign" {
                val v = vectorSpace.fromCoeffList(listOf(one, -two, zero))
                (v * Sign.PLUS) shouldBe v
                (Sign.PLUS * v) shouldBe v
                (v * Sign.MINUS) shouldBe -v
                (Sign.MINUS * v) shouldBe -v
            }
            "multiplication of scalar with extension functions" {
                val v = vectorSpace.fromCoeffList(listOf(zero, two, -one))
                val expected = vectorSpace.fromCoeffList(listOf(zero, four, -two))
                (2 * v) shouldBe expected
                (two * v) shouldBe expected
            }
            val (a, b, c) = vectorSpace.getBasis()
            "vectorSpace.divideByVector(2(a + b), a + b) should be 2" {
                vectorSpace.divideByVector(2 * (a + b), a + b) shouldBe two
            }
            "vectorSpace.divideByVector(2(a + b), a + b + c) should be null" {
                vectorSpace.divideByVector(2 * (a + b), a + b + c) shouldBe null
            }
            "vectorSpace.divideByVector(zeroVector, a + b + c) should be 0" {
                vectorSpace.divideByVector(zeroVector, a + b + c) shouldBe zero
            }
            "vectorSpace.divideByVector(2(a + b), zeroVector) should throw ArithmeticException" {
                shouldThrow<ArithmeticException> {
                    vectorSpace.divideByVector(2 * (a + b), zeroVector) shouldBe two
                }
            }
            "listOf(v, v, w).sum() should be 2 * v + w" {
                val v = vectorSpace.fromCoeffList(listOf(one, two, three))
                val w = vectorSpace.fromCoeffList(listOf(one / two, -three / five, four / three))
                listOf(v, v, w).sum() shouldBe (2 * v + w)
            }
            "emptyList().sum() should be zeroVector" {
                emptyList<Vector<StringBasisName, S, V>>().sum() shouldBe zeroVector
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>> printerTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "printer test" - {
        numVectorSpace.context.run {
            "default" - {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
                "(2a + 3b + 4c).toString() should be \"2 a + 3 b + 4 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, three, four))
                    v.toString() shouldBe "2 a + 3 b + 4 c"
                }
                "(0a + 3b + 4c).toString() should be \"3 b + 4 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, three, four))
                    v.toString() shouldBe "3 b + 4 c"
                }
                "(2a + (-3)b + 4c).toString() should be \"2 a - 3 b + 4 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, -three, four))
                    v.toString() shouldBe "2 a - 3 b + 4 c"
                }
                "((-2)a + 3b + 4c).toString() should be \"-2 a + 3 b + 4 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(-two, three, four))
                    v.toString() shouldBe "-2 a + 3 b + 4 c"
                }
                "(a + (1/2)b + (-1/3)c).toString() should be \"a + 1/2 b - 1/3 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(one, one / two, -one / three))
                    v.toString() shouldBe "a + 1/2 b - 1/3 c"
                }
                "(0a + 0b + 0c).toString() should be \"0\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, zero, zero))
                    v.toString() shouldBe "0"
                }
                "(-a).toString() should be \"- a\"" {
                    val v = vectorSpace.fromCoeffList(listOf(-one, zero, zero))
                    v.toString() shouldBe "- a"
                }
                "((-2)b).toString() should be \"-2 b\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, -two, zero))
                    v.toString() shouldBe "-2 b"
                }
            }
            "tex printer" - {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
                val texPrinter = Printer(PrintType.TEX)
                "(2a + 3b + 4c) should be printed as \"2 a + 3 b + 4 c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, three, four))
                    texPrinter(v) shouldBe "2 a + 3 b + 4 c"
                }
                "(a + (1/2)b + (-1/3)c)should be printed as \"a + \\frac{1}{2} b - \\frac{1}{3} c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(one, one / two, -one / three))
                    texPrinter(v) shouldBe "a + \\frac{1}{2} b - \\frac{1}{3} c"
                }
                "(0a + 0b + 0c) should be printed as \"0\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, zero, zero))
                    texPrinter(v) shouldBe "0"
                }
                "(-a) should be printed as \"- a\"" {
                    val v = vectorSpace.fromCoeffList(listOf(-one, zero, zero))
                    texPrinter(v) shouldBe "- a"
                }
                "((-2)b) should be printed as \"-2 b\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, -two, zero))
                    texPrinter(v) shouldBe "-2 b"
                }
                "listOf(a, b) should be printed as \"[a, b]\"" {
                    val a = vectorSpace.fromCoeffList(listOf(one, zero, zero))
                    val b = vectorSpace.fromCoeffList(listOf(zero, one, zero))
                    texPrinter(listOf(a, b)) shouldBe "[a, b]"
                }
            }
            "code printer" - {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("a", "b", "c"))
                val codePrinter = Printer(PrintType.CODE)
                "(2a + 3b + 4c) should be printed as \"2 * a + 3 * b + 4 * c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, three, four))
                    codePrinter(v) shouldBe "2 * a + 3 * b + 4 * c"
                }
                "(a + (1/2)b + (-1/3)c)should be printed as \"a + 1/2 * b - 1/3 * c\"" {
                    val v = vectorSpace.fromCoeffList(listOf(one, one / two, -one / three))
                    codePrinter(v) shouldBe "a + 1/2 * b - 1/3 * c"
                }
                "(0a + 0b + 0c) should be printed as \"0\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, zero, zero))
                    codePrinter(v) shouldBe "0"
                }
                "(-a) should be printed as \"- a\"" {
                    val v = vectorSpace.fromCoeffList(listOf(-one, zero, zero))
                    codePrinter(v) shouldBe "- a"
                }
                "((-2)b) should be printed as \"-2 * b\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, -two, zero))
                    codePrinter(v) shouldBe "-2 * b"
                }
                "listOf(a, b) should be printed as \"[a, b]\"" {
                    val a = vectorSpace.fromCoeffList(listOf(one, zero, zero))
                    val b = vectorSpace.fromCoeffList(listOf(zero, one, zero))
                    codePrinter(listOf(a, b)) shouldBe "[a, b]"
                }
            }
            "printer with comparator" - {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("y", "x", "z")) {
                    InternalPrintConfig(basisComparator = compareBy { it.name })
                }
                "(2y + 3x + 4z).toString() should be \"3 x + 2 y + 4 z\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, three, four))
                    v.toString() shouldBe "3 x + 2 y + 4 z"
                }
                "(0y + 3x + 4z).toString() should be \"3 x + 4 z\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, three, four))
                    v.toString() shouldBe "3 x + 4 z"
                }
                "(0y + 0x + 0z).toString() should be \"0\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, zero, zero))
                    v.toString() shouldBe "0"
                }
            }
            "TexVectorPrinter with comparator" - {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("y", "x", "z")) { printType ->
                    // PrintConfig(basisComparator = compareBy { it.name })
                    InternalPrintConfig.default<StringBasisName, S>(printType).copy(
                        basisComparator = compareBy { it.name }
                    )
                }
                val texPrinter = Printer(PrintType.TEX)
                "(2y + 3x + 4z) should be printed as \"3 x + 2 y + 4 z\"" {
                    val v = vectorSpace.fromCoeffList(listOf(two, three, four))
                    texPrinter(v) shouldBe "3 x + 2 y + 4 z"
                }
                "(y + (1/2)x + (-1/3)z) should be printed as \"\\frac{1}{2} x + y - \\frac{1}{3} z\"" {
                    val v = vectorSpace.fromCoeffList(listOf(one, one / two, -one / three))
                    texPrinter(v) shouldBe "\\frac{1}{2} x + y - \\frac{1}{3} z"
                }
            }
            "basis containing \"1\"" - {
                // GAlgebra is the actual example for this test.
                val vectorSpace = VectorSpace(numVectorSpace, listOf("x", "1", "y"))
                "(2 * 1) should be printed as \"2\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, two, zero))
                    v.toString() shouldBe "2"
                }
                "(- 2 * 1 + 3y) should be printed as \"-2 + 3 y\"" {
                    val v = vectorSpace.fromCoeffList(listOf(zero, -two, three))
                    v.toString() shouldBe "-2 + 3 y"
                }
                "(4x + 2 * 1) should be printed as \"4 x + 2\"" {
                    val v = vectorSpace.fromCoeffList(listOf(four, two, zero))
                    v.toString() shouldBe "4 x + 2"
                }
                "(4x - 2 * 1) should be printed as \"4 x - 2\"" {
                    val v = vectorSpace.fromCoeffList(listOf(four, -two, zero))
                    v.toString() shouldBe "4 x - 2"
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>> vectorSpaceTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    data class BasisElm(val name: String) : BasisName {
        override fun toString(): String = this.name
    }

    "test for vector space with custom class for basis" - {
        numVectorSpace.context.run {
            "custom class for basis" {
                val x = BasisElm("x")
                val y = BasisElm("y")
                val vectorSpace = VectorSpace(numVectorSpace, listOf(x, y))
                val v = vectorSpace.fromCoeffList(listOf(one, zero))
                shouldNotThrowAny {
                    v.toString()
                }
            }

            "getBasis should return the correct basis" {
                val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
                val v = vectorSpace.fromCoeffList(listOf(one, zero))
                val w = vectorSpace.fromCoeffList(listOf(zero, one))
                vectorSpace.getBasis() shouldBe listOf(v, w)
            }
        }
    }
}

@Suppress("UNUSED_VARIABLE")
fun <S : Scalar, V : NumVector<S>> manyBasisTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "destructuring declaration should work for basis with many elements" {
        val basisNames = (1..15).map { "v$it" }
        val vectorSpace = VectorSpace(numVectorSpace, basisNames)
        shouldNotThrowAny {
            val (v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15) = vectorSpace.getBasis()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> isBasisTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "isBasis test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
        val (v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            "isBasis should return true for a correct basis" {
                vectorSpace.isBasis(listOf(v, w), matrixSpace).shouldBeTrue()
                vectorSpace.isBasis(listOf(v - w, -2 * v + w), matrixSpace).shouldBeTrue()
            }
            "isBasis should return false for non-basis" {
                vectorSpace.isBasis(listOf(), matrixSpace).shouldBeFalse()
                vectorSpace.isBasis(listOf(v), matrixSpace).shouldBeFalse()
                vectorSpace.isBasis(listOf(v, v), matrixSpace).shouldBeFalse()
                vectorSpace.isBasis(listOf(v, w, v + w), matrixSpace).shouldBeFalse()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> isBasisForZeroTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "isBasis test concerning 0-dim vector space" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf<String>())
        vectorSpace.context.run {
            "empty list should be a basis of the zero vector space" {
                vectorSpace.isBasis(listOf(), matrixSpace).shouldBeTrue()
            }
            "non-empty list should not be a basis of the zero vector space" {
                val zeroVector = vectorSpace.zeroVector
                vectorSpace.isBasis(listOf(zeroVector), matrixSpace).shouldBeFalse()
                vectorSpace.isBasis(listOf(zeroVector, zeroVector), matrixSpace).shouldBeFalse()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> isGeneratedByTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "isGeneratedBy test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
        val (v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            "isGeneratedBy(basis) should be true" {
                vectorSpace.isGeneratedBy(vectorSpace.getBasis(), matrixSpace).shouldBeTrue()
            }
            "isGeneratedBy([v, v+w]) should be true" {
                vectorSpace.isGeneratedBy(
                    listOf(v, v + w),
                    matrixSpace
                ).shouldBeTrue()
            }
            "isGeneratedBy([v, v]) should be false" {
                vectorSpace.isGeneratedBy(
                    listOf(v, v),
                    matrixSpace
                ).shouldBeFalse()
            }
            "isGeneratedBy([v]) should be false" {
                vectorSpace.isGeneratedBy(
                    listOf(v),
                    matrixSpace
                ).shouldBeFalse()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> selectIndependentlyTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "selectIndependently test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("v", "w"))
        val (v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            "selectIndependently([v, w]) should be [v, w]" {
                vectorSpace.selectIndependently(listOf(v, w), matrixSpace) shouldBe listOf(v, w)
            }

            "selectIndependently([v, v, w]) should be [v, w]" {
                vectorSpace.selectIndependently(listOf(v, v, w), matrixSpace) shouldBe listOf(v, w)
            }

            "selectIndependently([v, w, w]) should be [v, w]" {
                vectorSpace.selectIndependently(listOf(v, w, w), matrixSpace) shouldBe listOf(v, w)
            }

            "selectIndependently([v + w, w]) should be [v, w]" {
                vectorSpace.selectIndependently(listOf(v + w, w), matrixSpace) shouldBe listOf(v + w, w)
            }

            "selectIndependently([0, v, w]) should be [v, w]" {
                vectorSpace.selectIndependently(listOf(zeroVector, v, w), matrixSpace) shouldBe listOf(v, w)
            }

            "selectIndependently([v, v]) should be [v]" {
                vectorSpace.selectIndependently(listOf(v, v), matrixSpace) shouldBe listOf(v)
            }
        }
    }
}

class RationalVectorTest : FreeSpec({
    tags(vectorTag, rationalTag)

    val numVectorSpace = DenseNumVectorSpaceOverRational
    include(vectorTest(numVectorSpace))
    include(vectorSpaceTest(numVectorSpace))
    include(manyBasisTest(numVectorSpace))
    include(printerTest(numVectorSpace))

    val matrixSpace = DenseMatrixSpaceOverRational
    include(isBasisTest(matrixSpace))
    include(isBasisForZeroTest(matrixSpace))
    include(isGeneratedByTest(matrixSpace))
    include(selectIndependentlyTest(matrixSpace))
})
