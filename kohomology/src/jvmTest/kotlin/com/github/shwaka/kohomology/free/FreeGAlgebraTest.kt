package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.checkGAlgebraAxioms
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeMorphism
import com.github.shwaka.kohomology.exception.InvalidSizeException
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
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.map
import kotlin.math.absoluteValue

val freeGAlgebraTag = NamedTag("FreeGAlgebra")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> noGeneratorTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "FreeGAlgebra should work well even when the list of generator is empty" {
        val indeterminateList = listOf<Indeterminate<IntDegree, StringIndeterminateName>>()
        val freeGAlgebra = shouldNotThrowAny {
            FreeGAlgebra(matrixSpace, indeterminateList)
        }
        freeGAlgebra[0].dim shouldBe 1
        freeGAlgebra.boundedness shouldBe Boundedness(upperBound = 0, lowerBound = 0)
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> polynomialTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int, maxPolynomialLength: Int = 5) = freeSpec {
    "[polynomial, deg=$generatorDegree]" - {
        if (generatorDegree == 0)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be non-zero")
        if (generatorDegree % 2 == 1)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be even")
        if (maxPolynomialLength <= 0)
            throw IllegalArgumentException("Invalid test parameter: maxPolynomialLength must be positive")
        val indeterminateList = listOf(
            Indeterminate("x", generatorDegree),
            Indeterminate("y", generatorDegree),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)

        checkGAlgebraAxioms(freeGAlgebra, 0..(generatorDegree * 4))

        // val lengthGen = exhaustive((0..maxPolynomialLength).toList())
        val multipleDegreeGen = exhaustive((0..maxPolynomialLength).toList()).map { i -> Pair(generatorDegree * i, i + 1) }
        "freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
            checkAll(multipleDegreeGen) { (degree, expectedDim) ->
                freeGAlgebra[degree].dim shouldBe expectedDim
            }
        }
        "freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
            val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
            checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
                val degree = multipleDegree + additionalDegree
                freeGAlgebra[degree].dim shouldBe 0
            }
        }
        "check multiplication" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                (x + y).pow(0) shouldBe freeGAlgebra.unit
                (x + y).pow(1) shouldBe (x + y)
                (x + y).pow(2) shouldBe (x.pow(2) + 2 * x * y + y.pow(2))
                (x - y).pow(3) shouldBe (x.pow(3) - 3 * x.pow(2) * y + 3 * x * y.pow(2) - y.pow(3))
            }
        }
        "check isBasis" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                freeGAlgebra.isBasis(listOf(x, y), generatorDegree).shouldBeTrue()
                freeGAlgebra.isBasis(listOf(x, y), freeGAlgebra.degreeGroup.fromInt(generatorDegree)).shouldBeTrue()
                freeGAlgebra.isBasis(listOf(x.pow(2), x * y, y.pow(2)), generatorDegree * 2).shouldBeTrue()
                freeGAlgebra.isBasis(listOf(x.pow(2), x * y, y.pow(2)), freeGAlgebra.degreeGroup.fromInt(generatorDegree * 2)).shouldBeTrue()
            }
        }
        "check algebra map" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val valueList = listOf(x + y, x - y)
                val f = freeGAlgebra.getGAlgebraMap(freeGAlgebra, valueList)
                f(x) shouldBe (x + y)
                f(y) shouldBe (x - y)
                f(x + y) shouldBe (2 * x)
                f(x * y) shouldBe (x.pow(2) - y.pow(2))
            }
        }
        "containsIndeterminate test" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.containsIndeterminate(0, x).shouldBeTrue()
            freeGAlgebra.containsIndeterminate(1, x).shouldBeFalse()
            freeGAlgebra.containsIndeterminate(0, y).shouldBeFalse()
            freeGAlgebra.containsIndeterminate(1, y).shouldBeTrue()
            val xy = freeGAlgebra.context.run {
                x * y
            }
            freeGAlgebra.containsIndeterminate(0, xy).shouldBeTrue()
            freeGAlgebra.containsIndeterminate(1, xy).shouldBeTrue()
        }
        "getGLinearMap test" {
            val (x, y) = freeGAlgebra.generatorList
            val f = freeGAlgebra.leftMultiplication(x)
            freeGAlgebra.context.run {
                f(x) shouldBe x.pow(2)
                f(y) shouldBe (x * y)
                f(x.pow(3)) shouldBe x.pow(4)
            }
        }
        "listOf(x, y).product() should be x * y" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                listOf(x, y).product() shouldBe (x * y)
            }
        }
        "emptyList().product() should be unit" {
            freeGAlgebra.context.run {
                val l = emptyList<GVector<IntDegree, Monomial<IntDegree, StringIndeterminateName>, S, V>>()
                l.product() shouldBe unit
            }
        }
        "test toScalar()" {
            val (x, _) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                unit.toScalar() shouldBe one
                (2 * unit).toScalar() shouldBe two
                shouldThrow<ArithmeticException> {
                    x.toScalar()
                }
                shouldThrow<ArithmeticException> {
                    (0 * x).toScalar()
                }
                zeroGVector.toScalar() shouldBe zero
            }
        }
        "boundedness should be Boundedness(upperBound=null, lowerBound=0)" {
            val expected = if (generatorDegree > 0) {
                Boundedness(upperBound = null, lowerBound = 0)
            } else {
                Boundedness(upperBound = 0, lowerBound = null)
            }
            freeGAlgebra.boundedness shouldBe expected
        }
        "test ideal generated by x, y^2" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val ideal = freeGAlgebra.getIdeal(listOf(x, y.pow(2)))
                val incl = ideal.inclusion
                ideal[0].dim shouldBe 0
                ideal[generatorDegree].dim shouldBe 1
                ideal[2 * generatorDegree].dim shouldBe 3
                ideal.getBasis(generatorDegree).map { incl(it) } shouldBe listOf(x)
            }
        }
        "test ideal generated by empty list" {
            val ideal = freeGAlgebra.getIdeal(emptyList())
            ((-2 * generatorDegree)..(2 * generatorDegree)).forAll { degree ->
                ideal[degree].dim shouldBe 0
            }
        }
        "test quotient by ideal generated by xy" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val ideal = freeGAlgebra.getIdeal(listOf(x * y))
                val quotientGAlgebra = freeGAlgebra.getQuotientByIdeal(ideal)
                val proj = quotientGAlgebra.projection
                quotientGAlgebra[0].dim shouldBe 1
                (1..10).forAll { n ->
                    quotientGAlgebra[n * generatorDegree].dim shouldBe 2
                    quotientGAlgebra.getBasis(n * generatorDegree) shouldBe listOf(
                        proj(x.pow(n)),
                        proj(y.pow(n)),
                    )
                }
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> exteriorTest(matrixSpace: MatrixSpace<S, V, M>, generatorDegree: Int) = freeSpec {
    "[exterior, deg=$generatorDegree]" - {
        if (generatorDegree % 2 == 0)
            throw IllegalArgumentException("Invalid test parameter: generatorDegree must be odd")
        val indeterminateList = listOf(
            Indeterminate("x", generatorDegree),
            Indeterminate("y", generatorDegree),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)

        checkGAlgebraAxioms(freeGAlgebra, 0..(generatorDegree * 3))

        val multipleDegreeGen = exhaustive(
            listOf(
                Pair(0, 1),
                Pair(generatorDegree, 2),
                Pair(2 * generatorDegree, 1),
                Pair(3 * generatorDegree, 0)
            )
        )
        "freeGAlgebra should have correct dimension for degrees which are multiple of $generatorDegree" {
            checkAll(multipleDegreeGen) { (degree, expectedDim) ->
                freeGAlgebra[degree].dim shouldBe expectedDim
            }
        }
        "freeGAlgebra should have dimension zero for degrees which are not multiple of $generatorDegree" {
            if (generatorDegree.absoluteValue >= 2) {
                // exhaustive の中身が empty list だとエラーを吐く
                val additionalDegreeGen = exhaustive((1 until generatorDegree.absoluteValue).toList())
                checkAll(multipleDegreeGen, additionalDegreeGen) { (multipleDegree, _), additionalDegree ->
                    val degree = multipleDegree + additionalDegree
                    freeGAlgebra[degree].dim shouldBe 0
                }
            }
        }
        "check multiplication" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                (x + y).pow(0) shouldBe freeGAlgebra.unit
                (x + y).pow(1) shouldBe (x + y)
                (y * x) shouldBe (-x * y)
                (x + y).pow(2).isZero().shouldBeTrue()
            }
        }
        "check algebra map" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                val valueList = listOf(x + y, y)
                val f = freeGAlgebra.getGAlgebraMap(freeGAlgebra, valueList)
                f(x) shouldBe (x + y)
                f(y) shouldBe y
                f(x + y) shouldBe (x + 2 * y)
                f(x * y) shouldBe (x * y)
            }
        }
        "getGLinearMap test" {
            val (x, y) = freeGAlgebra.generatorList
            val f = freeGAlgebra.leftMultiplication(x)
            freeGAlgebra.context.run {
                f(x).isZero().shouldBeTrue()
                f(y) shouldBe (x * y)
            }
        }
        "listOf(x, y, x).product() should be 0" {
            val (x, y) = freeGAlgebra.generatorList
            freeGAlgebra.context.run {
                listOf(x, y, x).product().isZero().shouldBeTrue()
            }
        }
        "boundedness should be Boundedness(upperBound=$generatorDegree, lowerBound=0)" {
            val expected = if (generatorDegree > 0) {
                Boundedness(upperBound = 2 * generatorDegree, lowerBound = 0)
            } else {
                Boundedness(upperBound = 0, lowerBound = 2 * generatorDegree)
            }
            freeGAlgebra.boundedness shouldBe expected
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> derivationTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "derivation test (2-dim sphere)" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, y) = freeGAlgebra.generatorList
            val dx = zeroGVector
            val dy = x * x
            val d = freeGAlgebra.getDerivation(listOf(dx, dy), 1)
            d(x).isZero().shouldBeTrue()
            d(x.pow(4)).isZero().shouldBeTrue()
            d(y) shouldBe (x * x)
            d(x * y) shouldBe (x.pow(3))
            d(x.pow(3) * y) shouldBe (x.pow(5))
        }
    }

    "derivation test (contractible)" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, y) = freeGAlgebra.generatorList
            @Suppress("UnnecessaryVariable") val dx = y
            val dy = zeroGVector
            val d = freeGAlgebra.getDerivation(listOf(dx, dy), 1)
            d(y).isZero().shouldBeTrue()
            d(x) shouldBe y
            d(x.pow(2)) shouldBe (2 * x * y)
            d(x.pow(5)) shouldBe (5 * x.pow(4) * y)
            d(x * y).isZero().shouldBeTrue()
            d(x.pow(3) * y).isZero().shouldBeTrue()
        }
    }

    "getDerivation should throw IllegalArgumentException when an element of invalid degree is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, _) = freeGAlgebra.generatorList
            shouldThrow<IllegalArgumentException> {
                freeGAlgebra.getDerivation(listOf(zeroGVector, x), 1)
            }
        }
    }

    "getDerivation should throw InvalidSizeException when a list of invalid size is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val exception = shouldThrow<InvalidSizeException> {
                freeGAlgebra.getDerivation(listOf(zeroGVector), 1)
            }
            exception.message.shouldContain("2 is expected")
            exception.message.shouldContain("1 is given")
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> algebraMapTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "getAlgebraMap should throw IllegalArgumentException when an element of invalid degree is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            val (x, _) = freeGAlgebra.generatorList
            shouldThrow<IllegalArgumentException> {
                freeGAlgebra.getGAlgebraMap(freeGAlgebra, listOf(x, x.pow(2)))
            }
        }
    }

    "getAlgebraMap should throw InvalidSizeException when a list of invalid size is given" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 3),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        freeGAlgebra.context.run {
            shouldThrow<InvalidSizeException> {
                freeGAlgebra.getGAlgebraMap(freeGAlgebra, listOf(zeroGVector))
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> parseTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "parse test" - {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.context.run {
            "scalar" {
                freeGAlgebra.parse("zero") shouldBe zeroGVector
                freeGAlgebra.parse("0") shouldBe zeroGVector
                freeGAlgebra.parse("1") shouldBe unit
                freeGAlgebra.parse("-2") shouldBe (-2 * unit)
            }

            "binary operations" {
                freeGAlgebra.parse("x * y") shouldBe (x * y)
                freeGAlgebra.parse("2 * x") shouldBe (2 * x)
                freeGAlgebra.parse("x * 2") shouldBe (2 * x)
                freeGAlgebra.parse("2*x") shouldBe (2 * x)
                freeGAlgebra.parse("x*2") shouldBe (2 * x)
                freeGAlgebra.parse("x - 2 * y") shouldBe (x - 2 * y)
                freeGAlgebra.parse("x-2*y") shouldBe (x - 2 * y)
                freeGAlgebra.parse("x*x - 2*x*y + y*y") shouldBe (x - y).pow(2)
                freeGAlgebra.parse("(x + y) * (x - y)") shouldBe (x.pow(2) - y.pow(2))
                freeGAlgebra.parse("2 * (x + y)") shouldBe (2 * (x + y))
            }

            "minus as an unary operation" {
                freeGAlgebra.parse("-x") shouldBe (-x)
                freeGAlgebra.parse("-2*y") shouldBe (-2 * y)
                freeGAlgebra.parse("- 2 * y") shouldBe (-2 * y)
                freeGAlgebra.parse("x - (-y)") shouldBe (x + y)
                freeGAlgebra.parse("x - -y") shouldBe (x + y) // Regarded as (x - (-y))
                freeGAlgebra.parse("y * -3 * x") shouldBe (y * (-3) * x) // Regarded as (y * (-(3 * x))
                freeGAlgebra.parse("y * -3") // Regarded as (y * (-3))
                freeGAlgebra.parse("y*-3") // Regarded as (y * (-3))
                freeGAlgebra.parse("y * (-3)")
            }

            "power" {
                freeGAlgebra.parse("x^2 + y^2") shouldBe (x.pow(2) + y.pow(2))
                freeGAlgebra.parse("(x+y)^3") shouldBe (x + y).pow(3)
                freeGAlgebra.parse("2^4") shouldBe (16 * unit)
                freeGAlgebra.parse("(-(x-y))^3") shouldBe (-x + y).pow(3)
                freeGAlgebra.parse("(-3)^3") shouldBe (-27 * unit)
                freeGAlgebra.parse("x^0") shouldBe unit
            }

            "fraction" {
                freeGAlgebra.parse("1/2*y") shouldBe (fromIntPair(1, 2) * y)
                freeGAlgebra.parse("-2/3*x") shouldBe (fromIntPair(-2, 3) * x)
                freeGAlgebra.parse("- 2 / 3 * x") shouldBe (fromIntPair(-2, 3) * x)
                freeGAlgebra.parse("x * 3 / 2") shouldBe (fromIntPair(3, 2) * x)
                freeGAlgebra.parse("x^2 - 1/2 * x * y") shouldBe (x.pow(2) - fromIntPair(1, 2) * x * y)
                freeGAlgebra.parse("1/2") shouldBe (fromIntPair(1, 2) * unit)
                shouldThrow<ArithmeticException> {
                    freeGAlgebra.parse("1/0")
                }
            }

            "division" {
                freeGAlgebra.parse("x/2") shouldBe (x * fromIntPair(1, 2))
                freeGAlgebra.parse("-2*y / 3") shouldBe (y * fromIntPair(-2, 3))
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> toStringTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "printer test" - {
        val indeterminateList = listOf(
            Indeterminate("x", "X", 2),
            Indeterminate("y", "Y", 2),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = freeGAlgebra.generatorList

        freeGAlgebra.context.run {
            "plain printer test" {
                unit.toString() shouldBe "1"
                (2 * unit).toString() shouldBe "2"
                x.toString() shouldBe "x"
                (x * y).toString() shouldBe "xy"
                (x * y.pow(2)).toString() shouldBe "xy^2"
                freeGAlgebra.toString() shouldBe "Λ(x, y)"
            }

            "tex printer test" {
                val texPrinter = Printer(PrintType.TEX)

                unit.toString() shouldBe "1"
                (2 * unit).toString() shouldBe "2"
                texPrinter(x) shouldBe "X"
                texPrinter(x * y) shouldBe "XY"
                texPrinter(x * y.pow(2)) shouldBe "XY^{2}"
                texPrinter(freeGAlgebra) shouldBe "Λ(X, Y)"
            }
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> convertDegreeTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "convertDegreeTest for the model the sphere" - {
        val degreeGroup1 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("K", 1)
            )
        )
        val (k) = degreeGroup1.generatorList
        val indeterminateList1 = degreeGroup1.context.run {
            listOf(
                Indeterminate("x", 2 * k),
                Indeterminate("y", 4 * k - 1),
            )
        }
        val freeGAlgebra1 = FreeGAlgebra(matrixSpace, degreeGroup1, indeterminateList1)
        val (x1, y1) = freeGAlgebra1.generatorList

        val degreeGroup2 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1),
                DegreeIndeterminate("M", 1),
            )
        )
        val (n, m) = degreeGroup2.generatorList
        val degreeMorphism = degreeGroup2.context.run {
            MultiDegreeMorphism(degreeGroup1, degreeGroup2, listOf(n + m))
        }

        val (freeGAlgebra2, gLinearMapWithDegreeChange) = freeGAlgebra1.convertDegree(degreeMorphism)
        val (x2, y2) = freeGAlgebra2.generatorList

        "x1 should be sent to x2" {
            gLinearMapWithDegreeChange(x1).degree shouldBe degreeGroup2.context.run { 2 * n + 2 * m }
            gLinearMapWithDegreeChange(x1) shouldBe x2
        }

        "y1 should be sent to y2" {
            gLinearMapWithDegreeChange(y1).degree shouldBe degreeGroup2.context.run { 4 * n + 4 * m - 1 }
            gLinearMapWithDegreeChange(y1) shouldBe y2
        }

        "(x1^2 y1) should be sent to (x2^2 y2)" {
            val elm1 = freeGAlgebra1.context.run { x1.pow(2) * y1 }
            val elm2 = freeGAlgebra2.context.run { x2.pow(2) * y2 }
            gLinearMapWithDegreeChange(elm1) shouldBe elm2
        }
    }

    "toIntDegree test for the model the sphere" - {
        val degreeGroup1 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("K", 1)
            )
        )
        val (k) = degreeGroup1.generatorList
        val indeterminateList1 = degreeGroup1.context.run {
            listOf(
                Indeterminate("x", 2 * k),
                Indeterminate("y", 4 * k - 1),
            )
        }
        val freeGAlgebra1 = FreeGAlgebra(matrixSpace, degreeGroup1, indeterminateList1)
        val (x1, y1) = freeGAlgebra1.generatorList

        val (freeGAlgebra2, gLinearMapWithDegreeChange) = freeGAlgebra1.toIntDegree()
        val (x2, y2) = freeGAlgebra2.generatorList

        val kAsInt: Int = degreeGroup1.augmentation(k)

        "x1 should be sent to x2" {
            gLinearMapWithDegreeChange(x1).degree shouldBe IntDegree(2 * kAsInt)
            gLinearMapWithDegreeChange(x1) shouldBe x2
        }

        "y1 should be sent to y2" {
            gLinearMapWithDegreeChange(y1).degree shouldBe IntDegree(4 * kAsInt - 1)
            gLinearMapWithDegreeChange(y1) shouldBe y2
        }

        "(x1^2 y1) should be sent to (x2^2 y2)" {
            val elm1 = freeGAlgebra1.context.run { x1.pow(2) * y1 }
            val elm2 = freeGAlgebra2.context.run { x2.pow(2) * y2 }
            gLinearMapWithDegreeChange(elm1) shouldBe elm2
        }
    }

    "convertDegreeTest for the polynomial algebra of two variables" - {
        val degreeGroup1 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("K", 1)
            )
        )
        val (k) = degreeGroup1.generatorList
        val indeterminateList1 = degreeGroup1.context.run {
            listOf(
                Indeterminate("x", 2 * k),
                Indeterminate("y", 2 * k),
            )
        }
        val freeGAlgebra1 = FreeGAlgebra(matrixSpace, degreeGroup1, indeterminateList1)
        val (x1, y1) = freeGAlgebra1.generatorList

        val degreeGroup2 = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1),
                DegreeIndeterminate("M", 1),
            )
        )
        val (n, m) = degreeGroup2.generatorList
        val degreeMorphism = degreeGroup2.context.run {
            MultiDegreeMorphism(degreeGroup1, degreeGroup2, listOf(n + m))
        }

        val (freeGAlgebra2, gLinearMapWithDegreeChange) = freeGAlgebra1.convertDegree(degreeMorphism)
        val (x2, y2) = freeGAlgebra2.generatorList

        "(x1 - y1)^2 should be sent to (x2 - y2)^2" {
            val elm1 = freeGAlgebra1.context.run { (x1 - y1).pow(2) }
            val elm2 = freeGAlgebra2.context.run { (x2 - y2).pow(2) }
            gLinearMapWithDegreeChange(elm1) shouldBe elm2
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> duplicatedNameTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "duplicated name test" {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val exception = shouldThrow<IllegalArgumentException> {
            FreeGAlgebra(matrixSpace, indeterminateList)
        }
        exception.message shouldContain "indeterminateList contains duplicates: [x]"
    }
}

class FreeGAlgebraTest : FreeSpec({
    tags(freeGAlgebraTag, rationalTag)

    val matrixSpace = DenseMatrixSpaceOverRational

    include(noGeneratorTest(matrixSpace))

    include(polynomialTest(matrixSpace, 2))
    include(polynomialTest(matrixSpace, 4))
    include(polynomialTest(matrixSpace, -2))

    include(exteriorTest(matrixSpace, 1))
    include(exteriorTest(matrixSpace, 3))
    include(exteriorTest(matrixSpace, -3))

    include(derivationTest(matrixSpace))
    include(algebraMapTest(matrixSpace))

    include(toStringTest(matrixSpace))

    include(convertDegreeTest(matrixSpace))

    include(duplicatedNameTest(matrixSpace))
})

class FreeGAlgebraParseTest : FreeSpec({
    // FreeGAlgebraParseTest is separated from FreeGAlgebraTest
    // since tags(parseTag) and "test name".config(tags = setOf(parseTag)) do not work for nested tests.
    tags(freeGAlgebraTag, rationalTag, parseTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(parseTest(matrixSpace))
})
