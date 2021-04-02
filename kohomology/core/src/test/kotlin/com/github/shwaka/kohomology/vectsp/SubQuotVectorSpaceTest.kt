package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val subQuotVectorSpaceTag = NamedTag("SubQuotVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subQuotVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
    val (u, v, w) = vectorSpace.getBasis()
    vectorSpace.withContext {
        val subspaceGenerator = listOf(u + v, v + w)
        val quotientGenerator = listOf(u + 2 * v + w)
        val subQuotVectorSpace = SubQuotVectorSpace(
            matrixSpace,
            vectorSpace,
            subspaceGenerator = subspaceGenerator,
            quotientGenerator = quotientGenerator
        )
        "sub quotient space test" {
            subQuotVectorSpace.dim shouldBe 1
            val x = subQuotVectorSpace.getBasis()[0]
            val sect = subQuotVectorSpace.section
            val proj = subQuotVectorSpace.projection
            // section(x) shouldBe (u + v) // depends on the choice
            proj(sect(x)) shouldBe x
            proj(u + 2 * v + w).isZero().shouldBeTrue()
            (proj(u - w) == proj(2 * (u + v))).shouldBeTrue()
        }
    }
}

class SubQuotVectorSpaceTest : StringSpec({
    tags(subQuotVectorSpaceTag, bigRationalTag)

    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(subQuotVectorSpaceTest(matrixSpace))
})
