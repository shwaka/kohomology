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
    "test for DGLinearMap with a surjective quasi-isomorphism" - {
        val sphereDim = 4
        check(sphereDim % 2 == 0)
        val sphere = sphere(matrixSpace, sphereDim)
        val freePathSpace = FreePathSpace(sphere)
        val projection = freePathSpace.projection
        val (x, y) = sphere.gAlgebra.generatorList
        val (x1, y1, x2, y2, sx, sy) = freePathSpace.gAlgebra.generatorList

        "findCocycleLift" - {
            "should return a lift of a cocycle" {
                val lift = projection.findCocycleLift(x)
                projection(lift) shouldBe x
                freePathSpace.context.run {
                    d(lift).isZero().shouldBeTrue()
                }
            }

            "should throw IllegalArgumentException when the argument is not a cocycle" {
                shouldThrow<IllegalArgumentException> {
                    projection.findCocycleLift(y) // y is not a cocycle
                }
            }
        }

        "findLift" - {
            "should return a lift of a cochain when the argument is valid" {
                val sourceCocycle = freePathSpace.context.run {
                    x1 * x2
                }
                val lift = projection.findLift(y, sourceCocycle)
                projection(lift) shouldBe y
                freePathSpace.context.run {
                    d(lift) shouldBe sourceCocycle
                }
            }

            "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                shouldThrow<IllegalArgumentException> {
                    projection.findLift(x, x1)
                }
            }

            "should throw IllegalArgumentException when sourceCocycle is not a cocycle" {
                val sourceCochain = freePathSpace.context.run {
                    y1 - y2
                }
                val zero = sphere.gAlgebra.getZero(sourceCochain.degree.value - 1)
                shouldThrow<IllegalArgumentException> {
                    projection.findLift(zero, sourceCochain)
                }
            }

            "should throw IllegalArgumentException when the condition f(sourceCocycle)=d(targetCochain) is not satisfied" {
                val targetCochain = sphere.context.run {
                    2 * y
                }
                val sourceCocycle = freePathSpace.context.run {
                    x1 * x2
                }
                shouldThrow<IllegalArgumentException> {
                    projection.findLift(targetCochain, sourceCocycle)
                }
            }
        }
    }

    "test for DGLinearMap with a non-surjective quasi-isomorphism" - {
        val sphereDim = 4
        check(sphereDim % 2 == 0)
        val sphere = sphere(matrixSpace, sphereDim)
        val freePathSpace = FreePathSpace(sphere)
        val (x, y) = sphere.gAlgebra.generatorList
        val (x1, y1, x2, y2, sx, sy) = freePathSpace.gAlgebra.generatorList
        val f = freePathSpace.context.run {
            val valueList = listOf(
                x1 + x2,
                2 * (y1 + y2) - (x2 - x1) * sx
            )
            sphere.getDGAlgebraMap(freePathSpace, valueList)
        }

        "findCocycleLift" - {
            "should throw UnsupportedOperationException since non-surjective" {
                shouldThrow<UnsupportedOperationException> {
                    f.findCocycleLift(x1)
                }
            }
        }

        "findLift" - {
            "should throw UnsupportedOperationException since non-surjective" {
                val sourceCocycle = sphere.context.run { x.pow(2) }
                val targetCochain = freePathSpace.context.run {
                    3 * y1 + y2 + 2 * x1 * sx
                }
                shouldThrow<UnsupportedOperationException> {
                    f.findLift(targetCochain, sourceCocycle)
                }
            }
        }

        "findCocycleLiftUpToHomotopy" - {
            "should return a homotopy lift of a cocycle" {
                val liftWithHomotopy = f.findCocycleLiftUpToHomotopy(x1)
                val lift = liftWithHomotopy.lift
                val boundingCochain = liftWithHomotopy.boundingCochain
                sphere.context.run {
                    d(lift).isZero().shouldBeTrue()
                }
                freePathSpace.context.run {
                    (f(lift) - x1) shouldBe d(boundingCochain)
                    f(lift).cohomologyClass() shouldBe x1.cohomologyClass()
                }
            }

            "should throw IllegalArgumentException when the argument is not a cocycle" {
                shouldThrow<IllegalArgumentException> {
                    f.findCocycleLiftUpToHomotopy(y1) // y1 is not a cocycle
                }
            }
        }

        "findLiftUpToHomotopy" - {
            "should return a homotopy lift of a cochain when the argument is valid" {
                val sourceCocycle = sphere.context.run { x.pow(2) }
                val targetCochain = freePathSpace.context.run {
                    3 * y1 + y2 + 2 * x1 * sx
                }
                val liftWithHomotopy = f.findLiftUpToHomotopy(targetCochain, sourceCocycle)
                val lift = liftWithHomotopy.lift
                val boundingCochain = liftWithHomotopy.boundingCochain
                sphere.context.run {
                    d(lift) shouldBe sourceCocycle
                }
                freePathSpace.context.run {
                    (f(lift) - targetCochain) shouldBe d(boundingCochain)
                    // The following condition is unnecessary, but is expected to make the test appropriate.
                    (f(lift) - targetCochain).isNotZero().shouldBeTrue()
                }
            }

            "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                shouldThrow<IllegalArgumentException> {
                    f.findLiftUpToHomotopy(x1, x)
                }
            }

            // There is no pair (targetCochain, sourceCochain) such that
            //   f(sourceCochain) = d(targetCochain) but d(sourceCochain) != 0
            // "should throw IllegalArgumentException when sourceCocycle is not a cocycle" { }

            "should throw IllegalArgumentException when the condition f(sourceCocycle)=d(targetCochain) is not satisfied" {
                val sourceCocycle = sphere.context.run { 2 * x.pow(2) }
                val targetCochain = freePathSpace.context.run {
                    3 * y1 + y2 + 2 * x1 * sx
                }
                shouldThrow<IllegalArgumentException> {
                    f.findLiftUpToHomotopy(targetCochain, sourceCocycle)
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
