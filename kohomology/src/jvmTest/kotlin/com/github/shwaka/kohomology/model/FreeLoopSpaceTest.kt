package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.bigRationalTag
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
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
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
        val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

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
                val hs = freeLoopSpace.suspension.inducedMapOnCohomology()
                hs(x.cohomologyClass()) shouldBe (sx.cohomologyClass())
                hs(sx.cohomologyClass()).isZero().shouldBeTrue()
            }
            "freeLoopSpace.shiftDegree should be 1" {
                val shiftDegree: Int = freeLoopSpace.shiftDegree.value
                shiftDegree shouldBe 1
            }
            "freeLoopSpace.baseSpace should be the same as the original freeDGAlgebra" {
                freeLoopSpace.baseSpace shouldBeSameInstanceAs sphere
            }
            "plain printer test for FreeLoopSpace" - {
                "with toString()" {
                    freeLoopSpace.toString() shouldBe "(Λ(x, y, sx, sy), d)"
                }
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.PLAIN, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ(x, y, _x, _y), d)"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ(x, y, sx, sy), d)"
                }
                "with ShowShift.S_WITH_DEGREE" {
                    val printerSWithDeg = Printer(printType = PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ(x, y, sx, sy), d)"
                }
            }
            "tex printer test for FreeLoopSpace" - {
                "with toString()" {
                    freeLoopSpace.toString() shouldBe "(Λ(x, y, sx, sy), d)"
                }
                "with ShowShift.BAR" {
                    val printerBar = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
                    printerBar(freeLoopSpace) shouldBe "(Λ({x}, {y}, \\bar{x}, \\bar{y}), d)"
                }
                "with ShowShift.S" {
                    val printerS = Printer(printType = PrintType.TEX, showShift = ShowShift.S)
                    printerS(freeLoopSpace) shouldBe "(Λ({x}, {y}, s{x}, s{y}), d)"
                }
                "with ShowShift.S_WITH_DEGERE" {
                    val printerSWithDeg = Printer(printType = PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
                    printerSWithDeg(freeLoopSpace) shouldBe "(Λ({x}, {y}, s{x}, s{y}), d)"
                }
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
    }
}

class FreeLoopSpaceTest : FreeSpec({
    tags(freeLoopSpaceTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 2))
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 4))
    include(freeLoopSpaceWithShiftDegreeTest(matrixSpace))
})

class FreeLoopSpaceTestWithDecomposedSparseMatrixSpace : FreeSpec({
    tags(freeLoopSpaceTag, bigRationalTag)

    val matrixSpace = DecomposedSparseMatrixSpaceOverBigRational
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 2))
    include(freeLoopSpaceOfEvenSphereTest(matrixSpace, 4))
    include(freeLoopSpaceWithShiftDegreeTest(matrixSpace))
})
