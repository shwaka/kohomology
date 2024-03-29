package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.checkGLieAlgebraAxioms
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val derivationGLieAlgebraTag = NamedTag("DerivationGLieAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationGLieAlgForEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    "derivations on even dimensional sphere" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 != 0)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val freeGAlgebra = sphere(matrixSpace, sphereDim)
        val derivationGLieAlgebra = DerivationGLieAlgebra(freeGAlgebra)

        checkGLieAlgebraAxioms(derivationGLieAlgebra, (-4 * sphereDim)..(4 * sphereDim))

        "check dimension" {
            derivationGLieAlgebra[-sphereDim].dim shouldBe 1
            derivationGLieAlgebra[-(sphereDim - 1)].dim shouldBe 1
            derivationGLieAlgebra[-(2 * sphereDim - 1)].dim shouldBe 1
        }
        val (dx) = derivationGLieAlgebra.getBasis(-sphereDim)
        val (dy) = derivationGLieAlgebra.getBasis(-(2 * sphereDim - 1))
        val (xdy) = derivationGLieAlgebra.getBasis(-(sphereDim - 1))
        val (x2dy) = derivationGLieAlgebra.getBasis(1)
        derivationGLieAlgebra.context.run {
            "check bracket" {
                (dx * xdy) shouldBe dy
                (xdy * dx) shouldBe (-dy)
                (dx * dy).isZero().shouldBeTrue()
                (dy * xdy).isZero().shouldBeTrue()
            }
            "check value of gVectorToDerivation()" {
                val (x, y) = freeGAlgebra.generatorList
                val dxDerivation = derivationGLieAlgebra.gVectorToDerivation(dx)
                val dyDerivation = derivationGLieAlgebra.gVectorToDerivation(dy)
                val xdyDerivation = derivationGLieAlgebra.gVectorToDerivation(xdy)
                freeGAlgebra.context.run {
                    dxDerivation(x) shouldBe unit
                    dxDerivation(y).isZero().shouldBeTrue()
                    dxDerivation(x.pow(3)) shouldBe (3 * x.pow(2))
                    dyDerivation(y) shouldBe unit
                    dyDerivation(x * y) shouldBe x
                    dyDerivation(x).isZero().shouldBeTrue()
                    xdyDerivation(y) shouldBe x
                    xdyDerivation(x * y) shouldBe x.pow(2)
                }
            }
            "check derivationToGVector(gVectorToDerivation(f)) == f" {
                for (f in listOf(dx, dy, xdy)) {
                    derivationGLieAlgebra.derivationToGVector(
                        derivationGLieAlgebra.gVectorToDerivation(f)
                    ) shouldBe f
                }
            }
            "check ad()" {
                ad(dx)(xdy) shouldBe dy
                ad(dy)(dx).isZero().shouldBeTrue()
            }
            "check toString()" {
                dx.toString() shouldBe "(x, 1)"
                dy.toString() shouldBe "(y, 1)"
                xdy.toString() shouldBe "(y, x)"
                (-dx).toString() shouldBe "- (x, 1)"
                (2 * xdy).toString() shouldBe "2 (y, x)"
                x2dy.toString() shouldBe "(y, x^2)"
            }
        }
    }

    "printer test" - {
        val indeterminateList = listOf(
            Indeterminate("x", "X", sphereDim),
            Indeterminate("y", "Y", 2 * sphereDim - 1),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val derivationGLieAlgebra = DerivationGLieAlgebra(freeGAlgebra)
        val texPrinter = Printer(PrintType.TEX)

        "print Der itself" {
            derivationGLieAlgebra.toString() shouldBe "Der(Λ(x, y))"
            texPrinter(derivationGLieAlgebra) shouldBe "\\mathrm{Der}(Λ(X, Y))"
        }

        "print elements" - {
            val (dx) = derivationGLieAlgebra.getBasis(-sphereDim)
            val (dy) = derivationGLieAlgebra.getBasis(-(2 * sphereDim - 1))
            val (xdy) = derivationGLieAlgebra.getBasis(-(sphereDim - 1))
            val (x2dy) = derivationGLieAlgebra.getBasis(1)

            derivationGLieAlgebra.context.run {
                "as plain text" {
                    dx.toString() shouldBe "(x, 1)"
                    dy.toString() shouldBe "(y, 1)"
                    xdy.toString() shouldBe "(y, x)"
                    (-dx).toString() shouldBe "- (x, 1)"
                    (2 * xdy).toString() shouldBe "2 (y, x)"
                    x2dy.toString() shouldBe "(y, x^2)"
                }
                "as latex code" {
                    texPrinter(dx) shouldBe "(X, 1)"
                    texPrinter(dy) shouldBe "(Y, 1)"
                    texPrinter(xdy) shouldBe "(Y, X)"
                    texPrinter(-dx) shouldBe "- (X, 1)"
                    texPrinter(2 * xdy) shouldBe "2 (Y, X)"
                    texPrinter(x2dy) shouldBe "(Y, X^{2})"
                }
            }
        }
    }
}

class DerivationGLieAlgebraTest : FreeSpec({
    tags(derivationGLieAlgebraTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(derivationGLieAlgForEvenSphereTest(matrixSpace, 4))
})
