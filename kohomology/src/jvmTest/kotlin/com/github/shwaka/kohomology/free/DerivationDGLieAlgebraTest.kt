package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.checkGLieAlgebraAxioms
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
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

        "check dimension" {
            ((-3 * sphereDim) until 0).forAll { degree ->
                val expected = if (degree == -(2 * sphereDim - 1)) 1 else 0
                derivationDGLieAlgebra.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class DerivationDGLieAlgebraTest : FreeSpec({
    tags(derivationDGLieAlgebraTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(derivationDGLieAlgForEvenSphereTest(matrixSpace, 4))
})
