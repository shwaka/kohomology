package com.github.shwaka.kohomology.tex

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TexScriptTest : FreeSpec({
    tags(scriptTag)

    "newcommand(name, value) should add \\ if name does not start with \\" {
        val texScript = TexScript {
            newcommand("foo", "FOO")
        }
        texScript.toString() shouldBe "\\newcommand{\\foo}{FOO}"
    }

    "newcommand(name, value) should not add \\ if name starts with \\" {
        val texScript = TexScript {
            newcommand("\\foo", "FOO")
        }
        texScript.toString() shouldBe "\\newcommand{\\foo}{FOO}"
    }

    "test newcommandStar() with an argument" {
        val texScript = TexScript {
            newcommandStar("cs", "arg is #1", numArgs = 1)
        }
        texScript.toString() shouldBe "\\newcommand*{\\cs}[1]{arg is #1}"
    }

    "test renewcommand() with an optional argument" {
        val texScript = TexScript {
            renewcommand("cs", "arg is #1", numArgs = 1, defaultArg = "default value")
        }
        texScript.toString() shouldBe "\\renewcommand{\\cs}[1][default value]{arg is #1}"
    }

    "test renewcommandStar()" {
        val texScript = TexScript {
            renewcommandStar("cs", "arg is #1", numArgs = 3)
        }
        texScript.toString() shouldBe "\\renewcommand*{\\cs}[3]{arg is #1}"
    }

    "comment() should work" {
        val texScript = TexScript {
            addLines("foo")
            comment("This is a comment!")
        }
        texScript.toString() shouldBe """
            |foo
            |% This is a comment!
        """.trimMargin()
    }
})
