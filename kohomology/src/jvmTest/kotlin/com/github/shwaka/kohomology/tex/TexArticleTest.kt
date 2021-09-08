package com.github.shwaka.kohomology.tex

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TexArticleTest : FreeSpec({
    tags(scriptTag)

    "test whole article" {
        val texArticle = TexArticle {
            preamble {
                documentclass("article")
                usepackage("amsmath")
            }

            document {
                begin("equation") {
                    addLines("a^2 + b^2 = c^2")
                }
            }
        }
        texArticle.toString() shouldBe """
            |\documentclass{article}
            |\usepackage{amsmath}
            |
            |\begin{document}
            |\begin{equation}
            |  a^2 + b^2 = c^2
            |\end{equation}
            |\end{document}
        """.trimMargin()
    }
})
