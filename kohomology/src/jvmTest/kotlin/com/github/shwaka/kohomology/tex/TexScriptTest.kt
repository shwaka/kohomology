package com.github.shwaka.kohomology.tex

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TexScriptTest : FreeSpec({
    tags(scriptBuilderTag)

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
