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

class TexDocumentTest : FreeSpec({
    tags(scriptTag)

    "begin() should work" {
        val texDocument = TexDocument {
            addLines("foo")
            begin("equation") {
                addLines("a^2 + b^2 = c^2")
            }
            addLines("bar")
        }
        texDocument.toString() shouldBe """
            |foo
            |\begin{equation}
            |  a^2 + b^2 = c^2
            |\end{equation}
            |bar
        """.trimMargin()
    }
})
