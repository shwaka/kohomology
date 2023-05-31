package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val subQuotGVectorSpaceTag = NamedTag("SubQuotGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subQuotGVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "sub-quotient graded vector space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val subQuotVectorSpace = run {
            val (u, v, w) = totalVectorSpace.getBasis()
            totalVectorSpace.context.run {
                val subspaceGenerator = listOf(u + v, v + w)
                val quotientGenerator = listOf(u + 2 * v + w)
                SubQuotVectorSpace(matrixSpace, totalVectorSpace, subspaceGenerator, quotientGenerator)
            }
        }
        val totalGVectorSpace = GVectorSpace(
            numVectorSpace,
            IntDegreeGroup,
            "V",
        ) { _ -> totalVectorSpace }
        val subQuotGVectorSpace = SubQuotGVectorSpace(
            matrixSpace,
            totalGVectorSpace,
            "W",
        ) { _ -> subQuotVectorSpace }

        "check subQuotGVectorSpace.totalGVectorSpace" {
            subQuotGVectorSpace.totalGVectorSpace shouldBe totalGVectorSpace
        }

        "check dimension" {
            (-5..5).forAll { degree ->
                subQuotGVectorSpace[degree].dim shouldBe 1
            }
        }

        "check classes" {
            val degree = 0
            val (x) = subQuotGVectorSpace.getBasis(degree)
            x.gVectorSpace::class.simpleName shouldBe "SubQuotGVectorSpaceImpl"
            x.vector.vectorSpace::class.simpleName shouldBe "SubQuotVectorSpace"
        }

        "test section" {
            (-5..5).forAll { degree ->
                val (u, v, _) = totalGVectorSpace.getBasis(degree)
                val (x) = subQuotGVectorSpace.getBasis(degree)
                val sect = subQuotGVectorSpace.section
                totalGVectorSpace.context.run {
                     sect(x) shouldBe u + v
                }
            }
        }

        "test projection" {
            (-5..5).forAll { degree ->
                val (u, v, w) = totalGVectorSpace.getBasis(degree)
                val (x) = subQuotGVectorSpace.getBasis(degree)
                val proj = subQuotGVectorSpace.projection
                totalGVectorSpace.context.run {
                    proj(u + v) shouldBe x
                    proj(-v - w) shouldBe x
                    proj(u + 2 * v + w).isZero().shouldBeTrue()
                }
            }
        }

        "test subspaceContains" {
            (-5..5).forAll { degree ->
                val (u, v, w) = totalGVectorSpace.getBasis(degree)
                totalGVectorSpace.context.run {
                    subQuotGVectorSpace.subspaceContains(u + v).shouldBeTrue()
                    subQuotGVectorSpace.subspaceContains(v + w).shouldBeTrue()
                    subQuotGVectorSpace.subspaceContains(u - w).shouldBeTrue()
                    subQuotGVectorSpace.subspaceContains(u + w).shouldBeFalse()
                }
            }
        }
    }
}

class SubQuotGVectorSpaceTest : FreeSpec({
    tags(subQuotGVectorSpaceTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(subQuotGVectorSpaceTest(matrixSpace))
})
