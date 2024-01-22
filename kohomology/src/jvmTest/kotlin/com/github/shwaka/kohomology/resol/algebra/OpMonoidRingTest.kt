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
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> opMonoidRingTest(
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
    val opMonoidRing = OpMonoidRing(monoidRing)

    "test OpAlgebra over $matrixSpace" - {
        "monoidRing and opAlgebra should be non-commutative" {
            monoidRing.isCommutative.shouldBeFalse()
            opMonoidRing.isCommutative.shouldBeFalse()
        }

        "multiplication should be the opposite" {
            for (v in opMonoidRing.getBasis()) {
                for (w in opMonoidRing.getBasis()) {
                    opMonoidRing.multiply(v, w) shouldBe monoidRing.multiply(w, v)
                }
            }
        }

        "opAlgebra.isOppositeOf(monoidRing) should be true" {
            opMonoidRing.isOppositeOf(monoidRing).shouldBeTrue()
            opMonoidRing.isOppositeOf(opMonoidRing).shouldBeFalse()
        }
    }
}

class OpMonoidRingTest : FreeSpec({
    tags(algebraTag)

    include(opAlgebraTest(SparseMatrixSpaceOverRational))
    include(opAlgebraTest(SparseMatrixSpaceOverF2))
})
