package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.free.DerivationGLieAlgebra
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val derivationGLieAlgebraTag = NamedTag("DerivationGLieAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationForEvenSphereTest(matrixSpace: MatrixSpace<S, V, M>, sphereDim: Int) = freeSpec {
    "derivations on even dimensional sphere" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 != 0)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val freeGAlgebra = sphere(matrixSpace, sphereDim).gAlgebra
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
        }
    }
}

class DerivationGLieAlgebraTest : FreeSpec({
    tags(derivationGLieAlgebraTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(derivationForEvenSphereTest(matrixSpace, 4))
})
