package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF5
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

val simplicialComplexTag = NamedTag("SimplicialComplex")

private fun factorial(n: Int): Int {
    return when (n) {
        0, 1 -> 1
        else -> n * factorial(n - 1)
    }
}

private fun combination(n: Int, p: Int): Int {
    return factorial(n) / (factorial(p) * factorial(n - p))
}

suspend inline fun <Vertex : Comparable<Vertex>> FreeScope.axiomTestTemplate(
    name: String,
    simplicialComplex: SimplicialComplex<Vertex>,
) {
    "check axioms of a simplicial complex $name" - {
        val numOfVertices = simplicialComplex.vertices.size
        "dimensions of simplices should be correct" {
            (0..(2 * numOfVertices)).forAll { dim ->
                simplicialComplex.getSimplices(dim).forAll { simplex ->
                    simplex.dim shouldBe dim
                }
            }
        }
        "any face of a simplex should be a simplex" {
            (1..(2 * numOfVertices)).forAll { dim ->
                simplicialComplex.getSimplices(dim).forAll { simplex ->
                    val simplices = simplicialComplex.getSimplices(dim - 1)
                    simplex.faceList.forAll { face ->
                        simplices.contains(face).shouldBeTrue()
                    }
                }
            }
        }
    }
}

fun axiomTest() = freeSpec {
    "check axioms of simplicial complexes" - {
        val n = 5
        axiomTestTemplate("Δ^$n", delta(n))
        axiomTestTemplate("∂Δ^$n", boundaryDelta(n))
        axiomTestTemplate("RP^2", projectivePlane())
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> deltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "Delta[$dim]" - {
        val simplicialComplex = delta(dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "check dimension of complex" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when {
                    (degree > 0 || degree < -dim) -> 0
                    else -> combination(dim + 1, -degree + 1)
                }
                dgVectorSpace[degree].dim shouldBe expected
            }
        }
        "check dimension of homology" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
        "check vertices" {
            simplicialComplex.vertices shouldBe (0..dim).toList()
        }
        "maximal face should be the top simplex" {
            (0 until dim).forAll { i ->
                simplicialComplex.getMaximalFaces(i).shouldBeEmpty()
            }
            simplicialComplex.getMaximalFaces(dim) shouldBe
                listOf(Simplex.fromSorted((0..dim).toList()))
        }
        "test generatedBy" {
            val generatingSimplices = mapOf(
                dim to listOf(Simplex.fromSorted((0..dim).toList()))
            )
            val generatedSimplicialComplex = SimplicialComplex.generatedBy(generatingSimplices)
            val generatedDGVectorSpace = generatedSimplicialComplex.dgVectorSpace(matrixSpace)
            (0..(dim + 1)).forAll { i ->
                generatedSimplicialComplex.getSimplices(i).size shouldBe simplicialComplex.getSimplices(i).size
            }
            (-1..(dim + 1)).forAll { i ->
                generatedDGVectorSpace.cohomology[-i].dim shouldBe dgVectorSpace.cohomology[-i].dim
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> boundaryDeltaTest(matrixSpace: MatrixSpace<S, V, M>, dim: Int) = freeSpec {
    "BoundaryDelta[$dim]" - {
        val simplicialComplex = boundaryDelta(dim)
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "check dimension of homology" {
            (-(dim + 2)..(dim + 2)).forAll { degree ->
                val expected = when (degree) {
                    0, -(dim - 1) -> 1
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
        "check vertices" {
            simplicialComplex.vertices shouldBe (0..dim).toList()
        }
        "the number of maximal faces should be dim + 1" {
            (0 until dim - 1).forAll { i ->
                simplicialComplex.getMaximalFaces(i).shouldBeEmpty()
            }
            simplicialComplex.getMaximalFaces(dim - 1).shouldHaveSize(dim + 1)
            simplicialComplex.getMaximalFaces(dim).shouldBeEmpty()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> projectivePlaneTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "real projective plane with coefficients in ${matrixSpace.field}" - {
        val simplicialComplex = projectivePlane()
        val dgVectorSpace = simplicialComplex.dgVectorSpace(matrixSpace)
        "Euler characteristic should be 1" {
            // This is independent of matrixSpace
            simplicialComplex.eulerCharacteristic() shouldBe 1
        }
        "check dimension of homology" {
            (-5..5).forAll { degree ->
                val expected = when (degree) {
                    0 -> 1
                    -1, -2 -> when (matrixSpace.field.characteristic) {
                        2 -> 1
                        else -> 0
                    }
                    else -> 0
                }
                dgVectorSpace.cohomology[degree].dim shouldBe expected
            }
        }
    }
}

class SimplicialComplexTest : FreeSpec({
    tags(simplicialComplexTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(deltaTest(matrixSpace, 5))
    include(boundaryDeltaTest(matrixSpace, 5))
    include(axiomTest())

    // projective plane
    include(projectivePlaneTest(SparseMatrixSpaceOverRational))
    include(projectivePlaneTest(SparseMatrixSpaceOverF2))
    include(projectivePlaneTest(SparseMatrixSpaceOverF3))
    include(projectivePlaneTest(SparseMatrixSpaceOverF5))
})
