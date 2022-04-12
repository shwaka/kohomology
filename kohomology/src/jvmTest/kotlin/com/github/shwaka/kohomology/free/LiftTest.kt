package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
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
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

val liftTag = NamedTag("Lift")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> liftTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test with a surjective quasi-isomorphism" - {
        val sphereDim = 4
        check(sphereDim % 2 == 0)
        val source = sphere(matrixSpace, sphereDim)
        val target = FreePathSpace(source)
        val projection = target.projection
        val (x, y) = source.gAlgebra.generatorList
        val (x1, y1, x2, y2, _, _) = target.gAlgebra.generatorList

        "lift DGVector along DGLinearMap" - {
            "findCocycleLift" - {
                "should return a lift of a cocycle" {
                    val lift = projection.findCocycleLift(x)
                    projection(lift) shouldBe x
                    target.context.run {
                        d(lift).isZero().shouldBeTrue()
                    }
                }

                "should throw IllegalArgumentException when the argument is not a cocycle" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        projection.findCocycleLift(y) // y is not a cocycle
                    }
                    exception.message.shouldContain("not a cocycle")
                }
            }

            "findLift" - {
                "should return a lift of a cochain when the argument is valid" {
                    val sourceCocycle = target.context.run {
                        x1 * x2
                    }
                    val lift = projection.findLift(y, sourceCocycle)
                    projection(lift) shouldBe y
                    target.context.run {
                        d(lift) shouldBe sourceCocycle
                    }
                }

                "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        projection.findLift(x, x1)
                    }
                    exception.message.shouldContain("should be equal to deg(")
                }

                "should throw IllegalArgumentException when sourceCocycle is not a cocycle" {
                    val sourceCochain = target.context.run {
                        y1 - y2
                    }
                    val zero = source.gAlgebra.getZero(sourceCochain.degree.value - 1)
                    val exception = shouldThrow<IllegalArgumentException> {
                        projection.findLift(zero, sourceCochain)
                    }
                    exception.message.shouldContain("not a cocycle")
                }

                "should throw IllegalArgumentException when the condition f(sourceCocycle)=d(targetCochain) is not satisfied" {
                    val targetCochain = source.context.run {
                        2 * y
                    }
                    val sourceCocycle = target.context.run {
                        x1 * x2
                    }
                    val exception = shouldThrow<IllegalArgumentException> {
                        projection.findLift(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("are not compatible")
                    exception.message.shouldContain("must be equal to d(")
                }
            }

            "findLiftUpToHomotopy" - {
                "should throw IllegalArgumentException when sourceCocycle is not a cocycle" {
                    // This can't be done in the following test for non-surjective quasi-isomorphism
                    val exception = shouldThrow<IllegalArgumentException> {
                        projection.findCocycleLiftUpToHomotopy(y) // y is not a cocycle
                    }
                    exception.message.shouldContain("not a cocycle")
                }
            }
        }

        "lift DGAlgebraMap from FreeDGAlgebra" - {
            "findSection" - {
                "should return a lift" {
                    val section = source.findSection(projection)
                    (0..(2 * sphereDim)).forAll { degree ->
                        (projection * section).gLinearMap[degree].isIdentity().shouldBeTrue()
                    }
                    source.gAlgebra.generatorList.forAll { v ->
                        (projection * section)(v) shouldBe v
                    }
                }
            }
        }
    }

    "test with a non-surjective quasi-isomorphism" - {
        val sphereDim = 4
        check(sphereDim % 2 == 0)
        val source = sphere(matrixSpace, sphereDim)
        val target = FreePathSpace(source)
        val (x, _) = source.gAlgebra.generatorList
        val (x1, y1, x2, y2, sx, _) = target.gAlgebra.generatorList
        val f = target.context.run {
            val valueList = listOf(
                x1 + x2,
                2 * (y1 + y2) - (x2 - x1) * sx
            )
            source.getDGAlgebraMap(target, valueList)
        }

        "lift DGVector along DGLinearMap" - {
            "findCocycleLift" - {
                "should throw UnsupportedOperationException since non-surjective" {
                    val exception = shouldThrow<UnsupportedOperationException> {
                        f.findCocycleLift(x1)
                    }
                    exception.message.shouldContain("is not surjective")
                }
            }

            "findLift" - {
                "should throw UnsupportedOperationException since non-surjective" {
                    val sourceCocycle = source.context.run { x.pow(2) }
                    val targetCochain = target.context.run {
                        3 * y1 + y2 + 2 * x1 * sx
                    }
                    val exception = shouldThrow<UnsupportedOperationException> {
                        f.findLift(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("is not surjective")
                }
            }

            "findCocycleLiftUpToHomotopy" - {
                "should return a homotopy lift of a cocycle" {
                    val liftWithBoundingCochain = f.findCocycleLiftUpToHomotopy(x1)
                    val lift = liftWithBoundingCochain.lift
                    val boundingCochain = liftWithBoundingCochain.boundingCochain
                    source.context.run {
                        d(lift).isZero().shouldBeTrue()
                    }
                    target.context.run {
                        (f(lift) - x1) shouldBe d(boundingCochain)
                        f(lift).cohomologyClass() shouldBe x1.cohomologyClass()
                    }
                }

                "should throw IllegalArgumentException when the argument is not a cocycle" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        f.findCocycleLiftUpToHomotopy(y1) // y1 is not a cocycle
                    }
                    exception.message.shouldContain("not a cocycle")
                }
            }

            "findLiftUpToHomotopy" - {
                "should return a homotopy lift of a cochain when the argument is valid" {
                    val sourceCocycle = source.context.run { x.pow(2) }
                    val targetCochain = target.context.run {
                        3 * y1 + y2 + 2 * x1 * sx
                    }
                    val liftWithBoundingCochain = f.findLiftUpToHomotopy(targetCochain, sourceCocycle)
                    val lift = liftWithBoundingCochain.lift
                    val boundingCochain = liftWithBoundingCochain.boundingCochain
                    source.context.run {
                        d(lift) shouldBe sourceCocycle
                    }
                    target.context.run {
                        (f(lift) - targetCochain) shouldBe d(boundingCochain)
                        // The following condition is unnecessary, but is expected to make the test appropriate.
                        (f(lift) - targetCochain).isNotZero().shouldBeTrue()
                    }
                }

                "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        f.findLiftUpToHomotopy(x1, x)
                    }
                    exception.message.shouldContain("should be equal to deg(")
                }

                // There is no pair (targetCochain, sourceCochain) such that
                //   f(sourceCochain) = d(targetCochain) but d(sourceCochain) != 0
                // "should throw IllegalArgumentException when sourceCocycle is not a cocycle" { }

                "should throw IllegalArgumentException when the condition f(sourceCocycle)=d(targetCochain) is not satisfied" {
                    val sourceCocycle = source.context.run { 2 * x.pow(2) }
                    val targetCochain = target.context.run {
                        3 * y1 + y2 + 2 * x1 * sx
                    }
                    val exception = shouldThrow<IllegalArgumentException> {
                        f.findLiftUpToHomotopy(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("are not compatible")
                    exception.message.shouldContain("must be equal to d(")
                }
            }
        }

        "lift DGAlgebraMap from FreeDGAlgebra" - {
            "findSectionUpToHomotopy" - {
                "should return a homotopy lift" {
                    val liftWithHomotopy = target.findSectionUpToHomotopy(f)
                    val section = liftWithHomotopy.lift
                    val freePathSpace = liftWithHomotopy.freePathSpace
                    (0..(2 * sphereDim)).forAll { degree ->
                        (f * section).inducedMapOnCohomology()[degree].isIdentity().shouldBeTrue()
                    }
                    val homotopy = liftWithHomotopy.homotopy
                    val inclusion1 = freePathSpace.inclusion1
                    val inclusion2 = freePathSpace.inclusion2
                    target.gAlgebra.generatorList.forAll { v ->
                        homotopy(inclusion1(v)) shouldBe v
                        homotopy(inclusion2(v)) shouldBe f(section(v))
                    }
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
