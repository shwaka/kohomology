package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val dgVectorSpaceTag = NamedTag("DGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    "DGVectorSpace with trivial differential" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        val dgVectorSpace = DGVectorSpace.fromGVectorSpace(matrixSpace, gVectorSpace)

        "dimension of the cohomology should be the same as that of the dg vector space" {
            (-10 until 10).forAll { degree ->
                dgVectorSpace.cohomology[degree].dim shouldBe dgVectorSpace[degree].dim
            }
        }
    }
}

class DGVectorSpaceTest : FreeSpec({
    tags(dgVectorSpaceTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(dgVectorSpaceTest(matrixSpace))
})
