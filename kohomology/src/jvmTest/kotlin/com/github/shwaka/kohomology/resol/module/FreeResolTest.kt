package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.bar.FreeResol
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF7
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe

val freeResolTag = NamedTag("FreeResol")

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testFreeResolOfCyclicGroup(
    order: Int,
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    require(order > 1)
    val coeffAlgebra = MonoidRing(CyclicGroup(order), matrixSpace)
    val complex = FreeResol(coeffAlgebra)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Z/$order]" - {
        val maxDegree = 10

        "underlyingDGVectorSpace[degree].dim should be 0 or $order" {
            (-maxDegree..maxDegree).forAll { degree ->
                complex.underlyingDGVectorSpace[degree].dim shouldBeIn listOf(0, order)
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when {
                    (order == matrixSpace.field.characteristic) -> when {
                        (degree > 0) -> 0
                        else -> 1
                    }
                    else -> when (degree) {
                        0 -> 1
                        else -> 0
                    }
                }
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testFreeResolOfFiedorowiczMonoid(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    // Z. Fiedorowicz,
    // A counterexample to a group completion conjecture of J. C. Moore,
    // Algebr. Geom. Topol., 2002
    val elements = listOf("1", "x1", "x2", "y1", "y2")
    val multiplicationTable = listOf(
        listOf("1", "x1", "x2", "y1", "y2"),
        listOf("x1", "x1", "x1", "y1", "y1"),
        listOf("x2", "x2", "x2", "y2", "y2"),
        listOf("y1", "x1", "x1", "y1", "y1"),
        listOf("y2", "x2", "x2", "y2", "y2"),
    )
    val monoid = FiniteMonoidFromList(elements, multiplicationTable)
    val coeffAlgebra = MonoidRing(monoid, matrixSpace)
    val complex = FreeResol(coeffAlgebra)
    val field = matrixSpace.field

    "test with free resolution of $field over $field[Fiedorowicz monoid]" - {
        val maxDegree = 10

        "underlyingDGVectorSpace[degree].dim should be not greater than 15" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when {
                    (degree == 0) -> 5
                    (degree == -1) -> 10
                    (degree <= -2) -> 15
                    else -> 0
                }
                complex.underlyingDGVectorSpace[degree].dim shouldBe expected
            }
        }

        "cohomology of underlyingDGVectorSpace should be 0 except for degree 0" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                complex.underlyingDGVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }

        "test cohomology of tensorWithBaseField" {
            (-maxDegree..maxDegree).forAll { degree ->
                val expected = when (degree) {
                    0, -2 -> 1
                    else -> 0
                }
                complex.tensorWithBaseField.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class FreeResolTest : FreeSpec({
    tags(moduleTag, freeResolTag)

    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(2, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(3, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(5, SparseMatrixSpaceOverF5))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverRational))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF2))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF3))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF5))
    include(testFreeResolOfCyclicGroup(7, SparseMatrixSpaceOverF7))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverRational))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverF2))
    include(testFreeResolOfFiedorowiczMonoid(SparseMatrixSpaceOverF3))
})
