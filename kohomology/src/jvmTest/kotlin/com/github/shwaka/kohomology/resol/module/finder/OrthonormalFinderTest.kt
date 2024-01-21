package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.FieldProduct
import com.github.shwaka.kohomology.resol.module.FreeModule
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> orthonormalFinderTest(
    dim: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "test OrthonormalFinder with ${matrixSpace.field}^$dim" {
        val fieldProduct = FieldProduct(dim, matrixSpace)
        val finder = OrthonormalFinder(fieldProduct, fieldProduct.getBasis())
        val module = FreeModule(fieldProduct, listOf(StringBasisName("x")))

        finder.find(module) shouldHaveSize 1
    }
}

class OrthonormalFinderTest : FreeSpec({
    tags(moduleTag)

    include(orthonormalFinderTest(1, SparseMatrixSpaceOverRational))
    include(orthonormalFinderTest(3, SparseMatrixSpaceOverRational))
    include(orthonormalFinderTest(3, SparseMatrixSpaceOverF2))
})
