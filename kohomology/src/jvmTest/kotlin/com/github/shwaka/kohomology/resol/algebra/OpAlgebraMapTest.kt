package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> opAlgebraMapTest(
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
    val id = AlgebraMap(
        source = monoidRing,
        target = monoidRing,
        underlyingLinearMap = monoidRing.getIdentity(matrixSpace),
    )
    val opAlgebra = OpAlgebra(monoidRing)
    val opId = OpAlgebraMap(
        source = opAlgebra,
        target = opAlgebra,
        originalAlgebraMap = id,
    )

    "test OpAlgebra over $matrixSpace" - {
        "monoidRing and opAlgebra should be non-commutative" {
            monoidRing.isCommutative.shouldBeFalse()
            opAlgebra.isCommutative.shouldBeFalse()
        }

        "test invoke" {
            val (one, x, y) = opAlgebra.getBasis()
            opId(one) shouldBe one
            opId(x) shouldBe x
            opId(y) shouldBe y
        }
    }
}

class OpAlgebraMapTest : FreeSpec({
    tags(algebraTag)

    include(opAlgebraTest(SparseMatrixSpaceOverRational))
    include(opAlgebraTest(SparseMatrixSpaceOverF3))
})
