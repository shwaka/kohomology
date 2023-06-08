package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.parser.ParseException
import com.github.shwaka.kohomology.dg.parser.ASTNode.Div
import com.github.shwaka.kohomology.dg.parser.ASTNode.Fraction
import com.github.shwaka.kohomology.dg.parser.ASTNode.Identifier
import com.github.shwaka.kohomology.dg.parser.ASTNode.Multiply
import com.github.shwaka.kohomology.dg.parser.ASTNode.Power
import com.github.shwaka.kohomology.dg.parser.ASTNode.Subtract
import com.github.shwaka.kohomology.dg.parser.ASTNode.Sum
import com.github.shwaka.kohomology.dg.parser.ASTNode.UnaryMinus
import com.github.shwaka.kohomology.dg.parser.ASTNode.Zero
import com.github.shwaka.kohomology.util.IdentifierTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

val gAlgebraElementASTGrammarTag = NamedTag("GAlgebraElementASTGrammar")

class GAlgebraElementASTGrammarTest : FreeSpec({
    tags(gAlgebraElementASTGrammarTag)

    "empty exception should not be parsed" {
        shouldThrow<ParseException> {
            GAlgebraElementASTGrammar.parseToEnd("")
        }
    }

    "\"zero\" should be parsed as Zero" {
        GAlgebraElementASTGrammar.parseToEnd("zero") shouldBe Zero
    }

    "\"0\" should be parsed as Fraction(0, 1)" {
        GAlgebraElementASTGrammar.parseToEnd("0") shouldBe Fraction(0, 1)
    }

    "\"x\" should be parsed as Identifier(\"x\")" {
        GAlgebraElementASTGrammar.parseToEnd("x") shouldBe Identifier("x")
    }

    "test valid identifiers" {
        IdentifierTest.validNameList.forAll { name ->
            GAlgebraElementASTGrammar.parseToEnd(name) shouldBe Identifier(name)
        }
    }

    "test invalid identifiers" {
        // IdentifierTest.invalidNameList cannot be used here
        listOf(
            "1a", "0x",
        ).forAll { name ->
            shouldThrow<ParseException> {
                GAlgebraElementASTGrammar.parseToEnd(name)
            }
        }
    }

    "\"x + y\" should be parsed as Sum(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x + y") shouldBe
            Sum(Identifier("x"), Identifier("y"))
    }

    "\"x+y\" (without space) should be parsed as Sum(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x+y") shouldBe
            Sum(Identifier("x"), Identifier("y"))
    }

    "\"x + y + z\" should be parsed as Sum(Sum(Identifier(\"x\"), Identifier(\"y\")), Identifier(\"z\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x + y + z") shouldBe
            Sum(
                Sum(Identifier("x"), Identifier("y")),
                Identifier("z"),
            )
    }

    "\"x - y\" should be parsed as Subtract(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x - y") shouldBe
            Subtract(Identifier("x"), Identifier("y"))
    }

    "\"-x\" should be parsed as UnaryMinus(Identifier(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("-x") shouldBe
            UnaryMinus(Identifier("x"))
    }

    "\"2 * x\" should be parsed as Multiply(Fraction(2, 1), Identifier(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("2 * x") shouldBe
            Multiply(
                Fraction(2, 1),
                Identifier("x"),
            )
    }

    "\"x * y\" should be parsed as Multiply(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x * y") shouldBe
            Multiply(Identifier("x"), Identifier("y"))
    }

    "\"x * y + z\" should be parsed as Sum(Multiply(Identifier(\"x\"), Identifier(\"y\")), Identifier(\"z\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x * y + z") shouldBe
            Sum(
                Multiply(Identifier("x"), Identifier("y")),
                Identifier("z"),
            )
    }

    "\"x + y * z\" should be parsed as Sum(Identifier(\"x\"), Multiply(Identifier(\"y\"), Identifier(\"z\")))" {
        GAlgebraElementASTGrammar.parseToEnd("x + y * z") shouldBe
            Sum(
                Identifier("x"),
                Multiply(Identifier("y"), Identifier("z")),
            )
    }

    "\"x^3\" should be parsed as Power(Identifier(\"x\"), 3)" {
        GAlgebraElementASTGrammar.parseToEnd("x^3") shouldBe
            Power(Identifier("x"), 3)
    }

    "\"1/2\" should be parsed as Fraction(1, 2)" {
        GAlgebraElementASTGrammar.parseToEnd("1/2") shouldBe Fraction(1, 2)
    }

    "\"-2/3\" should be parsed as UnaryMinus(Fraction(2, 3))" {
        GAlgebraElementASTGrammar.parseToEnd("-2/3") shouldBe
            UnaryMinus(Fraction(2, 3))
    }

    "\"1/0\" should be parsed as Fraction(1, 0)" {
        // This is mathematically nonsense, but parser should not throw exception.
        GAlgebraElementASTGrammar.parseToEnd("1/0") shouldBe Fraction(1, 0)
    }

    "\"-1 / 2 * x\" should be parsed as UnaryMinus(Multiply(Fraction(1, 2), Identifier(x)))" {
        GAlgebraElementASTGrammar.parseToEnd("-1 / 2 * x") shouldBe
            UnaryMinus(
                Multiply(
                    Fraction(1, 2),
                    Identifier("x"),
                )
            )
    }

    "\"(-1/2) * x\" should be parsed as Multiply(UnaryMinus(Fraction(1, 2)), Indeterminate(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("(-1/2) * x") shouldBe
            Multiply(
                UnaryMinus(Fraction(1, 2)),
                Identifier("x"),
            )
    }

    "\"(x - y)^2\" should be parsed as Power(Subtract(Identifier(\"x\"), Identifier(\"y\")), 2)" {
        GAlgebraElementASTGrammar.parseToEnd("(x - y)^2") shouldBe
            Power(
                Subtract(
                    Identifier("x"),
                    Identifier("y"),
                ),
                2,
            )
    }

    "\"x / 2\" should be parsed as Div(Identifier(\"x\"), 2)" {
        GAlgebraElementASTGrammar.parseToEnd("x / 2") shouldBe
            Div(Identifier("x"), 2)
    }

    "\"2 * x / 3\" should be parsed as Div(Multiply(Fraction(2, 1), Identifier(\"x\")), 3)" {
        GAlgebraElementASTGrammar.parseToEnd("2 * x / 3") shouldBe
            Div(
                Multiply(
                    Fraction(2, 1),
                    Identifier("x"),
                ),
                3,
            )
    }

    "\"x / 2 * 3\" should not be parsed" {
        shouldThrow<ParseException> {
            GAlgebraElementASTGrammar.parseToEnd("x / 2 * 3")
        }
    }
})
