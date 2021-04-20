package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.shouldBe

val gBilinearMapTag = NamedTag("GBilinearMap")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> gBilinearMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    // 1つの元で生成される外積代数
    val numVectorSpace = matrixSpace.numVectorSpace
    val gVectorSpace = GVectorSpace.fromStringBasisNames(numVectorSpace, "span<u, v>") { degree ->
        when (degree) {
            0 -> listOf("u") // unit
            1 -> listOf("v")
            else -> emptyList()
        }
    }
    val gBilinearMap = GBilinearMap(gVectorSpace, gVectorSpace, gVectorSpace, 0, "f") { p, q ->
        val u: Vector<StringBasisName, S, V> = gVectorSpace[0].getBasis()[0]
        val v: Vector<StringBasisName, S, V> = gVectorSpace[1].getBasis()[0]
        val z2: Vector<StringBasisName, S, V> = gVectorSpace[2].zeroVector
        when (Pair(p, q)) {
            Pair(0, 0) -> BilinearMap(gVectorSpace[0], gVectorSpace[0], gVectorSpace[0], matrixSpace, listOf(listOf(u)))
            Pair(1, 0) -> BilinearMap(gVectorSpace[1], gVectorSpace[0], gVectorSpace[1], matrixSpace, listOf(listOf(v)))
            Pair(0, 1) -> BilinearMap(gVectorSpace[0], gVectorSpace[1], gVectorSpace[1], matrixSpace, listOf(listOf(v)))
            Pair(1, 1) -> BilinearMap(gVectorSpace[1], gVectorSpace[1], gVectorSpace[2], matrixSpace, listOf(listOf(z2)))
            else -> BilinearMap(gVectorSpace[p], gVectorSpace[q], gVectorSpace[p + q], matrixSpace, emptyList())
        }
    }
    gVectorSpace.context.run {
        "graded bilinear map test" {
            val u: GVector<StringBasisName, S, V> = gVectorSpace.getBasis(0)[0]
            val v: GVector<StringBasisName, S, V> = gVectorSpace.getBasis(1)[0]
            val z2: GVector<StringBasisName, S, V> = gVectorSpace.getZero(2)
            val z3: GVector<StringBasisName, S, V> = gVectorSpace.getZero(3)
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

class BigRationalGBilinearMapTest : FreeSpec({
    tags(gBilinearMapTag, bigRationalTag)

    include(gBilinearMapTest(DenseMatrixSpaceOverBigRational))
})
