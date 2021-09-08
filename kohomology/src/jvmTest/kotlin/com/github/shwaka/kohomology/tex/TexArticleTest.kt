package com.github.shwaka.kohomology.tex

import com.github.shwaka.kohomology.compileTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
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

class TexArticleCompileTest : FreeSpec({
    tags(scriptTag, compileTag)

    "correct code should compile" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                }
                document {
                    section("foo")
                }
            }
        """.trimIndent()
        code.shouldCompile()
    }

    "preamble in preamble should not compile" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                    preamble { }
                }
                document {
                    section("foo")
                }
            }
        """.trimIndent()
        code.shouldNotCompile()
    }

    "preamble in document should not compile" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                }
                document {
                    section("foo")
                    preamble { }
                }
            }
        """.trimIndent()
        code.shouldNotCompile()
    }

    "document in preamble should not compile" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                    document { }
                }
                document {
                    section("foo")
                }
            }
        """.trimIndent()
        code.shouldNotCompile()
    }

    "document in document should not compile" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                }
                document {
                    section("foo")
                    document { }
                }
            }
        """.trimIndent()
        code.shouldNotCompile()
    }

    "document in document should compile if 'this' is specified explicitly" {
        val code = """
            import com.github.shwaka.kohomology.tex.TexArticle
            val texArticle = TexArticle {
                preamble {
                    documentclass("article")
                }
                document {
                    section("foo")
                    this@TexArticle.document { }
                }
            }
        """.trimIndent()
        code.shouldCompile()
    }
})
