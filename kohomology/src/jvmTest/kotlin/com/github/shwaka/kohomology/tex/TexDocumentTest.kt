package com.github.shwaka.kohomology.tex

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

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
