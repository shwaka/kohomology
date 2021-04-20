package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val bilinearMapTag = NamedTag("BilinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> bilinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "bilinear map test" {
        val numVectorSpace = matrixSpace.numVectorSpace
        val sourceVectorSpace0 = VectorSpace(numVectorSpace, listOf("v", "w"))
        val sourceVectorSpace1 = VectorSpace(numVectorSpace, listOf("x", "y"))
        val targetVectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))
        val context = MultipleVectorContext(numVectorSpace, listOf(sourceVectorSpace0, sourceVectorSpace1, targetVectorSpace))

        val (v, w) = sourceVectorSpace0.getBasis()
        val (x, y) = sourceVectorSpace1.getBasis()
        val (a, b) = targetVectorSpace.getBasis()
        context.run {
            val vectors = listOf(
                listOf(a, b - a), // v*x, v*y
                listOf(2 * a + b, targetVectorSpace.zeroVector) // w*x, w*y
            )
            val f = BilinearMap(sourceVectorSpace0, sourceVectorSpace1, targetVectorSpace, matrixSpace, vectors)
            f(v, x) shouldBe a
            f(v, y) shouldBe (b - a)
            f(w, x) shouldBe (2 * a + b)
            f(w, y) shouldBe targetVectorSpace.zeroVector
            f(v + w, x + y) shouldBe (2 * (a + b))
        }
    }
}

class BigRationalBilinearMapTest : FreeSpec({
    tags(bilinearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(bilinearMapTest(matrixSpace))
})
