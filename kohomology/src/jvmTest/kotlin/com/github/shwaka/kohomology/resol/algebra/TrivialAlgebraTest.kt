package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> trivialAlgebraTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "test TrivialAlgebra with MatrixSpace $matrixSpace" - {
        val trivialAlgebra = TrivialAlgebra(matrixSpace)

        "trivialAlgebra.dim should be 1" {
            trivialAlgebra.dim shouldBe 1
        }

        "trivialAlgebra.unit should be idempotent" {
            trivialAlgebra.context.run {
                (unit * unit) shouldBe unit
            }
        }

        val anotherTrivialAlgebra = TrivialAlgebra(matrixSpace)

        "two instances of TrivialAlgebra should be equal if their matrixSpace are the same" {
            anotherTrivialAlgebra shouldBe trivialAlgebra
        }

        "trivialAlgebra.context should work for elements of anotherTrivialAlgebra" {
            val anotherUnit = anotherTrivialAlgebra.unit
            trivialAlgebra.context.run {
                (anotherUnit * anotherUnit) shouldBe anotherUnit
            }
        }
    }
}

class TrivialAlgebraTest : FreeSpec({
    tags(algebraTag)

    include(trivialAlgebraTest(SparseMatrixSpaceOverRational))
    include(trivialAlgebraTest(SparseMatrixSpaceOverF2))
})
