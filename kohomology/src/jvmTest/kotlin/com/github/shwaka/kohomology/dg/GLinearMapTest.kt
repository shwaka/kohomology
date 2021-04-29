package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

val gLinearMapTag = NamedTag("GLinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> gLinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
        // V[n] = span{v0, v1,..., v{n-1}}
        (0 until degree).map { "v$it" }
    }
    val gLinearMap = GLinearMap(gVectorSpace, gVectorSpace, 1, matrixSpace, "f") { degree ->
        // vi -> vi + v{i+1}
        val n = degree.toInt()
        val targetBasis = gVectorSpace[n + 1].getBasis()
        val valueList = (0 until n).map { i ->
            gVectorSpace[n + 1].context.run {
                targetBasis[i] + targetBasis[i + 1]
            }
        }
        LinearMap.fromVectors(gVectorSpace[n], gVectorSpace[n + 1], matrixSpace, valueList)
    }

    gVectorSpace.context.run {
        "graded linear map test" {
            val (v0, v1) = gVectorSpace.getBasis(2)
            val (w0, w1, w2) = gVectorSpace.getBasis(3)
            gLinearMap(v0) shouldBe (w0 + w1)
            gLinearMap(v1) shouldBe (w1 + w2)
            gLinearMap(v0 + v1) shouldBe (w0 + w1 * 2 + w2)
        }
        "findPreimage should return an element of preimage" {
            val (w0, w1, w2) = gVectorSpace.getBasis(3)
            val image = w0 + w1 * 2 + w2
            val preimage = gLinearMap.findPreimage(image)
            preimage shouldNotBe null
            preimage as GVector<StringBasisName, IntDegree, S, V>
            gLinearMap(preimage) shouldBe image
        }
        "findPreimage should return null for gVector not in the image" {
            val (w0, w1, w2) = gVectorSpace.getBasis(3)
            val nonImage = w0 + w1 + w2
            val preimage = gLinearMap.findPreimage(nonImage)
            preimage shouldBe null
        }
    }
}

class BigRationalGLinearMapTest : FreeSpec({
    tags(gLinearMapTag, bigRationalTag)

    include(gLinearMapTest(DenseMatrixSpaceOverBigRational))
})
