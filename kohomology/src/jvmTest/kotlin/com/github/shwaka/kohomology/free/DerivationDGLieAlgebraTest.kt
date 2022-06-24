package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.checkDGLieAlgebraAxioms
import com.github.shwaka.kohomology.example.complexProjectiveSpace
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val derivationDGLieAlgebraTag = NamedTag("DerivationDGLieAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationDGLieAlgForEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    "derivations on even dimensional sphere" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 != 0)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val derivationDGLieAlgebra = DerivationDGLieAlgebra(freeDGAlgebra)

        checkDGLieAlgebraAxioms(derivationDGLieAlgebra, (-4 * sphereDim)..(4 * sphereDim))

        "check dimension" {
            ((-4 * sphereDim) until 0).forAll { degree ->
                val expected = if (degree == -(2 * sphereDim - 1)) 1 else 0
                derivationDGLieAlgebra.cohomology[degree].dim shouldBe expected
            }
        }

        val (dx) = derivationDGLieAlgebra.gLieAlgebra.getBasis(-sphereDim)
        val (dy) = derivationDGLieAlgebra.gLieAlgebra.getBasis(-(2 * sphereDim - 1))
        val (xdy) = derivationDGLieAlgebra.gLieAlgebra.getBasis(-(sphereDim - 1))

        "dgVectorToDerivation(gVector) should throw if d(gVector) != 0" {
            shouldThrow<IllegalArgumentException> {
                derivationDGLieAlgebra.gVectorToDGDerivation(dx)
            }
        }

        "check value of dgVectorToDerivation()" {
            val (x, y) = freeDGAlgebra.gAlgebra.generatorList
            val dyDgDerivation = derivationDGLieAlgebra.gVectorToDGDerivation(dy)
            val xdyDgDerivation = derivationDGLieAlgebra.gVectorToDGDerivation(xdy)
            freeDGAlgebra.context.run {
                dyDgDerivation(x).isZero().shouldBeTrue()
                dyDgDerivation(y) shouldBe unit
                xdyDgDerivation(x).isZero().shouldBeTrue()
                xdyDgDerivation(y) shouldBe x
                xdyDgDerivation(x * y) shouldBe x.pow(2)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationDGLieAlgForCPnTest(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
) = freeSpec {
    "derivations on complex projective space" - {
        val freeDGAlgebra = complexProjectiveSpace(matrixSpace, n)
        val derivationDGLieAlgebra = DerivationDGLieAlgebra(freeDGAlgebra)

        checkDGLieAlgebraAxioms(derivationDGLieAlgebra, (-4 * n)..(4 * n))

        "check dimension" {
            val nonTrivialDegrees = (0 until n).map { i -> 2 * i - 2 * n - 1 }
            ((-4 * n) until 0).forAll { degree ->
                val expected = if (degree in nonTrivialDegrees) 1 else 0
                derivationDGLieAlgebra.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationDGLieAlgForNonFormalSpaceTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "derivations on a non-formal space" - {
        val indeterminateList = listOf(
            Indeterminate("x", 3),
            Indeterminate("y", 5),
            Indeterminate("z", 7),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y, _) ->
            listOf(zeroGVector, zeroGVector, x * y)
        }
        val derivationDGLieAlgebra = DerivationDGLieAlgebra(freeDGAlgebra)

        checkDGLieAlgebraAxioms(derivationDGLieAlgebra, -20..20)

        "check dimension" {
            val nonTrivialDegrees = listOf(-7, -2)
            (-20 until 0).forAll { degree ->
                val expected = if (degree in nonTrivialDegrees) 1 else 0
                derivationDGLieAlgebra.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationPrinterTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "printer test" {
        val indeterminateList = listOf(
            Indeterminate("x", "X", 2),
            Indeterminate("y", "Y", 2),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { listOf(zeroGVector, zeroGVector) }
        val derivationDGLieAlgebra = DerivationDGLieAlgebra(freeDGAlgebra)
        val texPrinter = Printer(PrintType.TEX)

        derivationDGLieAlgebra.toString() shouldBe "(Der(Λ(x, y)), d)"
        texPrinter(derivationDGLieAlgebra) shouldBe "(\\mathrm{Der}(Λ(X, Y)), d)"
    }
}

class DerivationDGLieAlgebraTest : FreeSpec({
    tags(derivationDGLieAlgebraTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(derivationDGLieAlgForEvenSphereTest(matrixSpace, 4))
    include(derivationDGLieAlgForCPnTest(matrixSpace, 4))
    include(derivationDGLieAlgForNonFormalSpaceTest(matrixSpace))
    include(derivationPrinterTest(matrixSpace))
})
