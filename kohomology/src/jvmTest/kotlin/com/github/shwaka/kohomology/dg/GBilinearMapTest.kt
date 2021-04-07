package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val gBilinearMapTag = NamedTag("GBilinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> gBilinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = stringSpec {
    // 1つの元で生成される外積代数
    val numVectorSpace = matrixSpace.numVectorSpace
    val gVectorSpace = GVectorSpace.fromBasisNames(numVectorSpace, "span<u, v>") { degree ->
        when (degree) {
            0 -> listOf("u") // unit
            1 -> listOf("v")
            else -> emptyList()
        }
    }
    val gBilinearMap = GBilinearMap(gVectorSpace, gVectorSpace, gVectorSpace, 0) { p, q ->
        val u: Vector<String, S, V> = gVectorSpace[0].getBasis()[0]
        val v: Vector<String, S, V> = gVectorSpace[1].getBasis()[0]
        val z2: Vector<String, S, V> = gVectorSpace[2].zeroVector
        when (Pair(p, q)) {
            Pair(0, 0) -> BilinearMap.fromVectors(gVectorSpace[0], gVectorSpace[0], gVectorSpace[0], matrixSpace, listOf(listOf(u)))
            Pair(1, 0) -> BilinearMap.fromVectors(gVectorSpace[1], gVectorSpace[0], gVectorSpace[1], matrixSpace, listOf(listOf(v)))
            Pair(0, 1) -> BilinearMap.fromVectors(gVectorSpace[0], gVectorSpace[1], gVectorSpace[1], matrixSpace, listOf(listOf(v)))
            Pair(1, 1) -> BilinearMap.fromVectors(gVectorSpace[1], gVectorSpace[1], gVectorSpace[2], matrixSpace, listOf(listOf(z2)))
            else -> BilinearMap.fromVectors(gVectorSpace[p], gVectorSpace[q], gVectorSpace[p + q], matrixSpace, emptyList())
        }
    }
    gVectorSpace.context.run {
        "graded bilinear map test" {
            val u: GVector<String, S, V> = gVectorSpace.getBasis(0)[0]
            val v: GVector<String, S, V> = gVectorSpace.getBasis(1)[0]
            val z2: GVector<String, S, V> = gVectorSpace.getZero(2)
            val z3: GVector<String, S, V> = gVectorSpace.getZero(3)
            gBilinearMap(u, u) shouldBe u
            gBilinearMap(u, v) shouldBe v
            gBilinearMap(v, u) shouldBe v
            gBilinearMap(v, v) shouldBe gVectorSpace.getZero(2)
            gBilinearMap(z2, v) shouldBe z3
            gBilinearMap(v, z2) shouldBe z3
            gBilinearMap(z3, u) shouldBe z3
            gBilinearMap(u, z3) shouldBe z3
        }
    }
}

class BigRationalGBilinearMapTest : StringSpec({
    tags(gBilinearMapTag, bigRationalTag)

    include(gBilinearMapTest(DenseMatrixSpaceOverBigRational))
})
