package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.InvalidIdentifierException
import com.github.shwaka.kohomology.dg.parser.ASTNode.Divide
import com.github.shwaka.kohomology.dg.parser.ASTNode.Identifier
import com.github.shwaka.kohomology.dg.parser.ASTNode.Multiply
import com.github.shwaka.kohomology.dg.parser.ASTNode.NatNumber
import com.github.shwaka.kohomology.dg.parser.ASTNode.Power
import com.github.shwaka.kohomology.dg.parser.ASTNode.Subtract
import com.github.shwaka.kohomology.dg.parser.ASTNode.Sum
import com.github.shwaka.kohomology.dg.parser.ASTNode.UnaryMinus
import com.github.shwaka.kohomology.dg.parser.ASTNode.Zero
import com.github.shwaka.kohomology.dg.parser.KohomologyParseException
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.parseTag
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> parseTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "parse test with polynomial ring" - {
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = freeGAlgebra.generatorList
        freeGAlgebra.context.run {
            "test GAlgebra.getValueFromASTNode" - {
                "scalar" {
                    freeGAlgebra.getValueFromASTNode(Zero) shouldBeSameInstanceAs zeroGVector
                    freeGAlgebra.getValueFromASTNode(NatNumber(0)) shouldBeSameInstanceAs zeroGVector
                    freeGAlgebra.getValueFromASTNode(NatNumber(1)) shouldBe unit
                    freeGAlgebra.getValueFromASTNode(UnaryMinus(NatNumber(2))) shouldBe (-2 * unit)
                }

                "binary operations" {
                    freeGAlgebra.getValueFromASTNode(
                        Multiply(Identifier("x"), Identifier("y"))
                    ) shouldBe (x * y)
                    freeGAlgebra.getValueFromASTNode(
                        Multiply(NatNumber(2), Identifier("x"))
                    ) shouldBe (2 * x)
                    freeGAlgebra.getValueFromASTNode(
                        Subtract(Identifier("x"), Identifier("y"))
                    ) shouldBe (x - y)
                }

                "minus as an unary operation" {
                    freeGAlgebra.getValueFromASTNode(
                        UnaryMinus(Identifier("x"))
                    ) shouldBe (-x)
                }

                "power" {
                    freeGAlgebra.getValueFromASTNode(
                        Power(
                            Sum(
                                Identifier("x"),
                                Identifier("y"),
                            ),
                            2,
                        )
                    ) shouldBe (x + y).pow(2)
                }

                "division" {
                    freeGAlgebra.getValueFromASTNode(
                        Multiply(
                            Divide(
                                NatNumber(1),
                                NatNumber(2),
                            ),
                            Identifier("x"),
                        )
                    ) shouldBe (fromIntPair(1, 2) * x)
                }
            }
            "test GAlgebra.parse" - {
                "scalar" {
                    freeGAlgebra.parse("zero") shouldBeSameInstanceAs zeroGVector
                    freeGAlgebra.parse("0") shouldBeSameInstanceAs zeroGVector
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
                    freeGAlgebra.parse("(1 + 2 * 10) * (x - y)") shouldBe (21 * (x - y))
                    freeGAlgebra.parse("0 * x") shouldBeSameInstanceAs zeroGVector
                    freeGAlgebra.parse("0 + x") shouldBe x
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
                    shouldThrow<KohomologyParseException> {
                        // parentheses on the exponent are not allowed
                        freeGAlgebra.parse("x^(1+2)")
                    }
                }

                "division" {
                    freeGAlgebra.parse("1/2*y") shouldBe (fromIntPair(1, 2) * y)
                    freeGAlgebra.parse("-2/3*x") shouldBe (fromIntPair(-2, 3) * x)
                    freeGAlgebra.parse("- 2 / 3 * x") shouldBe (fromIntPair(-2, 3) * x)
                    freeGAlgebra.parse("x * 3 / 2") shouldBe (fromIntPair(3, 2) * x)
                    freeGAlgebra.parse("x^2 - 1/2 * x * y") shouldBe (x.pow(2) - fromIntPair(1, 2) * x * y)
                    freeGAlgebra.parse("1/2") shouldBe (fromIntPair(1, 2) * unit)
                    shouldThrow<ArithmeticException> {
                        freeGAlgebra.parse("1/0")
                    }
                    freeGAlgebra.parse("x/2") shouldBe (x * fromIntPair(1, 2))
                    freeGAlgebra.parse("x/(1+2)") shouldBe (x * fromIntPair(1, 3))
                    freeGAlgebra.parse("-2*y / 3") shouldBe (y * fromIntPair(-2, 3))
                    shouldThrow<ArithmeticException> {
                        freeGAlgebra.parse("x / y")
                    }
                }

                "invalid identifier" {
                    val exception = shouldThrow<InvalidIdentifierException> {
                        freeGAlgebra.parse("a")
                    }
                    exception.identifierName shouldBe "a"
                    exception.validIdentifierNames shouldBe listOf("x", "y")
                }
            }
        }
    }

    "parse test with truncated polynomial ring" {
        val n = 5
        val indeterminateList = listOf(
            Indeterminate("x", 2),
            Indeterminate("y", 2),
        )
        val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
        val (x, y) = freeGAlgebra.generatorList
        val ideal = freeGAlgebra.context.run {
            freeGAlgebra.getIdeal(listOf(x.pow(n), y.pow(n)))
        }
        val quotGAlgebra = freeGAlgebra.getQuotientByIdeal(ideal)
        val proj = quotGAlgebra.projection
        val generatorList = freeGAlgebra.generatorList.map { gVector ->
            Pair(
                gVector.toString(),
                proj(gVector),
            )
        }
        freeGAlgebra.context.run {
            quotGAlgebra.parse(generatorList, "x * y") shouldBe proj(x * y)
            quotGAlgebra.parse(generatorList, "x^${n - 1}").isZero().shouldBeFalse()
            quotGAlgebra.parse(generatorList, "x^$n").isZero().shouldBeTrue()
            quotGAlgebra.parse(generatorList, "x^$n + x^${n - 1} * y") shouldBe
                proj(x.pow(n - 1) * y)
        }
    }
}

class FreeGAlgebraParseTest : FreeSpec({
    // FreeGAlgebraParseTest is separated from FreeGAlgebraTest
    // since tags(parseTag) and "test name".config(tags = setOf(parseTag)) do not work for nested tests.
    tags(freeGAlgebraTag, rationalTag, parseTag)

    val matrixSpace = DenseMatrixSpaceOverRational
    include(parseTest(matrixSpace))
})
