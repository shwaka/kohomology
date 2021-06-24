package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.PrintType
import com.github.shwaka.kohomology.vectsp.Printer
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
            "check differential" {
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
            }
            "check cohomology" {
                for (n in 0 until (sphereDim * 3)) {
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
            "check differential" {
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(y) shouldBe x.pow(2)
            }
            "check cohomology" {
                for (n in 0 until (sphereDim * 3)) {
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
                for (n in 0 until 10) {
                    d.kernelBasis(sphereDim * n) shouldBe listOf(x.pow(n))
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4Test(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "model in FHT Section 12 (a) Example 7 (p.147)" {
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(matrixSpace)
        val (a, b, x, y, z) = freeDGAlgebra.gAlgebra.generatorList
        freeDGAlgebra.context.run {
            d(x * y) shouldBe (a.pow(2) * y - a * b * x)
            d(x * y * z) shouldBe (a.pow(2) * y * z - a * b * x * z + b.pow(2) * x * y)
        }
        for (n in 0 until 12) {
            val expectedDim = when (n) {
                0, 7 -> 1
                2, 5 -> 2
                else -> 0
            }
            freeDGAlgebra.cohomology[n].dim shouldBe expectedDim
        }
        freeDGAlgebra.context.run {
            val bClass = b.cohomologyClass()
            val someClass = (a * y - b * x).cohomologyClass()
            val topClass = (a * b * y - b.pow(2) * x).cohomologyClass()
            freeDGAlgebra.cohomology.context.run {
                (bClass * someClass) shouldBe topClass
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
        for (degree in 0 until 10) {
            val expectedDim = when (degree) {
                0, 2 -> 1
                else -> 0
            }
            freeDGAlgebra.cohomology[degree].dim shouldBe expectedDim
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> toStringInCohomologyTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "print cohomology classes as TeX" - {
        val generatorList = listOf(
            Indeterminate("a", "A", 2),
            Indeterminate("b", "B", 2),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, generatorList) { listOf(zeroGVector, zeroGVector) }
        val texPrinter = Printer(PrintType.TEX)
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
}

class FreeDGAlgebraTest : FreeSpec({
    tags(freeDGAlgebraTag, bigRationalTag)

    val matrixSpace = DenseMatrixSpaceOverBigRational
    include(invalidModelTest(matrixSpace))
    include(pointModelTest(matrixSpace))
    include(oddSphereModelTest(matrixSpace, 3))
    include(evenSphereModelTest(matrixSpace, 2))
    include(pullbackOfHopfFibrationOverS4Test(matrixSpace))
    include(errorTest(matrixSpace))
    include(parseDifferentialValueTest(matrixSpace))
    include(toStringInCohomologyTest(matrixSpace))
})
