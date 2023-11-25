package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.bar.ProjectiveResol
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val projectiveResolTag = NamedTag("ProjectiveResol")

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testHomologyOfCyclicGroup(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    require(order > 1)
    val coeffAlgebra = MonoidRing(CyclicGroup(order), matrixSpace)
    val complex = ProjectiveResol(coeffAlgebra)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Z/$order]" - {
        val maxDegree = 3
        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of dgVectorSpaceWithoutCoeff" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when {
                    (order == matrixSpace.field.characteristic) -> when {
                        (degree > 0) -> 0
                        else -> 1
                    }
                    else -> when (degree) {
                        0 -> 1
                        else -> 0
                    }
                }
                complex.dgVectorSpaceWithoutCoeff.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class ProjectiveResolTest : FreeSpec({
    tags(moduleTag, projectiveResolTag)

    include(testHomologyOfCyclicGroup(2, SparseMatrixSpaceOverRational))
    include(testHomologyOfCyclicGroup(2, SparseMatrixSpaceOverF2))
    include(testHomologyOfCyclicGroup(2, SparseMatrixSpaceOverF3))
    include(testHomologyOfCyclicGroup(3, SparseMatrixSpaceOverRational))
    include(testHomologyOfCyclicGroup(3, SparseMatrixSpaceOverF2))
    include(testHomologyOfCyclicGroup(3, SparseMatrixSpaceOverF3))
})
