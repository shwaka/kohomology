package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.LinearMap
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val gLinearMapTag = NamedTag("GLinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> gLinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val gVectorSpace = GVectorSpace.fromBasisNames(numVectorSpace, "V") { degree ->
        (0 until degree).map { "v$it" }
    }
    // val field = matrixSpace.field
    // val zero = field.zero
    // val one = field.one
    val gLinearMap = GLinearMap(gVectorSpace, gVectorSpace, 1) { degree ->
        val targetBasis = gVectorSpace[degree + 1].getBasis()
        val valueList = (0 until degree).map { i ->
            gVectorSpace[degree + 1].context.run {
                targetBasis[i] + targetBasis[i + 1]
            }
        }
        LinearMap.fromVectors(gVectorSpace[degree], gVectorSpace[degree + 1], matrixSpace, valueList)
        // val rows = (0 until (degree + 1)).map { i ->
        //     (0 until degree).map { j ->
        //         when (i) {
        //             j -> one
        //             j + 1 -> one
        //             else -> zero
        //         }
        //     }
        // }
        // val matrix = matrixSpace.fromRows(rows)
        // LinearMap(gVectorSpace[degree], gVectorSpace[degree + 1], matrix)
    }

    gVectorSpace.context.run {
        "graded linear map test" {
            val (v0, v1) = gVectorSpace.getBasis(2)
            val (w0, w1, w2) = gVectorSpace.getBasis(3)
            gLinearMap(v0) shouldBe (w0 + w1)
            gLinearMap(v1) shouldBe (w1 + w2)
            gLinearMap(v0 + v1) shouldBe (w0 + w1 * 2 + w2)
        }
    }
}

class BigRationalGLinearMapTest : StringSpec({
    tags(gLinearMapTag, bigRationalTag)

    include(gLinearMapTest(DenseMatrixSpaceOverBigRational))
})
