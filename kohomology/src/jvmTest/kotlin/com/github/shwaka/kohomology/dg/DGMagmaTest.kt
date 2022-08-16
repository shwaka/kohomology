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

val dgMagmaTag = NamedTag("DGMagma")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgMagmaTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace

    "DGMagma with trivial differential and mulitplication" - {
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            (0 until degree).map { "v$it" }
        }
        val dgMagma = DGMagma.fromGVectorSpace(matrixSpace, gVectorSpace)

        checkRequirementsForDGVectorSpace(dgMagma)
    }
}

class DGMagmaTest : FreeSpec({
    tags(dgMagmaTag)
    val matrixSpace = SparseMatrixSpaceOverRational

    include(dgMagmaTest(matrixSpace))
})
