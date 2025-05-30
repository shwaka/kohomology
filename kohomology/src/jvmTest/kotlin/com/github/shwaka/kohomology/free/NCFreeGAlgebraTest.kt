package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.pow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> noGeneratorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "NCFreeGAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()
        val ncFreeGAlgebra = shouldNotThrowAny {
            NCFreeGAlgebra(matrixSpace, indeterminateList)
        }
        ncFreeGAlgebra[0].dim shouldBe 1
        ncFreeGAlgebra.boundedness shouldBe Boundedness(upperBound = 0, lowerBound = 0)
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> twoGeneratorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test with NCFreeGAlgebra generated by two indeterminates" - {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
        )
        val ncFreeGAlgebra = NCFreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = ncFreeGAlgebra.generatorList

        "dimension at degree n should be 2^n" {
            (0..10).forAll { n ->
                ncFreeGAlgebra[n].dim shouldBe 2.pow(n)
            }
        }

        "dimension at negative degree should be 0" {
            (-10..-1).forAll { n ->
                ncFreeGAlgebra[n].dim shouldBe 0
            }
        }

        "ncFreeGAlgebra.generatorList should be [x, y]" {
            x.toString() shouldBe "x"
            y.toString() shouldBe "y"
        }

        ncFreeGAlgebra.context.run {
            "x*y should be xy" {
                (x * y).toString() shouldBe "xy"
            }

            "x*y should not be y*x" {
                (x * y) shouldNotBe (y * x)
            }

            "x^2 should not be 0" {
                x.pow(2).isZero().shouldBeFalse()
            }
        }
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> idealTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test the quotient T(x,y)/(xy-yx)" - {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
        )
        val ncFreeGAlgebra = NCFreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = ncFreeGAlgebra.generatorList
        val ideal = ncFreeGAlgebra.context.run {
            ncFreeGAlgebra.getIdeal(listOf(x * y - y * x))
        }
        val quotientGAlgebra = ncFreeGAlgebra.getQuotientByIdeal(ideal)
        val proj = quotientGAlgebra.projection

        "should have the same dim as polynomial ring" {
            (0..10).forAll { n ->
                quotientGAlgebra[n].dim shouldBe (n + 1)
            }
        }

        val a = proj(x)
        val b = proj(y)

        quotientGAlgebra.context.run {
            "x*y and y*x should be same in quotientGAlgebra" {
                (a * b) shouldBe (b * a)
            }

            "projection should be surjective" {
                (0..10).forAll { n ->
                    proj[n].isSurjective().shouldBeTrue()
                }
            }
        }
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pbwTest(
    matrixSpace: MatrixSpace<S, V, M>,
    a: Int,
) = freeSpec {
    // Polishchuk-Positselski, "Quadratic algebras", AMS
    // Section 4.10, p.97, Example
    // Correction is given in the following MO
    // https://mathoverflow.net/questions/34427/an-example-of-a-z-pbw-algebra-which-is-not-a-pbw-algebra

    "compute Polishchuk-Positselski's example" - {
        val indeterminateList = listOf(
            Indeterminate("x", 1),
            Indeterminate("y", 1),
            Indeterminate("z", 1),
        )
        val ncFreeGAlgebra = NCFreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y, z) = ncFreeGAlgebra.generatorList
        val aInv = ncFreeGAlgebra.context.run { a.toScalar().inv() }
        val xs = listOf(x, x, x)
        val ys = ncFreeGAlgebra.context.run { listOf(y - x, y + x, y - a * x) }
        val zs = ncFreeGAlgebra.context.run { listOf(z - x, z + aInv * x, z - aInv * x) }
        val initialGenerator = ncFreeGAlgebra.context.run {
            listOf(
                x.pow(2) + y * z,
                x.pow(2) + a * z * y,
            )
        }
        val zyx = listOf(0, 1).map { i ->
            ncFreeGAlgebra.context.run {
                listOf(
                    zs[i + 1] * zs[i],
                    zs[i + 1] * ys[i],
                    zs[i + 1] * xs[i],
                    ys[i + 1] * zs[i],
                    ys[i + 1] * ys[i],
                    ys[i + 1] * xs[i],
                    xs[i + 1] * zs[i],
                    xs[i + 1] * ys[i],
                    xs[i + 1] * xs[i],
                )
            }
        }

        "zyx[i][k] should be non-zero in a quotient except for k=6,7" {
            // The expected results in this test are computed by Macaulay2
            listOf(0, 1).forAll { i ->
                (0 until 9).forAll { k ->
                    val idealGenerator = initialGenerator + zyx[i].slice(0 until k)
                    val ideal = ncFreeGAlgebra.getIdeal(idealGenerator)
                    val quotientGAlgebra = ncFreeGAlgebra.getQuotientByIdeal(ideal)
                    val proj = quotientGAlgebra.projection
                    proj(zyx[i][k]).isZero() shouldBe when (k) {
                        6, 7 -> true
                        else -> false
                    }
                }
            }
        }

        "these x,y,z give a basis at degree 3" {
            val elements = ncFreeGAlgebra.context.run {
                listOf(
                    // ? * zs[1] * ?
                    zs[2] * zs[1] * zs[0],
                    zs[2] * zs[1] * ys[0],
                    zs[2] * zs[1] * xs[0],
                    ys[2] * zs[1] * zs[0],
                    ys[2] * zs[1] * ys[0],
                    ys[2] * zs[1] * xs[0],
                    // ? * ys[1] * ?
                    zs[2] * ys[1] * zs[0],
                    zs[2] * ys[1] * ys[0],
                    zs[2] * ys[1] * xs[0],
                    ys[2] * ys[1] * zs[0],
                    ys[2] * ys[1] * ys[0],
                    ys[2] * ys[1] * xs[0],
                    // ? * xs[1] * ?
                    zs[2] * xs[1] * xs[0],
                    ys[2] * xs[1] * xs[0],
                    xs[2] * xs[1] * xs[0],
                )
            }
            val ideal = ncFreeGAlgebra.getIdeal(initialGenerator)
            val quotientGAlgebra = ncFreeGAlgebra.getQuotientByIdeal(ideal)
            val proj = quotientGAlgebra.projection
            quotientGAlgebra.isBasis(elements.map { proj(it) }, 3).shouldBeTrue()
        }
    }
}

class NCFreeGAlgebraTest : FreeSpec({
    val matrixSpace = SparseMatrixSpaceOverRational

    include(noGeneratorTest(matrixSpace))
    include(twoGeneratorTest(matrixSpace))
    include(idealTest(matrixSpace))
    include(pbwTest(matrixSpace, 2))
})
