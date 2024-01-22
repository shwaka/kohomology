package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> leftFreeTensorProductOverAlgebraTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
    // val (one, t) = coeffAlgebra.getBasis()
    val rightModule = run {
        // Z/2 acting on Q{x, y} by
        //   x*t = y, y*t = x
        val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val (x, y) = underlyingVectorSpace.getBasis()
        val action = ValueBilinearMap(
            source1 = underlyingVectorSpace,
            source2 = coeffAlgebra,
            target = underlyingVectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(x, y), // x*(-)
                listOf(y, x), // y*(-)
            )
        )
        RightModule(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
    }
    val leftModule = FreeModule(coeffAlgebra, listOf("a", "b", "c").map(::StringBasisName))
    val tensorProduct = LeftFreeTensorProductOverAlgebra(rightModule, leftModule)

    "test LeftFreeTensorProductOverAlgebra over $matrixSpace" - {
        "tensorProduct.dim should be the product of rightModule.dim and leftModule.generatingBasisNames.size" {
            val expected = rightModule.underlyingVectorSpace.dim * leftModule.generatingBasisNames.size
            tensorProduct.dim shouldBe expected
        }
    }
}

class LeftFreeTensorProductOverAlgebraTest : FreeSpec({
    tags(moduleTag)

    include(leftFreeTensorProductOverAlgebraTest(SparseMatrixSpaceOverRational))
    include(leftFreeTensorProductOverAlgebraTest(SparseMatrixSpaceOverF2))
})
