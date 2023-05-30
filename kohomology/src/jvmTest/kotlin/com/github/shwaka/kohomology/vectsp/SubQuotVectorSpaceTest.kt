package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val subQuotVectorSpaceTag = NamedTag("SubQuotVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subQuotVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "sub-quotient space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
        val (u, v, w) = vectorSpace.getBasis()
        vectorSpace.context.run {
            val subspaceGenerator = listOf(u + v, v + w)
            val quotientGenerator = listOf(u + 2 * v + w)
            val subQuotVectorSpace = SubQuotVectorSpace(
                matrixSpace,
                vectorSpace,
                subspaceGenerator = subspaceGenerator,
                quotientGenerator = quotientGenerator
            )
            "subQuotVectorSpace.totalVectorSpace should be vectorSpace" {
                subQuotVectorSpace.totalVectorSpace shouldBe vectorSpace
            }
            "check projection and section" {
                subQuotVectorSpace.dim shouldBe 1
                val x = subQuotVectorSpace.getBasis()[0]
                val sect = subQuotVectorSpace.section
                val proj = subQuotVectorSpace.projection
                // section(x) shouldBe (u + v) // depends on the choice
                proj(sect(x)) shouldBe x
                proj(u + 2 * v + w).isZero().shouldBeTrue()
                (proj(u - w) == proj(2 * (u + v))).shouldBeTrue()
            }
            "subspaceContains should work" {
                subQuotVectorSpace.subspaceContains(u + v).shouldBeTrue()
                subQuotVectorSpace.subspaceContains(v + w).shouldBeTrue()
                subQuotVectorSpace.subspaceContains(-u - 2 * v - w).shouldBeTrue()
                subQuotVectorSpace.subspaceContains(u).shouldBeFalse()
                subQuotVectorSpace.subspaceContains(zeroVector).shouldBeTrue()
            }
            "check class" {
                val x = subQuotVectorSpace.getBasis()[0]
                x.vectorSpace::class.simpleName shouldBe "SubQuotVectorSpace"
            }
        }
    }

    "getInternalPrintConfig should be inherited from subQuotVectorSpace.totalVectorSpace" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val vectorSpace = VectorSpace(numVectorSpace, listOf("y", "x", "z")) {
            InternalPrintConfig(
                basisComparator = compareBy { it.name },
                basisToString = { basisName -> basisName.name.replaceFirstChar { it.uppercase() } },
            )
        }
        val (y, x, _) = vectorSpace.getBasis()
        vectorSpace.context.run {
            "test SubQuotVectorSpaceImpl.getInternalPrintConfig" {
                val generator = listOf(y + x, x)
                val subQuotVectorSpace = SubQuotVectorSpace(
                    matrixSpace,
                    vectorSpace,
                    subspaceGenerator = generator,
                    quotientGenerator = emptyList(),
                )
                val (v, w) = subQuotVectorSpace.getBasis()
                v.toString() shouldBe "[X + Y]"
                w.toString() shouldBe "[X]"
            }
        }
    }
}

class SubQuotVectorSpaceTest : FreeSpec({
    tags(subQuotVectorSpaceTag, rationalTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(subQuotVectorSpaceTest(matrixSpace))
})
