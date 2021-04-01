package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.map
import kotlin.math.absoluteValue

val freeGAlgebraTag = NamedTag("FreeGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> polynomialTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int, maxPolynomialLength: Int = 5) = stringSpec {
    if (generatorDegree == 0)
        throw IllegalArgumentException("Invalid test parameter: generatorDegree must be non-zero")
    if (generatorDegree % 2 == 1)
        throw IllegalArgumentException("Invalid test parameter: generatorDegree must be even")
    if (maxPolynomialLength <= 0)
        throw IllegalArgumentException("Invalid test parameter: maxPolynomialLength must be positive")
    val generatorList = listOf(
        Indeterminate("x", generatorDegree),
        Indeterminate("y", generatorDegree),
    )
    val freeGAlgebra = FreeGAlgebra(matrixSpace, generatorList)
    // val lengthGen = exhaustive((0..maxPolynomialLength).toList())
    val multipleDegreeGen = exhaustive((0..maxPolynomialLength).toList()).map { i -> Pair(generatorDegree * i, i + 1) }
    "[polynomial, deg=$generatorDegree] freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
        checkAll(multipleDegreeGen) { (degree, expectedDim) ->
            freeGAlgebra[degree].dim shouldBe expectedDim
        }
    }
    "[polynomial, deg=$generatorDegree] freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
        val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
        checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
            val degree = multipleDegree + additionalDegree
            freeGAlgebra[degree].dim shouldBe 0
        }
    }
    "[polynomial, deg=$generatorDegree] check multiplication" {
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.withGAlgebraContext {
            (x + y).pow(0) shouldBe freeGAlgebra.unit
            (x + y).pow(1) shouldBe (x + y)
            (x + y).pow(2) shouldBe (x.pow(2) + 2 * x * y + y.pow(2))
            (x - y).pow(3) shouldBe (x.pow(3) - 3 * x.pow(2) * y + 3 * x * y.pow(2) - y.pow(3))
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> exteriorTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int) = stringSpec {
    if (generatorDegree % 2 == 0)
        throw IllegalArgumentException("Invalid test parameter: generatorDegree must be odd")
    val generatorList = listOf(
        Indeterminate("x", generatorDegree),
        Indeterminate("y", generatorDegree),
    )
    val freeGAlgebra = FreeGAlgebra(matrixSpace, generatorList)
    val multipleDegreeGen = exhaustive(
        listOf(
            Pair(0, 1),
            Pair(generatorDegree, 2),
            Pair(2 * generatorDegree, 1),
            Pair(3 * generatorDegree, 0)
        )
    )
    "[exterior, deg=$generatorDegree] freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
        checkAll(multipleDegreeGen) { (degree, expectedDim) ->
            freeGAlgebra[degree].dim shouldBe expectedDim
        }
    }
    "[exterior, deg=$generatorDegree] freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
        if (generatorDegree.absoluteValue >= 2) {
            // exhaustive の中身が empty list だとエラーを吐く
            val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
            checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
                val degree = multipleDegree + additionalDegree
                freeGAlgebra[degree].dim shouldBe 0
            }
        }
    }
    "[exterior, deg=$generatorDegree] check multiplication" {
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.withGAlgebraContext {
            (x + y).pow(0) shouldBe freeGAlgebra.unit
            (x + y).pow(1) shouldBe (x + y)
            (y * x) shouldBe (-x * y)
            (x + y).pow(2).isZero().shouldBeTrue()
        }
    }
}

class FreeGAlgebraTest : StringSpec({
    tags(freeGAlgebraTag, bigRationalTag)

    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(polynomialTest(matrixSpace, 2))
    include(polynomialTest(matrixSpace, 4))
    include(polynomialTest(matrixSpace, -2))

    include(exteriorTest(matrixSpace, 1))
    include(exteriorTest(matrixSpace, 3))
    include(exteriorTest(matrixSpace, -3))

    "derivation test (2-dim sphere)" {
        val generatorList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, generatorList)
        freeGAlgebra.withGAlgebraContext {
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
        val generatorList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, generatorList)
        @Suppress("UnnecessaryVariable")
        freeGAlgebra.withGAlgebraContext {
            val (x, y) = freeGAlgebra.generatorList
            val dx = y
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
})
