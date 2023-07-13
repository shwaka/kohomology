package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val quotGVectorSpaceTag = NamedTag("QuotGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
quotGVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "quotient graded vector space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val subVectorSpace = totalVectorSpace.context.run {
            val (u, v, _) = totalVectorSpace.getBasis()
            SubVectorSpace(matrixSpace, totalVectorSpace, listOf(u + v, u, v))
        }
        val totalGVectorSpace = GVectorSpace(
            numVectorSpace,
            IntDegreeGroup,
            "V",
        ) { _ -> totalVectorSpace }
        val subGVectorSpace = SubGVectorSpace(matrixSpace, totalGVectorSpace, "W") { _ -> subVectorSpace}
        val quotGVectorSpace = QuotGVectorSpace(
            matrixSpace,
            "W",
            totalGVectorSpace,
            subGVectorSpace,
        )

        "check quotGVectorSpace.totalGVectorSpace" {
            quotGVectorSpace.totalGVectorSpace shouldBe totalGVectorSpace
        }

        "check dimension" {
            (-5..5).forAll { degree ->
                quotGVectorSpace[degree].dim shouldBe 1
            }
        }

        "check classes" {
            val degree = 0
            val (x) = quotGVectorSpace.getBasis(degree)
            x.gVectorSpace::class.simpleName shouldBe "QuotGVectorSpaceImpl"
            x.vector.vectorSpace::class.simpleName shouldBe "QuotVectorSpaceImpl"
        }

        "test section" {
            (-5..5).forAll { degree ->
                val (_, _, w) = totalGVectorSpace.getBasis(degree)
                val (x) = quotGVectorSpace.getBasis(degree)
                val sect = quotGVectorSpace.section
                totalGVectorSpace.context.run {
                    sect(x) shouldBe w
                }
            }
        }

        "test projection" {
            (-5..5).forAll { degree ->
                val (u, v, w) = totalGVectorSpace.getBasis(degree)
                val (x) = quotGVectorSpace.getBasis(degree)
                val proj = quotGVectorSpace.projection
                totalGVectorSpace.context.run {
                    proj(u).isZero().shouldBeTrue()
                    proj(v).isZero().shouldBeTrue()
                    proj(w).isNotZero().shouldBeTrue()
                    proj(w) shouldBe x
                }
            }
        }
    }
}

class QuotGVectorSpaceTest : FreeSpec({
    tags(quotGVectorSpaceTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(quotGVectorSpaceTest(matrixSpace))
})
