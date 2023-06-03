package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val quotVectorSpaceTag = NamedTag("QuotVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
quotVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "quot space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            val quotientGenerator = listOf(u - v)
            val quotVectorSpace = QuotVectorSpace(matrixSpace, vectorSpace, quotientGenerator)

            "check dimension" {
                quotVectorSpace.dim shouldBe  2
            }
            "check basis names" {
                val (x, y) = quotVectorSpace.getBasis()
                x.toString() shouldBe "[u]"
                y.toString() shouldBe "[w]"
            }
            "check projection and section" {
                val (x, y) = quotVectorSpace.getBasis()
                val proj = quotVectorSpace.projection
                val sect = quotVectorSpace.section
                proj(u) shouldBe x
                proj(v) shouldBe x
                proj(w) shouldBe y
                proj(sect(x)) shouldBe x
                proj(sect(y)) shouldBe y
            }
        }
    }
    "quot space test with redundant generator" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            val quotientGenerator = listOf(zeroVector, u - v, zeroVector, u - v)
            val quotVectorSpace = QuotVectorSpace(matrixSpace, vectorSpace, quotientGenerator)

            "check dimension" {
                quotVectorSpace.dim shouldBe  2
            }
            "check basis names" {
                val (x, y) = quotVectorSpace.getBasis()
                x.toString() shouldBe "[u]"
                y.toString() shouldBe "[w]"
            }
            "check projection and section" {
                val (x, y) = quotVectorSpace.getBasis()
                val proj = quotVectorSpace.projection
                val sect = quotVectorSpace.section
                proj(u) shouldBe x
                proj(v) shouldBe x
                proj(w) shouldBe y
                proj(sect(x)) shouldBe x
                proj(sect(y)) shouldBe y
            }
        }
    }
}

class QuotVectorSpaceTest : FreeSpec({
    tags(quotVectorSpaceTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(quotVectorSpaceTest(matrixSpace))
})
