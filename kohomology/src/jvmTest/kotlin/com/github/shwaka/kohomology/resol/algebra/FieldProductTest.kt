package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fieldProductTest(
    dim: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "test ${matrixSpace.field}^$dim" - {
        val fieldProduct = FieldProduct(dim, matrixSpace)
        val basis = fieldProduct.getBasis()

        fieldProduct.context.run {
            "fieldProduct.dim should be dim" {
                fieldProduct.dim shouldBe dim
            }

            "basis elements must be idempotent" {
                basis.forAll { e -> (e * e) shouldBe e }
            }

            "basis elements must be orthogonal" {
                basis.flatMap { e1 -> basis.map { e2 -> Pair(e1, e2) } }.forAll { (e1, e2) ->
                    if (e1 != e2) {
                        (e1 * e2).isZero().shouldBeTrue()
                    }
                }
            }

            "sum of basis elements must be the unit" {
                basis.sum() shouldBe unit
            }
        }
    }
}

class FieldProductTest : FreeSpec({
    tags(algebraTag)

    include(fieldProductTest(3, SparseMatrixSpaceOverRational))
})
