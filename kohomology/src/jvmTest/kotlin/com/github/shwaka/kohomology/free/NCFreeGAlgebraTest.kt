package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> noGeneratorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "NCFreeGAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()
        val ncFreeGAlgebra = shouldNotThrowAny {
            NCFreeGAlgebra(matrixSpace, indeterminateList)
        }
        ncFreeGAlgebra[0].dim shouldBe 1
        ncFreeGAlgebra.boundedness shouldBe Boundedness(upperBound = 0, lowerBound = 0)
    }
}

class NCFreeGAlgebraTest : FreeSpec({
    val matrixSpace = SparseMatrixSpaceOverRational

    include(noGeneratorTest(matrixSpace))
})
