package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.BasisName
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

val dgVectorSpaceTag = NamedTag("DGVectorSpace")

suspend inline fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.checkRequirementsForDGVectorSpace(
    dgVectorSpace: DGVectorSpace<D, B, S, V, M>
) {
    "check requirements for an implementation of DGVectorSpace" - {
        "check classes of underlyingGVectorSpace" {
            dgVectorSpace.underlyingGVectorSpace::class.simpleName shouldBe "GVectorSpaceImpl"
            dgVectorSpace.cohomology.underlyingGVectorSpace::class.simpleName shouldBe
                "SubQuotGVectorSpaceImpl"
        }

        "gVector should an element of dgVectorSpace" {
            val v = dgVectorSpace.getZero(0)
            (v in dgVectorSpace).shouldBeTrue()
            v.gVectorSpace shouldBeSameInstanceAs dgVectorSpace.underlyingGVectorSpace
            v.gVectorSpace shouldNotBe dgVectorSpace
        }

        "gVector should be an element of the underlying graded vector space" {
            val v = dgVectorSpace.getZero(0)
            (v in dgVectorSpace.underlyingGVectorSpace).shouldBeTrue()
            v.gVectorSpace.underlyingGVectorSpace shouldBeSameInstanceAs
                dgVectorSpace.underlyingGVectorSpace
        }

        "cohomology class should be an element of the cohomology" {
            dgVectorSpace.context.run {
                val v = dgVectorSpace.getZero(0)
                (v.cohomologyClass() in dgVectorSpace.cohomology).shouldBeTrue()
                v.cohomologyClass().gVectorSpace.underlyingGVectorSpace shouldBeSameInstanceAs
                    dgVectorSpace.cohomology.underlyingGVectorSpace
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgVectorSpaceTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    "DGVectorSpace with trivial differential" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        val dgVectorSpace = DGVectorSpace.fromGVectorSpace(matrixSpace, gVectorSpace)

        checkRequirementsForDGVectorSpace(dgVectorSpace)

        dgVectorSpace.context.run {
            "dimension of the cohomology should be the same as that of the dg vector space" {
                (-10 until 10).forAll { degree ->
                    dgVectorSpace.cohomology[degree].dim shouldBe dgVectorSpace[degree].dim
                }
            }
        }
    }
}

class DGVectorSpaceTest : FreeSpec({
    tags(dgVectorSpaceTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(dgVectorSpaceTest(matrixSpace))
})
