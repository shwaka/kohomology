package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val liftTag = NamedTag("Lift")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> liftTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "lift test" - {
        val sphereDim = 4
        check(sphereDim % 2 == 0)
        val sphere = sphere(matrixSpace, sphereDim)
        val freePathSpace = FreePathSpace(sphere)
        val projection = freePathSpace.projection
        val (x, y) = sphere.gAlgebra.generatorList
        val (x1, y1, x2, y2, sx, sy) = freePathSpace.gAlgebra.generatorList

        "test findCocycleLift" - {
            "find list of a cocycle" {
                val cocycle = x
                val lift = projection.findCocycleLift(cocycle)
                freePathSpace.context.run {
                    d(lift).isZero().shouldBeTrue()
                    projection(lift) shouldBe cocycle
                }
            }

            "throw IllegalArgumentException when the argument is not a cocycle" {
                val nonCocycle = y
                shouldThrow<IllegalArgumentException> {
                    projection.findCocycleLift(nonCocycle)
                }
            }
        }
    }
}

class LiftTest : FreeSpec({
    tags(liftTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(liftTest(matrixSpace))
})
