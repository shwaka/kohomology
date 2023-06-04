package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.example.sphereWithMultiDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

val freeLoopSpaceTag = NamedTag("FreeLoopSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeLoopSpaceOfEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    "[dim=$sphereDim]" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 == 1)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val indeterminateList = listOf(
            Indeterminate("x", sphereDim),
            Indeterminate("y", sphereDim * 2 - 1)
        )
        val sphere = FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        val (x, y, sx, sy) = freeLoopSpace.generatorList

        freeLoopSpace.context.run {
            "check differential" {
                d(x).isZero().shouldBeTrue()
                d(y) shouldBe (x * x)
                d(sx).isZero().shouldBeTrue()
                d(sy) shouldBe (-2 * x * sx)
            }
            "check cohomology" {
                (0 until sphereDim * 5).forAll { degree ->
                    val expectedDim = when {
                        degree == 0 -> 1
                        ((degree % (sphereDim - 1) == 0) && ((degree / (sphereDim - 1)) % 2 == 1)) -> 1
                        (((degree - 1) % (sphereDim - 1) == 0) && (((degree - 1) / (sphereDim - 1)) % 2 == 1)) -> 1
                        else -> 0
                    }
                    freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
                }
            }
            "check basis of cohomology" {
                (0 until 5).forAll { n ->
                    val degree = (2 * n + 1) * (sphereDim - 1)
                    val basis = listOf(freeLoopSpace.cohomologyClassOf(sx * sy.pow(n)))
                    freeLoopSpace.cohomology.isBasis(basis, degree).shouldBeTrue()
                }
                (0 until 5).forAll { n ->
                    val degree = (2 * n + 1) * (sphereDim - 1) + 1
                    val basis = listOf(
                        freeLoopSpace.cohomologyClassOf(
                            if (n == 0)
                                x
                            else
                                2 * n * y * sx * sy.pow(n - 1) + x * sy.pow(n)
                        )
                    )
                    freeLoopSpace.cohomology.isBasis(basis, degree).shouldBeTrue()
                }
            }
            "check suspension" {
                val s = freeLoopSpace.suspension
                s(x) shouldBe sx
                s(y) shouldBe sy
                s(sx).isZero().shouldBeTrue()
                s(sy).isZero().shouldBeTrue()
                s(x.pow(2)) shouldBe (2 * x * sx)
                s(x * sx).isZero().shouldBeTrue()
                s(x * y) shouldBe (sx * y + x * sy)
            }
            "check suspension on cohomology" {
                val hs = freeLoopSpace.suspension.inducedMapOnCohomology
                hs(x.cohomologyClass()) shouldBe (sx.cohomologyClass())
                hs(sx.cohomologyClass()).isZero().shouldBeTrue()
            }
            "freeLoopSpace.shiftDegree should be 1" {
                val shiftDegree: Int = freeLoopSpace.shiftDegree.value
                shiftDegree shouldBe 1
            }
            "check freeLoopSpace.getDegree" {
                freeLoopSpace.getDegree(sphereDim, 0) shouldBe x.degree
                freeLoopSpace.getDegree(2 * sphereDim - 1, 0) shouldBe y.degree
                freeLoopSpace.getDegree(sphereDim - 1, 1) shouldBe sx.degree
                freeLoopSpace.getDegree(2 * sphereDim - 2, 1) shouldBe sy.degree
            }
            "freeLoopSpace.baseSpace should be the same as the original freeDGAlgebra" {
                freeLoopSpace.baseSpace shouldBeSameInstanceAs sphere
            }
            "quotient by ideal respecting formality of even sphere" - {
                val ideal = freeLoopSpace.getDGIdeal(
                    listOf(x.pow(2), y)
                )
                val subQuotDGAlgebra = freeLoopSpace.getQuotientByIdeal(ideal)
                "check dimension of cohomology" {
                    (0 until 10 * sphereDim).forAll { n ->
                        subQuotDGAlgebra.cohomology[n].dim shouldBe freeLoopSpace.cohomology[n].dim
                    }
                }
                "projection should be quasi-isomorphism" {
                    val proj = subQuotDGAlgebra.projection
                    (0 until 10 * sphereDim).forAll { n ->
                        proj.inducedMapOnCohomology[n].isIsomorphism().shouldBeTrue()
                    }
                }
            }
            "getDGIdeal should thrown IllegalArgumentException when the ideal is not closed under d" {
                shouldThrow<IllegalArgumentException> {
                    freeLoopSpace.getDGIdeal(
                        listOf(y) // dy = x^2 ∉ I
                    )
                }
            }
            "plain printer test for FreeLoopSpace" - {
                "with toString()" {
                    freeLoopSpace.toString() shouldBe "(Λ(x, y, sx, sy), d)"
                    sx.toString() shouldBe "sx"
                }
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.PLAIN, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ(x, y, _x, _y), d)"
                    printerBar(sx) shouldBe "_x"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ(x, y, sx, sy), d)"
                    printerS(sx) shouldBe "sx"
                }
                "with ShowShift.S_WITH_DEGREE" {
                    val printerSWithDeg = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ(x, y, sx, sy), d)"
                    printerSWithDeg(sx) shouldBe "sx"
                }
            }
            "tex printer test for FreeLoopSpace" - {
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ({x}, {y}, \\bar{x}, \\bar{y}), d)"
                    printerBar(sx) shouldBe "\\bar{x}"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.TEX, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ({x}, {y}, s{x}, s{y}), d)"
                    printerS(sx) shouldBe "s{x}"
                }
                "with ShowShift.S_WITH_DEGREE" {
                    val printerSWithDeg = Printer(printType = PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ({x}, {y}, s{x}, s{y}), d)"
                    printerSWithDeg(sx) shouldBe "s{x}"
                }
            }
            "parse test".config(tags = setOf(parseTag)) {
                freeLoopSpace.parse("x") shouldBe x
                freeLoopSpace.parse("sx") shouldBe sx
                freeLoopSpace.parse("y") shouldBe y
                freeLoopSpace.parse("sy") shouldBe sy
            }
        }
    }
}

