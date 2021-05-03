package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.map
import kotlin.math.absoluteValue

val freeGAlgebraTag = NamedTag("FreeGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> noGeneratorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "GAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()
        val freeGAlgebra = shouldNotThrowAny {
            FreeGAlgebra(matrixSpace, indeterminateList)
        }
        freeGAlgebra[0].dim shouldBe 1
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> polynomialTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int, maxPolynomialLength: Int = 5) = freeSpec {
    "[polynomial, deg=$generatorDegree]" - {
        if (generatorDegree == 0)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be non-zero")
        if (generatorDegree % 2 == 1)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be even")
        if (maxPolynomialLength <= 0)
            throw IllegalArgumentException("Invalid test parameter: maxPolynomialLength must be positive")
        val indeterminateList = listOf(
            Indeterminate("x", generatorDegree),
            Indeterminate("y", generatorDegree),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        // val lengthGen = exhaustive((0..maxPolynomialLength).toList())
        val multipleDegreeGen = exhaustive((0..maxPolynomialLength).toList()).map { i -> Pair(generatorDegree * i, i + 1) }
        "freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
            checkAll(multipleDegreeGen) { (degree, expectedDim) ->
                freeGAlgebra[degree].dim shouldBe expectedDim
            }
        }
        "freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
            val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
            checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
                val degree = multipleDegree + additionalDegree
                freeGAlgebra[degree].dim shouldBe 0
            }
        }
        "check multiplication" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                (x + y).pow(0) shouldBe freeGAlgebra.unit
                (x + y).pow(1) shouldBe (x + y)
                (x + y).pow(2) shouldBe (x.pow(2) + 2 * x * y + y.pow(2))
                (x - y).pow(3) shouldBe (x.pow(3) - 3 * x.pow(2) * y + 3 * x * y.pow(2) - y.pow(3))
            }
        }
        "check algebra map" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val valueList = listOf(x + y, x - y)
                val f = freeGAlgebra.getGAlgebraMap(freeGAlgebra, valueList)
                f(x) shouldBe (x + y)
                f(y) shouldBe (x - y)
                f(x + y) shouldBe (2 * x)
                f(x * y) shouldBe (x.pow(2) - y.pow(2))
            }
        }
        "containsIndeterminate test" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.containsIndeterminate(0, x).shouldBeTrue()
            freeGAlgebra.containsIndeterminate(1, x).shouldBeFalse()
            freeGAlgebra.containsIndeterminate(0, y).shouldBeFalse()
            freeGAlgebra.containsIndeterminate(1, y).shouldBeTrue()
            val xy = freeGAlgebra.context.run {
                x * y
            }
            freeGAlgebra.containsIndeterminate(0, xy).shouldBeTrue()
            freeGAlgebra.containsIndeterminate(1, xy).shouldBeTrue()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> exteriorTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int) = freeSpec {
    "[exterior, deg=$generatorDegree]" - {
        if (generatorDegree % 2 == 0)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be odd")
        val indeterminateList = listOf(
            Indeterminate("x", generatorDegree),
            Indeterminate("y", generatorDegree),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val multipleDegreeGen = exhaustive(
            listOf(
                Pair(0, 1),
                Pair(generatorDegree, 2),
                Pair(2 * generatorDegree, 1),
                Pair(3 * generatorDegree, 0)
            )
        )
        "freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
            checkAll(multipleDegreeGen) { (degree, expectedDim) ->
                freeGAlgebra[degree].dim shouldBe expectedDim
            }
        }
        "freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
            if (generatorDegree.absoluteValue >= 2) {
                // exhaustive の中身が empty list だとエラーを吐く
                val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
                checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
                    val degree = multipleDegree + additionalDegree
                    freeGAlgebra[degree].dim shouldBe 0
                }
            }
        }
        "check multiplication" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                (x + y).pow(0) shouldBe freeGAlgebra.unit
                (x + y).pow(1) shouldBe (x + y)
                (y * x) shouldBe (-x * y)
                (x + y).pow(2).isZero().shouldBeTrue()
            }
        }
        "check algebra map" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val valueList = listOf(x + y, y)
                val f = freeGAlgebra.getGAlgebraMap(freeGAlgebra, valueList)
                f(x) shouldBe (x + y)
                f(y) shouldBe y
                f(x + y) shouldBe (x + 2 * y)
                f(x * y) shouldBe (x * y)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "derivation test (2-dim sphere)" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, y) = freeGAlgebra.generatorList
            val dx = zeroGVector
            val dy = x * x
            val d = freeGAlgebra.getDerivation(listOf(dx, dy), 1)
            d(x).isZero().shouldBeTrue()
            d(x.pow(4)).isZero().shouldBeTrue()
            d(y) shouldBe (x * x)
            d(x * y) shouldBe (x.pow(3))
            d(x.pow(3) * y) shouldBe (x.pow(5))
        }
    }

    "derivation test (contractible)" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, y) = freeGAlgebra.generatorList
            @Suppress("UnnecessaryVariable") val dx = y
            val dy = zeroGVector
            val d = freeGAlgebra.getDerivation(listOf(dx, dy), 1)
            d(y).isZero().shouldBeTrue()
            d(x) shouldBe y
            d(x.pow(2)) shouldBe (2 * x * y)
            d(x.pow(5)) shouldBe (5 * x.pow(4) * y)
            d(x * y).isZero().shouldBeTrue()
            d(x.pow(3) * y).isZero().shouldBeTrue()
        }
    }

    "getDerivation should throw IllegalArgumentException when an element of invalid degree is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, _) = freeGAlgebra.generatorList
            shouldThrow<IllegalArgumentException> {
                freeGAlgebra.getDerivation(listOf(zeroGVector, x), 1)
            }
        }
    }

    "getDerivation should throw InvalidSizeException when a list of invalid size is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            shouldThrow<InvalidSizeException> {
                freeGAlgebra.getDerivation(listOf(zeroGVector), 1)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> algebraMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "getAlgebraMap should throw IllegalArgumentException when an element of invalid degree is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, _) = freeGAlgebra.generatorList
            shouldThrow<IllegalArgumentException> {
                freeGAlgebra.getGAlgebraMap(freeGAlgebra, listOf(x, x.pow(2)))
            }
        }
    }

    "getAlgebraMap should throw InvalidSizeException when a list of invalid size is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            shouldThrow<InvalidSizeException> {
                freeGAlgebra.getGAlgebraMap(freeGAlgebra, listOf(zeroGVector))
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> parseTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "parse test".config(tags = setOf(parseTag)) {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.context.run {
            freeGAlgebra.parse("x * y") shouldBe (x * y)
            freeGAlgebra.parse("2 * x") shouldBe (2 * x)
            freeGAlgebra.parse("x * 2") shouldBe (2 * x)
            freeGAlgebra.parse("x*x - 2*x*y + y*y") shouldBe (x - y).pow(2)
            freeGAlgebra.parse("x^2 + y^2") shouldBe (x.pow(2) + y.pow(2))
            freeGAlgebra.parse("(x + y) * (x - y)") shouldBe (x.pow(2) - y.pow(2))
            freeGAlgebra.parse("(x+y)^3") shouldBe (x + y).pow(3)
            freeGAlgebra.parse("2 * (x + y)") shouldBe (2 * (x + y))
            freeGAlgebra.parse("zero") shouldBe zeroGVector
        }
    }
}

class FreeGAlgebraTest : FreeSpec({
    tags(freeGAlgebraTag, bigRationalTag)

    val matrixSpace = DenseMatrixSpaceOverBigRational

    include(noGeneratorTest(matrixSpace))

    include(polynomialTest(matrixSpace, 2))
    include(polynomialTest(matrixSpace, 4))
    include(polynomialTest(matrixSpace, -2))

    include(exteriorTest(matrixSpace, 1))
    include(exteriorTest(matrixSpace, 3))
    include(exteriorTest(matrixSpace, -3))

    include(derivationTest(matrixSpace))
    include(algebraMapTest(matrixSpace))

    include(parseTest(matrixSpace))
})
