package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseNumVectorSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val tensorProductTag = NamedTag("TensorProduct")

fun <S : Scalar, V : NumVector<S>> tensorProductTest(numVectorSpace: NumVectorSpace<S, V>) = freeSpec {
    "tensor product test" - {
        val vectorSpace1 = VectorSpace(numVectorSpace, listOf("v1", "v2"))
        val vectorSpace2 = VectorSpace(numVectorSpace, listOf("w1", "w2", "w3"))
        val tensorProduct = TensorProduct(vectorSpace1, vectorSpace2)
        val (v1, v2) = vectorSpace1.getBasis()
        val (w1, w2, w3) = vectorSpace2.getBasis()
        val v1w1 = tensorProduct.tensorProductOf(v1, w1)
        val v1w2 = tensorProduct.tensorProductOf(v1, w2)
        val v1w3 = tensorProduct.tensorProductOf(v1, w3)
        val v2w1 = tensorProduct.tensorProductOf(v2, w1)
        val v2w2 = tensorProduct.tensorProductOf(v2, w2)
        val v2w3 = tensorProduct.tensorProductOf(v2, w3)

        "The dimension of the tensor product space should be the multiplication of the two vector spaces" {
            tensorProduct.vectorSpace.dim shouldBe (vectorSpace1.dim * vectorSpace2.dim)
        }
        "Basis of the tensor product" {
            tensorProduct.vectorSpace.getBasis() shouldBe listOf(v1w1, v1w2, v1w3, v2w1, v2w2, v2w3)
        }
        "Tensor product of two vectors" {
            val expected = tensorProduct.vectorSpace.context.run { 2 * v1w1 - v1w2 + 6 * v2w1 - 3 * v2w2 }
            tensorProduct.tensorProductOf(
                vectorSpace1.context.run { v1 + 3 * v2 },
                vectorSpace2.context.run { 2 * w1 - w2 }
            ) shouldBe expected
        }
        "context test" {
            tensorProduct.context.run {
                (v1 tensor w1) shouldBe v1w1
                ((v1 + 3 * v2) tensor (2 * w1 - w2)) shouldBe
                    ((2 * v1 tensor w1) - (v1 tensor w2) + (6 * v2 tensor w1) - (3 * v2 tensor w2))
            }
        }
    }
}

class TensorProductTest : FreeSpec({
    tags(tensorProductTag, bigRationalTag)
    include(tensorProductTest(DenseNumVectorSpaceOverBigRational))
})
