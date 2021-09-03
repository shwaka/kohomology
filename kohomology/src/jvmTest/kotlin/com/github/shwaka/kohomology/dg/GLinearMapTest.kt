package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
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
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

val gLinearMapTag = NamedTag("GLinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> gLinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "graded linear map test" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val gVectorSpace = GVectorSpace.fromStringBasisNamesWithIntDegree(numVectorSpace, "V") { degree ->
            // V[n] = span{v0, v1,..., v{n-1}}
            (0 until degree).map { "v$it" }
        }
        val gLinearMap = GLinearMap(gVectorSpace, gVectorSpace, 1, matrixSpace, "f") { degree ->
            // vi -> vi + v{i+1}
            val n = degree.value
            val targetBasis = gVectorSpace[n + 1].getBasis()
            val valueList = (0 until n).map { i ->
                gVectorSpace[n + 1].context.run {
                    targetBasis[i] + targetBasis[i + 1]
                }
            }
            LinearMap.fromVectors(gVectorSpace[n], gVectorSpace[n + 1], matrixSpace, valueList)
        }

        gVectorSpace.context.run {
            "check values" {
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
                preimage as GVector<IntDegree, StringBasisName, S, V>
                gLinearMap(preimage) shouldBe image
            }
            "findPreimage should return null for gVector not in the image" {
                val (w0, w1, w2) = gVectorSpace.getBasis(3)
                val nonImage = w0 + w1 + w2
                val preimage = gLinearMap.findPreimage(nonImage)
                preimage shouldBe null
            }
            "imageContains should return true for gVector in the image" {
                val (w0, w1, w2) = gVectorSpace.getBasis(3)
                val image = w0 + w1 * 2 + w2
                gLinearMap.imageContains(image).shouldBeTrue()
            }
            "imageContains should return false for gVector not in the image" {
                val (w0, w1, w2) = gVectorSpace.getBasis(3)
                val nonImage = w0 + w1 + w2
                gLinearMap.imageContains(nonImage).shouldBeFalse()
            }
            "kernelBasis() should return an empty list" {
                (0 until 20).forAll { degree ->
                    gLinearMap.kernelBasis(degree).shouldBeEmpty()
                }
            }
            "imageBasis(n) should return a list of length (n - 1) with elements of degree n" {
                gLinearMap.imageBasis(0).shouldBeEmpty()
                (1 until 20).forAll { degree ->
                    val imageBasis = gLinearMap.imageBasis(degree)
                    imageBasis.size shouldBe (degree - 1)
                    imageBasis.forAll { gVector ->
                        val gVectorDegree: IntDegree = gVector.degree
                        gVectorDegree shouldBe IntDegree(degree)
                    }
                }
            }
        }
    }
}

class BigRationalGLinearMapTest : FreeSpec({
    tags(gLinearMapTag, bigRationalTag)

    include(gLinearMapTest(DenseMatrixSpaceOverBigRational))
})
