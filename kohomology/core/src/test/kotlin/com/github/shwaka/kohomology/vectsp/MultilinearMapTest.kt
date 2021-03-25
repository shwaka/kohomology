package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.field.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val multilinearMapTag = NamedTag("MultilinearMap")

fun <S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> multilinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val sourceVectorSpace0 = VectorSpace(numVectorSpace, listOf("v", "w"))
    val sourceVectorSpace1 = VectorSpace(numVectorSpace, listOf("x", "y"))
    val targetVectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))

    val (v, w) = sourceVectorSpace0.getBasis()
    val (x, y) = sourceVectorSpace1.getBasis()
    val (a, b) = targetVectorSpace.getBasis()
    targetVectorSpace.withContext {
        "multilinear map test" {
            val vectors = listOf(
                listOf(a, b - a), // v*x, v*y
                listOf(2 * a + b, zeroVector) // w*x, w*y
            )
            val f = MultilinearMap.fromVectors(sourceVectorSpace0, sourceVectorSpace1, targetVectorSpace, matrixSpace, vectors)
            f(v, x) shouldBe a
            f(v, y) shouldBe (b - a)
            f(w, x) shouldBe (2 * a + b)
            f(w, y) shouldBe zeroVector
            val vw = sourceVectorSpace0.withContext { v + w }
            val xy = sourceVectorSpace1.withContext { x + y }
            f(vw, xy) shouldBe (2 * (a + b))
        }
    }
}

class BigRationalMultilinearMapTest : StringSpec({
    tags(multilinearMapTag, bigRationalTag)
    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(multilinearMapTest(matrixSpace))
})
