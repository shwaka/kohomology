package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val subGVectorSpaceTag = NamedTag("SubGVectorSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
subGVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "sub graded vector space test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val subVectorSpace = run {
            val totalVectorSpace = VectorSpace(numVectorSpace, listOf("u", "v", "w"))
            val (u, v, w) = totalVectorSpace.getBasis()
            totalVectorSpace.context.run {
                val generator = listOf(u + v, v + w, u - w)
                SubVectorSpace(matrixSpace, totalVectorSpace, generator)
            }
        }
        val subGVectorSpace = SubGVectorSpace(
            matrixSpace.numVectorSpace,
            IntDegreeGroup,
            "V",
            { InternalPrintConfig.default(it) },
            null,
        ) { _ -> subVectorSpace }

        "check dimension" {
            (-5..5).forAll { degree ->
                subGVectorSpace[degree].dim shouldBe 2
            }
        }
    }
}

class SubGVectorSpaceTest : FreeSpec({
    tags(subGVectorSpaceTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(subGVectorSpaceTest(matrixSpace))
})