suspend inline fun <D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.freeLoopSpaceWithShiftDegreeTestTemplate(
    name: String,
    freeDGAlgebra: FreeDGAlgebra<D, StringIndeterminateName, S, V, M>,
    maxDegree: Int,
) {
    "FreeLoopSpace.withShiftDegree for $name" - {
        val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
        val freeLoopSpaceWithShiftDegree = FreeLoopSpace.withShiftDegree(freeDGAlgebra)
        "dimension of the cohomology with / without shiftDegree should be the same" {
            (0..maxDegree).forAll { degree ->
                freeLoopSpaceWithShiftDegree.cohomology.getBasisForAugmentedDegree(degree).size shouldBe
                    freeLoopSpace.cohomology.getBasisForAugmentedDegree(degree).size
            }
        }
        "the augmentation of shiftDegree should be 1" {
            val shiftDegree: MultiDegree = freeLoopSpaceWithShiftDegree.shiftDegree
            freeLoopSpaceWithShiftDegree.degreeGroup.augmentation(shiftDegree) shouldBe 1
        }
        "freeLoopSpace.baseSpace should be the same instance as the original freeDGAlgebra" {
            freeLoopSpace.baseSpace shouldBeSameInstanceAs freeDGAlgebra
        }
        "freeLoopSpaceWithShiftDegree.baseSpace should be a different instance from the original freeDGAlgebra" {
            freeLoopSpaceWithShiftDegree.baseSpace shouldNotBeSameInstanceAs freeDGAlgebra
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeLoopSpaceWithShiftDegreeTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "FreeLoopSpace.withShiftDegree" - {
        freeLoopSpaceWithShiftDegreeTestTemplate("even sphere (IntDegree)", sphere(matrixSpace, 2), 20)
        freeLoopSpaceWithShiftDegreeTestTemplate("odd sphere (IntDegree)", sphere(matrixSpace, 3), 20)
        freeLoopSpaceWithShiftDegreeTestTemplate("even sphere (MultiDegree)", sphereWithMultiDegree(matrixSpace, 2), 20)
        freeLoopSpaceWithShiftDegreeTestTemplate("odd sphere (MultiDegree)", sphereWithMultiDegree(matrixSpace, 3), 20)

        "test for FreeLoopSpace.withShiftDegree(sphere)" - {
            val sphereDim = 2
            val freeLoopSpace = FreeLoopSpace.withShiftDegree(sphere(matrixSpace, sphereDim))
            val (x, y, sx, sy) = freeLoopSpace.generatorList

            "check freeLoopSpace.getDegree" {
                freeLoopSpace.getDegree(sphereDim, 0) shouldBe x.degree
                freeLoopSpace.getDegree(2 * sphereDim - 1, 0) shouldBe y.degree
                freeLoopSpace.getDegree(sphereDim - 1, 1) shouldBe sx.degree
                freeLoopSpace.getDegree(2 * sphereDim - 2, 1) shouldBe sy.degree
            }

            "plain printer test for FreeLoopSpace.withShiftDegree" - {
                "with toString()" {
                    freeLoopSpace.toString() shouldBe "(Λ(x, y, s^{1 + -2S}x, s^{1 + -2S}y), d)"
                    sx.toString() shouldBe "s^{1 + -2S}x"
                }
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.PLAIN, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ(x, y, _x, _y), d)"
                    printerBar(sx) shouldBe "_x"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ(x, y, sx, sy), d)"
                    printerS(sx) shouldBe "sx"
                }
                "with ShowShift.S_WITH_DEGREE" {
                    val printerSWithDeg = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ(x, y, s^{1 + -2S}x, s^{1 + -2S}y), d)"
                    printerSWithDeg(sx) shouldBe "s^{1 + -2S}x"
                }
            }
            "tex printer test for FreeLoopSpace.withShiftDegree" - {
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ({x}, {y}, \\bar{x}, \\bar{y}), d)"
                    printerBar(sx) shouldBe "\\bar{x}"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.TEX, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ({x}, {y}, s{x}, s{y}), d)"
                    printerS(sx) shouldBe "s{x}"
                }
                "with ShowShift.S_WITH_DEGREE" {
                    val printerSWithDeg = Printer(printType = PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ({x}, {y}, s^{1 + -2S}{x}, s^{1 + -2S}{y}), d)"
                    printerSWithDeg(sx) shouldBe "s^{1 + -2S}{x}"
                }
            }
            "parse test".config(tags = setOf(parseTag)) {
                freeLoopSpace.parse("x") shouldBe x
                freeLoopSpace.parse("s_1_m2x") shouldBe sx
                freeLoopSpace.parse("y") shouldBe y
                freeLoopSpace.parse("s_1_m2y") shouldBe sy
            }
        }
    }
}

class FreeLoopSpaceTest : FreeSpec({
    tags(freeLoopSpaceTag, rationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 2))
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 4))
    include(freeLoopSpaceWithShiftDegreeTest(matrixSpace))
})

class FreeLoopSpaceTestWithDecomposedSparseMatrixSpace : FreeSpec({
    tags(freeLoopSpaceTag, rationalTag)

    val matrixSpace = DecomposedSparseMatrixSpaceOverRational
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 2))
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 4))
    include(freeLoopSpaceWithShiftDegreeTest(matrixSpace))
})
