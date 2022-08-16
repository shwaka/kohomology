package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val dgMagmaTag = NamedTag("DGMagma")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgMagmaTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace

    "DGMagma with trivial differential and multiplication" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        val dgMagma = DGMagma.fromGVectorSpace(matrixSpace, gVectorSpace)

        checkRequirementsForDGVectorSpace(dgMagma)

        dgMagma.context.run {
            val (v0, v1) = gVectorSpace.getBasis(2)

            "check multiplication of cochains" {
                (v0 * v1).isZero().shouldBeTrue()
            }

            "check multiplication of cohomology classes" {
                dgMagma.cohomology.context.run {
                    (v0.cohomologyClass() * v1.cohomologyClass()).isZero().shouldBeTrue()
                }
            }

            "check classes" {
                v0.cohomologyClass().gVectorSpace::class.simpleName shouldBe "SubQuotGMagmaImpl"
                dgMagma.cohomology::class.simpleName shouldBe "SubQuotGMagmaImpl"
            }
        }
    }
}

class DGMagmaTest : FreeSpec({
    tags(dgMagmaTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(dgMagmaTest(matrixSpace))
})
