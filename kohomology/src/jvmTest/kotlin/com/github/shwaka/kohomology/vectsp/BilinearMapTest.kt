package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val bilinearMapTag = NamedTag("BilinearMap")

typealias BilinearMapConstructor<BS1, BS2, BT, S, V, M> = (
    VectorSpace<BS1, S, V>,
    VectorSpace<BS2, S, V>,
    VectorSpace<BT, S, V>,
    MatrixSpace<S, V, M>,
    (BS1, BS2) -> Vector<BT, S, V>,
) -> BilinearMap<BS1, BS2, BT, S, V, M>

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> bilinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test bilinear map" - {
        val numVectorSpace = matrixSpace.numVectorSpace
        val sourceVectorSpace1 = VectorSpace(numVectorSpace, listOf("v", "w"))
        val sourceVectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
        val targetVectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))
        val context = MultipleVectorContext(numVectorSpace, listOf(sourceVectorSpace1, sourceVectorSpace2, targetVectorSpace))

        val (v, w) = sourceVectorSpace1.getBasis()
        val (x, y) = sourceVectorSpace2.getBasis()
        val (a, b) = targetVectorSpace.getBasis()
        context.run {
            "test ValueBilinearMap" {
                val vectors = listOf(
                    listOf(a, b - a), // v*x, v*y
                    listOf(2 * a + b, targetVectorSpace.zeroVector) // w*x, w*y
                )
                val f = ValueBilinearMap(
                    sourceVectorSpace1,
                    sourceVectorSpace2,
                    targetVectorSpace,
                    matrixSpace,
                    vectors
                )
                f(v, x) shouldBe a
                f(v, y) shouldBe (b - a)
                f(w, x) shouldBe (2 * a + b)
                f(w, y) shouldBe targetVectorSpace.zeroVector
                f(v + w, x + y) shouldBe (2 * (a + b))
            }

            val constructors: Map<String, BilinearMapConstructor<StringBasisName, StringBasisName, StringBasisName, S, V, M>> =
                mapOf(
                    "ValueBilinearMap" to ::ValueBilinearMap,
                    "LazyBilinearMap" to ::LazyBilinearMap,
                )
            for ((name, constructor) in constructors) {
                "test constructor of $name with getValue" {
                    val f = constructor(
                        sourceVectorSpace1,
                        sourceVectorSpace2,
                        targetVectorSpace,
                        matrixSpace
                    ) { s, t ->
                        when (s.name) {
                            "v" -> when (t.name) {
                                "x" -> a
                                "y" -> b - a
                                else -> throw Exception("This can't happen!")
                            }
                            "w" -> when (t.name) {
                                "x" -> 2 * a + b
                                "y" -> targetVectorSpace.zeroVector
                                else -> throw Exception("This can't happen!")
                            }
                            else -> throw Exception("This can't happen!")
                        }
                    }
                    f(v, x) shouldBe a
                    f(v, y) shouldBe (b - a)
                    f(w, x) shouldBe (2 * a + b)
                    f(w, y) shouldBe targetVectorSpace.zeroVector
                    f(v + w, x + y) shouldBe (2 * (a + b))
                }
            }
        }
    }
}

class RationalBilinearMapTest : FreeSpec({
    tags(bilinearMapTag, rationalTag)
    val matrixSpace = DenseMatrixSpaceOverRational
    include(bilinearMapTest(matrixSpace))
})
