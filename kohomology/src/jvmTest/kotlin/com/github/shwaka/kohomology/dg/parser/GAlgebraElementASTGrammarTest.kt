package com.github.shwaka.kohomology.dg.parser

import com.github.shwaka.kohomology.dg.parser.ASTNode.Divide
import com.github.shwaka.kohomology.dg.parser.ASTNode.Identifier
import com.github.shwaka.kohomology.dg.parser.ASTNode.Multiply
import com.github.shwaka.kohomology.dg.parser.ASTNode.NatNumber
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
        shouldThrow<KohomologyParseException> {
            GAlgebraElementASTGrammar.parseToEnd("")
        }
    }

    "\"zero\" should be parsed as Zero" {
        GAlgebraElementASTGrammar.parseToEnd("zero") shouldBe Zero
    }

    "\"0\" should be parsed as NatNumber(0)" {
        GAlgebraElementASTGrammar.parseToEnd("0") shouldBe NatNumber(0)
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
            shouldThrow<KohomologyParseException> {
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

    "\"2 * x\" should be parsed as Multiply(NatNumber(2), Identifier(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("2 * x") shouldBe
            Multiply(
                NatNumber(2),
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

    "\"1/2\" should be parsed as Div(NatNumber(1), NatNumber(2))" {
        GAlgebraElementASTGrammar.parseToEnd("1/2") shouldBe
            Divide(
                NatNumber(1),
                NatNumber(2),
            )
    }

    "\"-2/3\" should be parsed as UnaryMinus(Div(NatNumber(2), NatNumber(3)))" {
        GAlgebraElementASTGrammar.parseToEnd("-2/3") shouldBe
            UnaryMinus(
                Divide(
                    NatNumber(2),
                    NatNumber(3),
                )
            )
    }

    "\"1/0\" should be parsed as Div(NatNumber(1), NatNumber(0))" {
        // This is mathematically nonsense, but parser should not throw exception.
        GAlgebraElementASTGrammar.parseToEnd("1/0") shouldBe
            Divide(
                NatNumber(1),
                NatNumber(0),
            )
    }

    "\"-1 / 2 * x\" should be parsed as UnaryMinus(Multiply(Div(NatNumber(1), NatNumber(2)), Identifier(x)))" {
        GAlgebraElementASTGrammar.parseToEnd("-1 / 2 * x") shouldBe
            UnaryMinus(
                Multiply(
                    Divide(NatNumber(1), NatNumber(2)),
                    Identifier("x"),
                )
            )
    }

    "\"(-1/2) * x\" should be parsed as Multiply(UnaryMinus(Div(NatNumber(1), NatNumber(2))), Indeterminate(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("(-1/2) * x") shouldBe
            Multiply(
                UnaryMinus(
                    Divide(
                        NatNumber(1),
                        NatNumber(2),
                    )
                ),
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

    "\"x / 2\" should be parsed as Div(Identifier(\"x\"), NatNumber(2))" {
        GAlgebraElementASTGrammar.parseToEnd("x / 2") shouldBe
            Divide(
                Identifier("x"),
                NatNumber(2),
            )
    }

    "\"2 * x / 3\" should be parsed as Div(Multiply(NatNumber(2), Identifier(\"x\")), NatNumber(3))" {
        GAlgebraElementASTGrammar.parseToEnd("2 * x / 3") shouldBe
            Divide(
                Multiply(
                    NatNumber(2),
                    Identifier("x"),
                ),
                NatNumber(3),
            )
    }

    "\"x / 2 * 3\" should not be parsed" {
        GAlgebraElementASTGrammar.parseToEnd("x / 2 * 3") shouldBe
            Multiply(
                Divide(
                    Identifier("x"),
                    NatNumber(2),
                ),
                NatNumber(3),
            )
    }
})
