package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.shwaka.kohomology.dg.parser.ASTNode.Fraction
import com.github.shwaka.kohomology.dg.parser.ASTNode.Identifier
import com.github.shwaka.kohomology.dg.parser.ASTNode.Multiply
import com.github.shwaka.kohomology.dg.parser.ASTNode.Power
import com.github.shwaka.kohomology.dg.parser.ASTNode.Subtract
import com.github.shwaka.kohomology.dg.parser.ASTNode.Sum
import com.github.shwaka.kohomology.dg.parser.ASTNode.UnaryMinus
import com.github.shwaka.kohomology.dg.parser.ASTNode.Zero
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val gAlgebraElementASTGrammarTag = NamedTag("GAlgebraElementASTGrammar")

class GAlgebraElementASTGrammarTest : FreeSpec({
    tags(gAlgebraElementASTGrammarTag)

    "\"zero\" should be parsed as Zero" {
        GAlgebraElementASTGrammar.parseToEnd("zero") shouldBe Zero
    }

    "\"0\" should be parsed as Fraction(0, 1)" {
        GAlgebraElementASTGrammar.parseToEnd("0") shouldBe Fraction(0, 1)
    }

    "\"x\" should be parsed as Identifier(\"x\")" {
        GAlgebraElementASTGrammar.parseToEnd("x") shouldBe Identifier("x")
    }

    "\"x + y\" should be parsed as Sum(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x + y") shouldBe
            Sum(Identifier("x"), Identifier("y"))
    }

    "\"x+y\" (without space) should be parsed as Sum(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x+y") shouldBe
            Sum(Identifier("x"), Identifier("y"))
    }

    "\"x - y\" should be parsed as Subtract(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x - y") shouldBe
            Subtract(Identifier("x"), Identifier("y"))
    }

    "\"-x\" should be parsed as UnaryMinus(Identifier(\"x\"))" {
        GAlgebraElementASTGrammar.parseToEnd("-x") shouldBe
            UnaryMinus(Identifier("x"))
    }

    "\"x * y\" should be parsed as Multiply(Identifier(\"x\"), Identifier(\"y\"))" {
        GAlgebraElementASTGrammar.parseToEnd("x * y") shouldBe
            Multiply(Identifier("x"), Identifier("y"))
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
})
