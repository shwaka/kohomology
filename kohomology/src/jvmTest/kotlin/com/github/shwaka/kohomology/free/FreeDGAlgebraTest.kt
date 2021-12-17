package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.checkDGAlgebraAxioms
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.example.sphereWithMultiDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.pow
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

val freeDGAlgebraTag = NamedTag("FreeDGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invalidModelTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "FreeDGAlgebra should throw IllegalArgumentException when d^2 != 0" {
        val indeterminateList = listOf(
            Indeterminate("x", 3),
            Indeterminate("y", 2),
            Indeterminate("z", 1),
        )
        shouldThrow<IllegalArgumentException> {
            FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y, _) ->
                listOf(zeroGVector, x, y)
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pointModelTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "FreeDGAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()
        val freeDGAlgebra = shouldNotThrowAny {
            FreeDGAlgebra(matrixSpace, indeterminateList) { emptyList() }
        }
        val algebraMap = freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, emptyList())
        freeDGAlgebra.context.run {
            d(unit).isZero().shouldBeTrue()
            algebraMap(unit) shouldBe unit
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> oddSphereModelTest(matrixSpace: MatrixSpace<S, V, M>, sphereDim: Int) = freeSpec {
    "[sphere of odd dim $sphereDim]" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 == 0)
            throw IllegalArgumentException("The dimension of a sphere must be odd in this test")
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            checkDGAlgebraAxioms(freeDGAlgebra, 0..(sphereDim * 2))

            "check differential" {
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
            }
            "check cohomology" {
                (0 until (sphereDim * 3)).forAll { n ->
                    val expectedDim = when (n) {
                        0, sphereDim -> 1
                        else -> 0
                    }
                    freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> evenSphereModelTest(matrixSpace: MatrixSpace<S, V, M>, sphereDim: Int) = freeSpec {
    "[sphere of even dim $sphereDim]" - {
        if (sphereDim <= 0)
            throw IllegalArgumentException("The dimension of a sphere must be positive")
        if (sphereDim % 2 == 1)
            throw IllegalArgumentException("The dimension of a sphere must be even in this test")
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x, y) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            checkDGAlgebraAxioms(freeDGAlgebra, 0..(sphereDim * 4))

            "check differential" {
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(y) shouldBe x.pow(2)
            }
            "check cohomology" {
                (0 until (sphereDim * 3)).forAll { n ->
                    val expectedDim = when (n) {
                        0, sphereDim -> 1
                        else -> 0
                    }
                    freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
                }
            }
            "check cocycle" {
                shouldNotThrowAny { cohomologyClassOf(x) }
                shouldNotThrowAny { cohomologyClassOf(x.pow(2)) }
                shouldThrow<IllegalArgumentException> { cohomologyClassOf(y) }
            }
            "check bounding cochain" {
                x.boundingCochain().shouldBeNull()
                val a = x.pow(2).boundingCochain() ?: throw Exception("x^2 should be a coboundary")
                d(a) shouldBe x.pow(2)
            }
            "differential.kernelBasis(sphereDim * n) should be listOf(x^n)" {
                (0 until 10).forAll { n ->
                    d.kernelBasis(sphereDim * n) shouldBe listOf(x.pow(n))
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDGAlgebraMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val sphereDim: Int = 4
    "[test getDGAlgebraMap for the sphere of dimension $sphereDim]" - {
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x, y) = freeDGAlgebra.gAlgebra.generatorList
        val a: Int = 2
        freeDGAlgebra.context.run {
            "(x->${a}x, y->${a.pow(2)}y) should give a dga map" {
                val f = shouldNotThrowAny {
                    freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, listOf(a * x, a.pow(2) * y))
                }
                f(x) shouldBe (a * x)
                f(y) shouldBe (a.pow(2) * y)
                val n = 3
                f(x.pow(n)) shouldBe (a.pow(n) * x.pow(n))
            }
            "(x->${a}x, y->${a}y) should not give a dga map" {
                shouldThrow<IllegalArgumentException> {
                    freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, listOf(a * x, a * y))
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDGDerivationTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val sphereDim: Int = 4
    "[test getDGDerivation for the sphere of dimension $sphereDim]" - {
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x, y) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            "(x->0, y->1) should give a dg derivation" {
                val f = shouldNotThrowAny {
                    freeDGAlgebra.getDGDerivation(listOf(zeroGVector, unit), -(2 * sphereDim - 1))
                }
                f(x).isZero().shouldBeTrue()
                f(y) shouldBe (unit)
                val n = 3
                f(x.pow(n) * y) shouldBe (x.pow(n))
            }
            "(x->1, y->0) should not give a dg derivation" {
                shouldThrow<IllegalArgumentException> {
                    freeDGAlgebra.getDGDerivation(listOf(unit, zeroGVector), -sphereDim)
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4Test(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "model in FHT Section 12 (a) Example 7 (p.147)" - {
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(matrixSpace)
        val (a, b, x, y, z) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            checkDGAlgebraAxioms(freeDGAlgebra, 0..15)
            "check differential" {
                d(x * y) shouldBe (a.pow(2) * y - a * b * x)
                d(x * y * z) shouldBe (a.pow(2) * y * z - a * b * x * z + b.pow(2) * x * y)
            }
            "check dimension of cohomology" {
                (0 until 12).forAll { n ->
                    val expectedDim = when (n) {
                        0, 7 -> 1
                        2, 5 -> 2
                        else -> 0
                    }
                    freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
                }
            }
            "check top class" {
                val bClass = b.cohomologyClass()
                val someClass = (a * y - b * x).cohomologyClass()
                val topClass = (a * b * y - b.pow(2) * x).cohomologyClass()
                freeDGAlgebra.cohomology.context.run {
                    (bClass * someClass) shouldBe topClass
                }
                val f = freeDGAlgebra.leftMultiplication(a * y - b * x).inducedMapOnCohomology()
                f(bClass) shouldBe topClass
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> errorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "generator must be sorted along a Sullivan filtration" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        shouldThrow<IllegalArgumentException> {
            FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y, _, _, _) ->
                listOf(y, x.pow(2))
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> parseDifferentialValueTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "construct DGA by parsing string".config(tags = setOf(parseTag)) {
        val generatorList = listOf(
            GeneratorOfFreeDGA("x", 2, "zero"),
            GeneratorOfFreeDGA("y", 3, "x^2"),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, generatorList)
        val (x, y) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            d(x).isZero().shouldBeTrue()
            d(y) shouldBe (x.pow(2))
            parse("2 * x^2") shouldBe (2 * x.pow(2))
        }
        (0 until 10).forAll { degree ->
            val expectedDim = when (degree) {
                0, 2 -> 1
                else -> 0
            }
            freeDGAlgebra.cohomology[degree].dim shouldBe expectedDim
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> printerTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "printer test for FreeDGAlgebra" - {
        val generatorList = listOf(
            Indeterminate("a", "A", 2),
            Indeterminate("b", "B", 2),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, generatorList) { listOf(zeroGVector, zeroGVector) }
        val texPrinter = Printer(PrintType.TEX)
        "print cohomology classes as TeX" - {
            val (a, b) = freeDGAlgebra.gAlgebra.generatorList
            freeDGAlgebra.context.run {
                "length 1" {
                    texPrinter(a.cohomologyClass()) shouldBe "[A]"
                    texPrinter(b.cohomologyClass()) shouldBe "[B]"
                }
                "length 2" {
                    texPrinter(a.pow(2).cohomologyClass()) shouldBe "[A^{2}]"
                    texPrinter(b.pow(2).cohomologyClass()) shouldBe "[B^{2}]"
                    texPrinter((a * b).cohomologyClass()) shouldBe "[AB]"
                    texPrinter((a.pow(2) + b.pow(2)).cohomologyClass()) shouldBe "[A^{2}] + [B^{2}]"
                }
            }
        }
        "print FreeDGAlgebra" {
            freeDGAlgebra.toString() shouldBe "(Λ(a, b), d)"
            texPrinter(freeDGAlgebra) shouldBe "(Λ(A, B), d)"
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> toIntDegreeTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "toIntDegree Test" - {
        val sphereDim = 4
        val freeDGAlgebraWithMultiDegree = sphereWithMultiDegree(matrixSpace, sphereDim)
        val (freeDGAlgebra, _) = freeDGAlgebraWithMultiDegree.toIntDegree()
        (0 until 10).forAll { degree ->
            val expectedDim = when (degree) {
                0, sphereDim -> 1
                else -> 0
            }
            freeDGAlgebra.cohomology[degree].dim shouldBe expectedDim
        }
    }
}

class FreeDGAlgebraTest : FreeSpec({
    tags(freeDGAlgebraTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverBigRational
    include(invalidModelTest(matrixSpace))
    include(pointModelTest(matrixSpace))
    include(oddSphereModelTest(matrixSpace, 3))
    include(evenSphereModelTest(matrixSpace, 2))
    include(getDGAlgebraMapTest(matrixSpace))
    include(getDGDerivationTest(matrixSpace))
    include(pullbackOfHopfFibrationOverS4Test(matrixSpace))
    include(errorTest(matrixSpace))
    include(parseDifferentialValueTest(matrixSpace))
    include(printerTest(matrixSpace))
    include(toIntDegreeTest(matrixSpace))
})

class FreeDGAlgebraTestWithDecomposedSparseMatrixSpace : FreeSpec({
    tags(freeDGAlgebraTag, bigRationalTag)

    val matrixSpace = DecomposedSparseMatrixSpaceOverBigRational
    include(invalidModelTest(matrixSpace))
    include(pointModelTest(matrixSpace))
    include(oddSphereModelTest(matrixSpace, 3))
    include(evenSphereModelTest(matrixSpace, 2))
    include(getDGAlgebraMapTest(matrixSpace))
    include(getDGDerivationTest(matrixSpace))
    include(pullbackOfHopfFibrationOverS4Test(matrixSpace))
    include(errorTest(matrixSpace))
    include(parseDifferentialValueTest(matrixSpace))
    include(printerTest(matrixSpace))
    include(toIntDegreeTest(matrixSpace))
})
