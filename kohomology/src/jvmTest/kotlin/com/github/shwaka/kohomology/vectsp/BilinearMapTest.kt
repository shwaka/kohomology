package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val bilinearMapTag = NamedTag("BilinearMap")

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
            "test zero" {
                val f = BilinearMap.getZero(sourceVectorSpace1, sourceVectorSpace2, targetVectorSpace, matrixSpace)
                f(v, x).isZero().shouldBeTrue()
            }

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
        }
    }
}

typealias BilinearMapConstructor<BS1, BS2, BT, S, V, M> = (
    VectorSpace<BS1, S, V>,
    VectorSpace<BS2, S, V>,
    VectorSpace<BT, S, V>,
    MatrixSpace<S, V, M>,
    (BS1, BS2) -> Vector<BT, S, V>,
) -> BilinearMap<BS1, BS2, BT, S, V, M>

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> bilinearMapImplTest(
    matrixSpace: MatrixSpace<S, V, M>,
    implName: String,
    constructor: BilinearMapConstructor<StringBasisName, StringBasisName, StringBasisName, S, V, M>,
) = freeSpec {
    val numVectorSpace = matrixSpace.numVectorSpace
    val sourceVectorSpace1 = VectorSpace(numVectorSpace, listOf("v", "w"))
    val sourceVectorSpace2 = VectorSpace(numVectorSpace, listOf("x", "y"))
    val targetVectorSpace = VectorSpace(numVectorSpace, listOf("a", "b"))
    val context = MultipleVectorContext(numVectorSpace, listOf(sourceVectorSpace1, sourceVectorSpace2, targetVectorSpace))

    val (v, w) = sourceVectorSpace1.getBasis()
    val (x, y) = sourceVectorSpace2.getBasis()
    val (a, b) = targetVectorSpace.getBasis()

    context.run {
        "test $implName as parametrized test" - {
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
            "check values" {
                f(v, x) shouldBe a
                f(v, y) shouldBe (b - a)
                f(w, x) shouldBe (2 * a + b)
                f(w, y) shouldBe targetVectorSpace.zeroVector
                f(v + w, x + y) shouldBe (2 * (a + b))
            }
            "test bilinearMap.induce for QuotVectorSpace" {
                val source1QuotVectorSpace = QuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = sourceVectorSpace1,
                    quotientGenerator = listOf(),
                )
                val source2QuotVectorSpace = QuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = sourceVectorSpace2,
                    quotientGenerator = listOf(y),
                )
                val targetQuotVectorSpace = QuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = targetVectorSpace,
                    quotientGenerator = listOf(a - b),
                )
                val g = f.induce(
                    source1QuotVectorSpace,
                    source2QuotVectorSpace,
                    targetQuotVectorSpace,
                )

                val p1 = source1QuotVectorSpace.projection
                val p2 = source2QuotVectorSpace.projection
                val q = targetQuotVectorSpace.projection
                g(p1(v), p2(x)) shouldBe q(a)
                g(p1(v), p2(x)) shouldBe q(b) // [a] = [b] in targetQuotVectorSpace
                g(p1(w), p2(x)) shouldBe q(3 * a)
                g(p1(v), p2(y)).isZero().shouldBeTrue()
                g(p1(w), p2(y)).isZero().shouldBeTrue()
            }
            "test bilinearMap.induce for SubQuotVectorSpace" {
                val source1SubQuotVectorSpace = SubQuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = sourceVectorSpace1,
                    subspaceGenerator = listOf(v, w),
                    quotientGenerator = listOf(),
                )
                val source2SubQuotVectorSpace = SubQuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = sourceVectorSpace2,
                    subspaceGenerator = listOf(x, y),
                    quotientGenerator = listOf(y),
                )
                val targetSubQuotVectorSpace = SubQuotVectorSpace(
                    matrixSpace,
                    totalVectorSpace = targetVectorSpace,
                    subspaceGenerator = listOf(a, b),
                    quotientGenerator = listOf(a - b),
                )
                val g = f.induce(
                    source1SubQuotVectorSpace,
                    source2SubQuotVectorSpace,
                    targetSubQuotVectorSpace,
                )

                val p1 = source1SubQuotVectorSpace.projection
                val p2 = source2SubQuotVectorSpace.projection
                val q = targetSubQuotVectorSpace.projection
                g(p1(v), p2(x)) shouldBe q(a)
                g(p1(v), p2(x)) shouldBe q(b) // [a] = [b] in targetSubQuotVectorSpace
                g(p1(w), p2(x)) shouldBe q(3 * a)
                g(p1(v), p2(y)).isZero().shouldBeTrue()
                g(p1(w), p2(y)).isZero().shouldBeTrue()
            }
            "f.image() should be the whole of targetVectorSpace" {
                f.image().dim shouldBe targetVectorSpace.dim
            }
            "test f.image(source1Sub)" {
                val image = f.image(
                    source2Sub = SubVectorSpace(matrixSpace, sourceVectorSpace2, listOf(y)),
                )
                image.dim shouldBe 1
                image.subspaceContains(b - a).shouldBeTrue()
            }
            "test f.image(source1Sub, source2Sub)" {
                val image = f.image(
                    source1Sub = SubVectorSpace(matrixSpace, sourceVectorSpace1, listOf(v)),
                    source2Sub = SubVectorSpace(matrixSpace, sourceVectorSpace2, listOf(x + y)),
                )
                image.dim shouldBe 1
                image.subspaceContains(b).shouldBeTrue()
            }
        }
    }
}

class RationalBilinearMapTest : FreeSpec({
    tags(bilinearMapTag, rationalTag)
    val matrixSpace = DenseMatrixSpaceOverRational
    include(bilinearMapTest(matrixSpace))
    include(bilinearMapImplTest(matrixSpace, "ValueBilinearMap", ::ValueBilinearMap))
    include(bilinearMapImplTest(matrixSpace, "LazyBilinearMap", ::LazyBilinearMap))
})
