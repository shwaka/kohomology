package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val simplicialComplexTag = NamedTag("SimplicialComplex")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> deltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "Delta[$dim]" - {
        val simplicialComplex = delta(matrixSpace, dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace
        "check dimension of homology" {
            dgVectorSpace.cohomology[0].dim shouldBe 1
            (1..(dim + 2)).forAll { i ->
                dgVectorSpace.cohomology[-i].dim shouldBe 0
                dgVectorSpace.gVectorSpace[i].dim shouldBe 0
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> boundaryDeltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "BoundaryDelta[$dim]" - {
        val simplicialComplex = boundaryDelta(matrixSpace, dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace
        "check dimension of homology" {
            (-(dim + 2)..(dim + 2)).forAll { i ->
                val expected = when (i) {
                    0, -(dim - 1) -> 1
                    else -> 0
                }
                dgVectorSpace.cohomology[i].dim shouldBe expected
            }
        }
    }
}

class SimplicialComplexTest : FreeSpec({
    tags(simplicialComplexTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(deltaTest(matrixSpace, 5))
    include(boundaryDeltaTest(matrixSpace, 5))
})
