package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF5
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val simplicialComplexTag = NamedTag("SimplicialComplex")

private fun factorial(n: Int): Int {
    return when (n) {
        0, 1 -> 1
        else -> n * factorial(n - 1)
    }
}

private fun combination(n: Int, p: Int): Int {
    return factorial(n) / (factorial(p) * factorial(n - p))
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> deltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "Delta[$dim]" - {
        val simplicialComplex = delta(dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "check dimension of complex" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when {
                    (degree > 0 || degree < -dim) -> 0
                    else -> combination(dim + 1, -degree + 1)
                }
                dgVectorSpace.gVectorSpace[degree].dim shouldBe expected
            }
        }
        "check dimension of homology" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> boundaryDeltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "BoundaryDelta[$dim]" - {
        val simplicialComplex = boundaryDelta(dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "check dimension of homology" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when (degree) {
                    0, -(dim - 1) -> 1
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> projectivePlaneTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "real projective plane with coefficients in ${matrixSpace.field}" - {
        val simplicialComplex = projectivePlane()
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "Euler characteristic should be 1" {
            // This is independent of matrixSpace
            simplicialComplex.eulerCharacteristic() shouldBe 1
        }
        "check dimension of homology" {
            (-5..5).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    -1, -2 -> when (matrixSpace.field.characteristic) {
                        2 -> 1
                        else -> 0
                    }
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class SimplicialComplexTest : FreeSpec({
    tags(simplicialComplexTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(deltaTest(matrixSpace, 5))
    include(boundaryDeltaTest(matrixSpace, 5))

    // projective plane
    include(projectivePlaneTest(SparseMatrixSpaceOverRational))
    include(projectivePlaneTest(SparseMatrixSpaceOverF2))
    include(projectivePlaneTest(SparseMatrixSpaceOverF3))
    include(projectivePlaneTest(SparseMatrixSpaceOverF5))
})
