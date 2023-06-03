package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.QuotDGAlgebra
import com.github.shwaka.kohomology.dg.SubQuotDGAlgebra
import com.github.shwaka.kohomology.dg.checkDGAlgebraAxioms
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.example.sphereWithMultiDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DecomposedSparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.pow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

val freeDGAlgebraTag = NamedTag("FreeDGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> constructorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "constructor should throw IllegalArgumentException when d^2 != 0" - {
        val indeterminateList = listOf(
            Indeterminate("x", 3),
            Indeterminate("y", 2),
            Indeterminate("z", 1),
        )

        "fromList" {
            shouldThrow<IllegalArgumentException> {
                FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, y, _) ->
                    listOf(zeroGVector, x, y)
                }
            }
        }

        "fromMap" {
            shouldThrow<IllegalArgumentException> {
                FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (x, y, z) ->
                    mapOf(
                        y to x,
                        z to y,
                    )
                }
            }
        }
    }

    "constructor should work well even when the list of generator is empty" - {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()

        "fromList" {
            val freeDGAlgebra = shouldNotThrowAny {
                FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { emptyList() }
            }
            val algebraMap = freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, emptyList())
            freeDGAlgebra.context.run {
                d(unit).isZero().shouldBeTrue()
                algebraMap(unit) shouldBe unit
            }
        }

        "fromMap" {
            val freeDGAlgebra = shouldNotThrowAny {
                FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { emptyMap() }
            }
            val algebraMap = freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, emptyList())
            freeDGAlgebra.context.run {
                d(unit).isZero().shouldBeTrue()
                algebraMap(unit) shouldBe unit
            }
        }
    }

    "constructor should work well with IntDegree and StringIndeterminate" - {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )

        "fromList" {
            val freeDGAlgebra = FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, _) ->
                val dx = zeroGVector
                val dy = x.pow(2)
                listOf(dx, dy)
            }
            freeDGAlgebra.context.run {
                val (x, y) = freeDGAlgebra.generatorList
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(x * y) shouldBe x.pow(3)
            }
        }

        "fromMap" {
            val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (x, y) ->
                mapOf(y to x.pow(2))
            }
            freeDGAlgebra.context.run {
                val (x, y) = freeDGAlgebra.generatorList
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(x * y) shouldBe x.pow(3)
            }
        }
    }

    "constructor should work well with all optional arguments" - {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val degreeGroup = IntDegreeGroup
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<Monomial<IntDegree, StringIndeterminateName>, S> =
            InternalPrintConfig.Companion::default

        "fromList" {
            val freeDGAlgebra =
                FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList, getInternalPrintConfig) { (x, _) ->
                    val dx = zeroGVector
                    val dy = x.pow(2)
                    listOf(dx, dy)
                }
            freeDGAlgebra.context.run {
                val (x, y) = freeDGAlgebra.generatorList
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(x * y) shouldBe x.pow(3)
            }
        }

        "fromMap" {
            val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, degreeGroup, indeterminateList, getInternalPrintConfig) { (x, y) ->
                mapOf(y to x.pow(2))
            }
            freeDGAlgebra.context.run {
                val (x, y) = freeDGAlgebra.generatorList
                d(unit).isZero().shouldBeTrue()
                d(x).isZero().shouldBeTrue()
                d(x * y) shouldBe x.pow(3)
            }
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
        val (x) = freeDGAlgebra.generatorList
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
            "freeDGAlgebra.isCommutative should be true" {
                freeDGAlgebra.isCommutative.shouldBeTrue()
            }
            "freeDGAlgebra.cohomology.isCommutative should be true" {
                freeDGAlgebra.cohomology.isCommutative.shouldBeTrue()
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
        val (x, y) = freeDGAlgebra.generatorList
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
            "freeDGAlgebra.isCommutative should be true" {
                freeDGAlgebra.isCommutative.shouldBeTrue()
            }
            "freeDGAlgebra.cohomology.isCommutative should be true" {
                freeDGAlgebra.cohomology.isCommutative.shouldBeTrue()
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDGAlgebraMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    val sphereDim: Int = 4
    "[test getDGAlgebraMap for the sphere of dimension $sphereDim]" - {
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x, y) = freeDGAlgebra.generatorList
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
    val sphereDim: Int = 4 // must be even
    "[test getDGDerivation for the sphere of dimension $sphereDim]" - {
        val freeDGAlgebra = sphere(matrixSpace, sphereDim)
        val (x, y) = freeDGAlgebra.generatorList
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
    "[test getDGDerivation for the product $sphereDim-sphere and a contractible space]" - {
        val n = sphereDim // must be even
        val m = -3 // must be odd
        val indeterminateList = listOf(
            Indeterminate("x", n),
            Indeterminate("y", 2 * n - 1),
            Indeterminate("v", 2 * n - m),
            Indeterminate("w", 2 * n - m - 1),
        )
        val freeDGAlgebra = FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, _, v, _) ->
            listOf(zeroGVector, x.pow(2), zeroGVector, v)
        }
        val (x, y, _, _) = freeDGAlgebra.generatorList
        freeDGAlgebra.context.run {
            "check sign in the commutativity of d and a derivation" {
                shouldNotThrowAny {
                    val valueList = listOf(zeroGVector, zeroGVector, x.pow(2), -y)
                    freeDGAlgebra.getDGDerivation(valueList, m)
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> pullbackOfHopfFibrationOverS4Test(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "model in FHT Section 12 (a) Example 7 (p.147)" - {
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(matrixSpace)
        val (a, b, x, y, z) = freeDGAlgebra.generatorList
        freeDGAlgebra.context.run {
            checkDGAlgebraAxioms(freeDGAlgebra, 0..15)
            "check differential" {
                d(x) shouldBe a.pow(2)
                d(y) shouldBe a * b
                d(z) shouldBe b.pow(2)
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
                val f = freeDGAlgebra.leftMultiplicationByCocycle(a * y - b * x).inducedMapOnCohomology
                f(bClass) shouldBe topClass
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> quotientTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test quotient by an ideal" - {
        "quotient of model in FHT Section 12 (a) Example 7 (p.147)" - {
            val freeDGAlgebra = pullbackOfHopfFibrationOverS4(matrixSpace)
            val (a, b, x, y, z) = freeDGAlgebra.generatorList
            val ideal = freeDGAlgebra.context.run {
                freeDGAlgebra.getDGIdeal(
                    listOf(a.pow(2), b.pow(2), x, z)
                )
            }
            // subQuotDGAlgebra should be quasi-isomorphic to freeDGAlgebra
            // since Λ(a, b, x, z) → ∧(a, b, x, z)/(a^2, b^2, x, z) is (obviously) quasi-isomorphism.
            val quotDGAlgebra = freeDGAlgebra.getQuotientByIdeal(ideal)
            val proj = quotDGAlgebra.projection
            val cohomProj = proj.induce(freeDGAlgebra.cohomology, quotDGAlgebra.cohomology)

            "quotDGAlgebra should be an instance of QuotDGAlgebra" {
                quotDGAlgebra.shouldBeInstanceOf<QuotDGAlgebra<*, *, S, V, M>>()
            }
            checkDGAlgebraAxioms(quotDGAlgebra, 0..15)
            freeDGAlgebra.context.run {
                quotDGAlgebra.context.run {
                    "check image of elements" {
                        proj(a).isZero().shouldBeFalse()
                        proj(b).isZero().shouldBeFalse()
                        proj(a.pow(2)).isZero().shouldBeTrue()
                        proj(b.pow(2)).isZero().shouldBeTrue()
                        proj(x).isZero().shouldBeTrue()
                        proj(y).isZero().shouldBeFalse()
                        proj(z).isZero().shouldBeTrue()
                    }
                    "check differential" {
                        d(proj(a)).isZero().shouldBeTrue()
                        d(proj(b)).isZero().shouldBeTrue()
                        d(proj(x)).isZero().shouldBeTrue()
                        d(proj(z)).isZero().shouldBeTrue()
                        d(proj(y)).isZero().shouldBeFalse()
                        d(proj(a * y)).isZero().shouldBeTrue()
                        d(proj(b * y)).isZero().shouldBeTrue()
                        d(proj(a * b * y)).isZero().shouldBeTrue()
                    }
                    "check cohomology class" {
                        // Note: a * y is not a cocycle in freeDGAlgebra
                        proj(a * y).cohomologyClass() shouldBe
                            cohomProj((a * y - b * x).cohomologyClass())
                        // Note: b * y is not a cocycle in freeDGAlgebra
                        proj(b * y).cohomologyClass() shouldBe
                            cohomProj((b * y - a * z).cohomologyClass())
                        // Note: a * b * y is not a cocycle in freeDGAlgebra
                        // This is the top cohomology class
                        proj(a * b * y).cohomologyClass() shouldBe
                            cohomProj((a * b * y - b.pow(2) * x).cohomologyClass())
                    }
                }
            }
            "check dimension of cohomology" {
                (0 until 15).forAll { n ->
                    quotDGAlgebra.cohomology[n].dim shouldBe freeDGAlgebra.cohomology[n].dim
                }
            }
            "projection should be quasi-isomorphism" {
                (0 until 15).forAll { n ->
                    proj.inducedMapOnCohomology[n].isIsomorphism().shouldBeTrue()
                }
            }
        }
        "model of moment angle complex" - {
            // Model of Z_K(D^2, S^1) where K=S^0 (two points).
            // It is homeomorphic to (D^2×S^1)∪(S^1×D^2) = S^3.
            val indeterminateList = listOf(
                Indeterminate("t1", 2),
                Indeterminate("t2", 2),
                Indeterminate("x1", 1),
                Indeterminate("x2", 1),
            )
            val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (t1, t2, x1, x2) ->
                mapOf(
                    x1 to t1,
                    x2 to t2,
                )
            }
            val (t1, t2, x1, x2) = freeDGAlgebra.generatorList
            freeDGAlgebra.context.run {
                val ideal = freeDGAlgebra.getDGIdeal(listOf(t1 * t2))
                val subQuotDGAlgebra = freeDGAlgebra.getQuotientByIdeal(ideal)
                val proj = subQuotDGAlgebra.projection
                val u1 = proj(t1)
                val u2 = proj(t2)
                val y1 = proj(x1)
                val y2 = proj(x2)
                subQuotDGAlgebra.context.run {
                    "check differential" {
                        d(u1).isZero().shouldBeTrue()
                        d(u2).isZero().shouldBeTrue()
                        d(y1) shouldBe u1
                        d(y2) shouldBe u2
                        d(y1 * u2).isZero().shouldBeTrue() // d([x1*t2]) = [t1*t2] = 0
                        d(y1 * y2 * u1.pow(3)) shouldBe (y2 * u1.pow(4))
                        // ↑ d([x1*x2*t1^3]) = [x2*t1^4 + x1*t1^3*t2] = [x2*t1^4]
                    }
                    "the top cohomology class should be [t1*x2]=[t2*x1]" {
                        val basis = subQuotDGAlgebra.cohomology.getBasis(3)
                        basis shouldHaveSize 1
                        basis[0] shouldBe (u1 * y2).cohomologyClass()
                        basis[0] shouldBe (u2 * y1).cohomologyClass()
                    }
                }

                "check dimension of cohomology" {
                    (0..20).forAll { n ->
                        val expected = when (n) {
                            0, 3 -> 1
                            else -> 0
                        }
                        subQuotDGAlgebra.cohomology[n].dim shouldBe expected
                    }
                }
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
            FreeDGAlgebra.fromList(matrixSpace, indeterminateList) { (x, y, _, _, _) ->
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
        val freeDGAlgebra = FreeDGAlgebra.fromList(matrixSpace, generatorList)
        val (x, y) = freeDGAlgebra.generatorList
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
        val freeDGAlgebra = FreeDGAlgebra.fromList(matrixSpace, generatorList) { listOf(zeroGVector, zeroGVector) }
        val texPrinter = Printer(PrintType.TEX)
        "print cohomology classes as TeX" - {
            val (a, b) = freeDGAlgebra.generatorList
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
    tags(freeDGAlgebraTag, rationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(constructorTest(matrixSpace))
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
    tags(freeDGAlgebraTag, rationalTag)

    val matrixSpace = DecomposedSparseMatrixSpaceOverRational
    include(constructorTest(matrixSpace))
    include(oddSphereModelTest(matrixSpace, 3))
    include(evenSphereModelTest(matrixSpace, 2))
    include(getDGAlgebraMapTest(matrixSpace))
    include(getDGDerivationTest(matrixSpace))
    include(pullbackOfHopfFibrationOverS4Test(matrixSpace))
    include(errorTest(matrixSpace))
    include(parseDifferentialValueTest(matrixSpace))
    include(printerTest(matrixSpace))
    include(toIntDegreeTest(matrixSpace))
    include(quotientTest(matrixSpace))
})
