package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithFreeResolution(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    require(order > 1)
    val complex = freeResolutionOverCyclicGroup(order, matrixSpace)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Z/$order]" - {
        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-10..10).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-10..10).forAll { degree ->
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
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class FreeComplexTest : FreeSpec({
    tags(moduleTag)

    include(testWithFreeResolution(2, SparseMatrixSpaceOverRational))
    include(testWithFreeResolution(2, SparseMatrixSpaceOverF2))
    include(testWithFreeResolution(2, SparseMatrixSpaceOverF3))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverRational))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverF2))
    include(testWithFreeResolution(3, SparseMatrixSpaceOverF3))
})
