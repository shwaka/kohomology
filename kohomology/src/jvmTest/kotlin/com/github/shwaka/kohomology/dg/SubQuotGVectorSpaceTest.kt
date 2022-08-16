package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val subQuotGVectorSpaceTag = NamedTag("SubQuotGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subQuotGVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "sub-quotient graded vector space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val subQuotVectorSpace = run {
            val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
            val (u, v, w) = totalVectorSpace.getBasis()
            totalVectorSpace.context.run {
                val subspaceGenerator = listOf(u + v, v + w)
                val quotientGenerator = listOf(u + 2 * v + w)
                SubQuotVectorSpace(matrixSpace, totalVectorSpace, subspaceGenerator, quotientGenerator)
            }
        }
        val subQuotGVectorSpace = SubQuotGVectorSpace(
            matrixSpace.numVectorSpace,
            IntDegreeGroup,
            "V",
            { InternalPrintConfig.default(it) },
            null,
        ) { _ -> subQuotVectorSpace }

        "check dimension" {
            (-5..5).forAll { degree ->
                subQuotGVectorSpace[degree].dim shouldBe 1
            }
        }

        "check classes" {
            val degree = 0
            val (x) = subQuotGVectorSpace.getBasis(degree)
            x.gVectorSpace::class.simpleName shouldBe "SubQuotGVectorSpaceImpl"
            x.vector.vectorSpace::class.simpleName shouldBe "SubQuotVectorSpaceImpl"
        }
    }
}

class SubQuotGVectorSpaceTest : FreeSpec({
    tags(subQuotGVectorSpaceTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(subQuotGVectorSpaceTest(matrixSpace))
})
