package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val gLinearMapTag = NamedTag("GLinearMap")

fun <S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> gLinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val gVectorSpace = GVectorSpace(numVectorSpace) { degree -> (0 until degree).map { "v$it" } }
    val field = matrixSpace.field
    val zero = field.zero
    val one = field.one

    "graded linear map test" {
        val gLinearMap = GLinearMap(gVectorSpace, gVectorSpace, 1) { degree ->
            val rows = (0 until (degree + 1)).map { i ->
                (0 until degree).map { j ->
                    when (i) {
                        j -> one
                        j + 1 -> one
                        else -> zero
                    }
                }
            }
            val matrix = matrixSpace.fromRows(rows)
            LinearMap(gVectorSpace[degree], gVectorSpace[degree + 1], matrix)
        }
        val (v0, v1) = gVectorSpace.getBasis(2)
        val (w0, w1, w2) = gVectorSpace.getBasis(3)
        gLinearMap(v0) shouldBe (w0 + w1)
        gLinearMap(v1) shouldBe (w1 + w2)
    }
}

class BigRationalGLinearMapTest : StringSpec({
    tags(gLinearMapTag, bigRationalTag)

    include(gLinearMapTest(DenseMatrixSpaceOverBigRational))
})
