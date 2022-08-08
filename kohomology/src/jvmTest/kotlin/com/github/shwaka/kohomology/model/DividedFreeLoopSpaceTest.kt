package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val dividedFreeLoopSpaceTag = NamedTag("DividedFreeLoopSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dividedFreeLoopSpaceOfEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    "[dim=$sphereDim]" - {
        if (sphereDim % 2 == 1)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val sphere = sphere(matrixSpace, sphereDim)
        val dividedFreeLoopSpace = DividedFreeLoopSpace(sphere)
        val (x1, y1, x2, y2, sx1, sy1, sx2, sy2) = dividedFreeLoopSpace.generatorList
        val freeLoopSpace = dividedFreeLoopSpace.freeLoopSpace
        val (x, y, sx, sy) = freeLoopSpace.generatorList

        "check differential" {
            dividedFreeLoopSpace.context.run {
                d(x1).isZero().shouldBeTrue()
                d(y1) shouldBe (x1 * x1)
                d(sx1) shouldBe (x2 - x1)
                d(sy1) shouldBe (y2 - y1 - x1 * sx1 - x2 * sx1)
                d(sx2) shouldBe (x2 - x1)
                d(sy2) shouldBe (y2 - y1 - x1 * sx2 - x2 * sx2)
            }
        }

        "assert that projections are dga maps" {
            (listOf(dividedFreeLoopSpace.projection1, dividedFreeLoopSpace.projection2)).forAll { projection ->
                (dividedFreeLoopSpace.generatorList).forAll { v ->
                    freeLoopSpace.differential(projection(v)) shouldBe
                        projection(dividedFreeLoopSpace.differential(v))
                }
            }
        }

        "find cocycle lift along projection" {
            val n = 3
            (listOf(dividedFreeLoopSpace.projection1, dividedFreeLoopSpace.projection2)).forAll { projection ->
                val cocycle = freeLoopSpace.context.run {
                    2 * n * y * sx * sy.pow(n - 1) + x * sy.pow(n)
                }
                val lift = projection.findCocycleLift(cocycle)
                dividedFreeLoopSpace.context.run {
                    d(lift).isZero().shouldBeTrue()
                }
                projection(lift) shouldBe cocycle
            }
        }

        "find section" {
            val projection = dividedFreeLoopSpace.projection1
            val section = freeLoopSpace.findSection(projection)
            val d1 = freeLoopSpace.differential
            val d2 = dividedFreeLoopSpace.differential
            (listOf(x, y, sx, sy)).forAll { v ->
                section(d1(v)) shouldBe d2(section(v))
                projection(section(v)) shouldBe v
            }
        }
    }
}

class DividedFreeLoopSpaceTest : FreeSpec({
    tags(dividedFreeLoopSpaceTag, rationalTag)

    include(dividedFreeLoopSpaceOfEvenSphereTest(SparseMatrixSpaceOverRational, 2))
})
