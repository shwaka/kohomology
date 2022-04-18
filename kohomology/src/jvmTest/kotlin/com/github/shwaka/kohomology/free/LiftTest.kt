package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
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
        val target = sphere(matrixSpace, sphereDim)
        val source = FreePathSpace(target)
        val surjectiveQuasiIsomorphism = source.projection
        val (x, y) = target.gAlgebra.generatorList
        val (x1, y1, x2, y2, _, _) = source.gAlgebra.generatorList

        "lift DGVector along DGLinearMap" - {
            "findCocycleLift" - {
                "should return a lift of a cocycle" {
                    val lift = surjectiveQuasiIsomorphism.findCocycleLift(x)
                    surjectiveQuasiIsomorphism(lift) shouldBe x
                    source.context.run {
                        d(lift).isZero().shouldBeTrue()
                    }
                }

                "should throw IllegalArgumentException when the argument is not a cocycle" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        surjectiveQuasiIsomorphism.findCocycleLift(y) // y is not a cocycle
                    }
                    exception.message.shouldContain("not a cocycle")
                }
            }

            "findLift" - {
                "should return a lift of a cochain when the argument is valid" {
                    val sourceCocycle = source.context.run {
                        x1 * x2
                    }
                    val lift = surjectiveQuasiIsomorphism.findLift(y, sourceCocycle)
                    surjectiveQuasiIsomorphism(lift) shouldBe y
                    source.context.run {
                        d(lift) shouldBe sourceCocycle
                    }
                }

                "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        surjectiveQuasiIsomorphism.findLift(x, x1)
                    }
                    exception.message.shouldContain("should be equal to deg(")
                }

                "should throw IllegalArgumentException when sourceCocycle is not a cocycle" {
                    val sourceCochain = source.context.run {
                        y1 - y2
                    }
                    val zero = target.gAlgebra.getZero(sourceCochain.degree.value - 1)
                    val exception = shouldThrow<IllegalArgumentException> {
                        surjectiveQuasiIsomorphism.findLift(zero, sourceCochain)
                    }
                    exception.message.shouldContain("not a cocycle")
                }

                "should throw IllegalArgumentException when the condition f(sourceCocycle)=d(targetCochain) is not satisfied" {
                    val targetCochain = target.context.run {
                        2 * y
                    }
                    val sourceCocycle = source.context.run {
                        x1 * x2
                    }
                    val exception = shouldThrow<IllegalArgumentException> {
                        surjectiveQuasiIsomorphism.findLift(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("are not compatible")
                    exception.message.shouldContain("must be equal to d(")
                }
            }

            "findLiftUpToHomotopy" - {
                "should throw IllegalArgumentException when sourceCocycle is not a cocycle" {
                    // This can't be done in the following test for non-surjective quasi-isomorphism
                    val exception = shouldThrow<IllegalArgumentException> {
                        surjectiveQuasiIsomorphism.findCocycleLiftUpToHomotopy(y) // y is not a cocycle
                    }
                    exception.message.shouldContain("not a cocycle")
                }
            }
        }

        "lift DGAlgebraMap from FreeDGAlgebra" - {
            "findLift" - {
                "should return a lift" {
                    val underlyingMap = target.context.run {
                        val valueList = listOf(2 * x, 4 * y)
                        target.getDGAlgebraMap(target, valueList)
                    }
                    val lift = target.findLift(underlyingMap, surjectiveQuasiIsomorphism)
                    target.gAlgebra.generatorList.forAll { v ->
                        (surjectiveQuasiIsomorphism * lift)(v) shouldBe underlyingMap(v)
                    }
                }
            }
            "findSection" - {
                "should return a lift" {
                    val section = target.findSection(surjectiveQuasiIsomorphism)
                    target.gAlgebra.generatorList.forAll { v ->
                        (surjectiveQuasiIsomorphism * section)(v) shouldBe v
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
        val quasiIsomorphism = target.context.run {
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
                        quasiIsomorphism.findCocycleLift(x1)
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
                        quasiIsomorphism.findLift(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("is not surjective")
                }
            }

            "findCocycleLiftUpToHomotopy" - {
                "should return a homotopy lift of a cocycle" {
                    val liftWithBoundingCochain = quasiIsomorphism.findCocycleLiftUpToHomotopy(x1)
                    val lift = liftWithBoundingCochain.lift
                    val boundingCochain = liftWithBoundingCochain.boundingCochain
                    source.context.run {
                        d(lift).isZero().shouldBeTrue()
                    }
                    target.context.run {
                        (quasiIsomorphism(lift) - x1) shouldBe d(boundingCochain)
                        quasiIsomorphism(lift).cohomologyClass() shouldBe x1.cohomologyClass()
                    }
                }

                "should throw IllegalArgumentException when the argument is not a cocycle" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        quasiIsomorphism.findCocycleLiftUpToHomotopy(y1) // y1 is not a cocycle
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
                    val liftWithBoundingCochain = quasiIsomorphism.findLiftUpToHomotopy(targetCochain, sourceCocycle)
                    val lift = liftWithBoundingCochain.lift
                    val boundingCochain = liftWithBoundingCochain.boundingCochain
                    source.context.run {
                        d(lift) shouldBe sourceCocycle
                    }
                    target.context.run {
                        (quasiIsomorphism(lift) - targetCochain) shouldBe d(boundingCochain)
                        // The following condition is unnecessary, but is expected to make the test appropriate.
                        (quasiIsomorphism(lift) - targetCochain).isNotZero().shouldBeTrue()
                    }
                }

                "should throw IllegalArgumentException when the degrees of the arguments are incompatible" {
                    val exception = shouldThrow<IllegalArgumentException> {
                        quasiIsomorphism.findLiftUpToHomotopy(x1, x)
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
                        quasiIsomorphism.findLiftUpToHomotopy(targetCochain, sourceCocycle)
                    }
                    exception.message.shouldContain("are not compatible")
                    exception.message.shouldContain("must be equal to d(")
                }
            }
        }

        "lift DGAlgebraMap from FreeDGAlgebra" - {
            "findLiftUpToHomotopy" - {
                "should return a homotopy lift" {
                    val underlyingMap = source.getDGAlgebraMap(target, listOf(x1, y1))
                    // test lift on cohomology
                    val liftWithHomotopy = source.findLiftUpToHomotopy(underlyingMap, quasiIsomorphism)
                    val lift = liftWithHomotopy.lift
                    (0..(2 * sphereDim)).forAll { degree ->
                        (quasiIsomorphism * lift).inducedMapOnCohomology()[degree] shouldBe
                            underlyingMap.inducedMapOnCohomology()[degree]
                    }
                    // test homotopy
                    val homotopy = liftWithHomotopy.homotopy
                    val freePathSpace = liftWithHomotopy.freePathSpace
                    val inclusion1 = freePathSpace.inclusion1
                    val inclusion2 = freePathSpace.inclusion2
                    source.gAlgebra.generatorList.forAll { v ->
                        homotopy(inclusion1(v)) shouldBe underlyingMap(v)
                        homotopy(inclusion2(v)) shouldBe quasiIsomorphism(lift(v))
                    }
                }
            }
            "findSectionUpToHomotopy" - {
                "should return a homotopy lift" {
                    val liftWithHomotopy = target.findSectionUpToHomotopy(quasiIsomorphism)
                    // test section on cohomology
                    val section = liftWithHomotopy.lift
                    (0..(2 * sphereDim)).forAll { degree ->
                        (quasiIsomorphism * section).inducedMapOnCohomology()[degree].isIdentity().shouldBeTrue()
                    }
                    // test homotopy
                    val homotopy = liftWithHomotopy.homotopy
                    val freePathSpace = liftWithHomotopy.freePathSpace
                    val inclusion1 = freePathSpace.inclusion1
                    val inclusion2 = freePathSpace.inclusion2
                    target.gAlgebra.generatorList.forAll { v ->
                        homotopy(inclusion1(v)) shouldBe v
                        homotopy(inclusion2(v)) shouldBe quasiIsomorphism(section(v))
                    }
                }
            }
        }
    }

    "test with a surjective quasi-injection" - {
        val n = 4
        check(n % 2 == 0)
        val source = run {
            val indeterminateList = listOf(
                Indeterminate("dt", n + 1),
                Indeterminate("t", n),
                Indeterminate("du", 2 * n),
                Indeterminate("u", 2 * n - 1),
                Indeterminate("dv", 2 * n),
                Indeterminate("v", 2 * n - 1),
            )
            FreeDGAlgebra(matrixSpace, indeterminateList) { (dt, _, du, _, dv, _) ->
                listOf(zeroGVector, dt, zeroGVector, du, zeroGVector, dv)
            }
        }
        val (_, _, du, _, _, _) = source.gAlgebra.generatorList
        val target = run {
            val indeterminateList = listOf(
                Indeterminate("x", n),
                Indeterminate("y", 2 * n - 1),
                Indeterminate("z", 2 * n - 1),
            )
            FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _, _) ->
                listOf(zeroGVector, x.pow(2), zeroGVector)
            }
        }
        val (x, y, z) = target.gAlgebra.generatorList
        val quasiInjection = target.context.run {
            val valueList = listOf(zeroGVector, x, x.pow(2), y, zeroGVector, z)
            source.getDGAlgebraMap(target, valueList)
        }

        "lift DGVector along DGLinearMap" - {
            "findCocycleLift" - {
                "should throw UnsupportedOperationException" {
                    val exception = shouldThrow<UnsupportedOperationException> {
                        quasiInjection.findCocycleLift(x)
                    }
                    exception.message.shouldContain("H^")
                    exception.message.shouldContain("is not surjective")
                }
            }

            "findLift" - {
                "should throw UnsupportedOperationException" {
                    val targetCochain = target.context.run {
                        y + z
                    }
                    val exception = shouldThrow<UnsupportedOperationException> {
                        quasiInjection.findLift(targetCochain, du)
                    }
                    exception.message.shouldContain("H^")
                    exception.message.shouldContain("is not surjective")
                }
            }
        }
    }

    "test with a surjective quasi-surjection" - {
        val n = 4
        check(n % 2 == 0)
        val source = run {
            val indeterminateList = listOf(
                Indeterminate("t", n),
                Indeterminate("u", 2 * n - 1),
                Indeterminate("v", 2 * n),
            )
            FreeDGAlgebra(matrixSpace, indeterminateList) { (t, _, _) ->
                listOf(zeroGVector, t.pow(2), zeroGVector)
            }
        }
        val (t, _, v) = source.gAlgebra.generatorList
        val target = sphere(matrixSpace, n)
        val (x, y) = target.gAlgebra.generatorList
        val quasiSurjection = target.context.run {
            val valueList = listOf(x, y, zeroGVector)
            source.getDGAlgebraMap(target, valueList)
        }

        "lift DGVector along DGLinearMap" - {
            "findLift" - {
                "should throw UnsupportedOperationException" {
                    val exception = shouldThrow<UnsupportedOperationException> {
                        val sourceCocycle = source.context.run {
                            t.pow(2) + v
                        }
                        quasiSurjection.findLift(y, sourceCocycle)
                    }
                    exception.message.shouldContain("H^")
                    exception.message.shouldContain("is not injective")
                }
            }
        }
    }
}

class LiftTest : FreeSpec({
    tags(liftTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(liftTest(matrixSpace))
})
