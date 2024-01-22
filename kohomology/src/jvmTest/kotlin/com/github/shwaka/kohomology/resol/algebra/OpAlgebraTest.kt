package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> opAlgebraTest(
    matrixSpace: MatrixSpace<S, V, M>
) = freeSpec {
    // non-commutative monoid of order 3
    val monoid = FiniteMonoidFromList(
        elements = listOf("1", "x", "y"),
        multiplicationTable = listOf(
            listOf("1", "x", "y"),
            listOf("x", "x", "x"),
            listOf("y", "y", "y"),
        ),
        name = "M",
    )
    val monoidRing = MonoidRing(monoid, matrixSpace)
    val opAlgebra = OpAlgebra(monoidRing)

    "test OpAlgebra over $matrixSpace" - {
        "monoidRing and opAlgebra should be non-commutative" {
            monoidRing.isCommutative.shouldBeFalse()
            opAlgebra.isCommutative.shouldBeFalse()
        }

        "multiplication should be the opposite" {
            for (v in opAlgebra.getBasis()) {
                for (w in opAlgebra.getBasis()) {
                    opAlgebra.multiply(v, w) shouldBe monoidRing.multiply(w, v)
                }
            }
        }
    }
}

class OpAlgebraTest : FreeSpec({
    tags(algebraTag)

    include(opAlgebraTest(SparseMatrixSpaceOverRational))
    include(opAlgebraTest(SparseMatrixSpaceOverF2))
})
