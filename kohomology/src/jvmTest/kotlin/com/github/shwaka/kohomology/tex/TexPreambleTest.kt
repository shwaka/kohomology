package com.github.shwaka.kohomology.tex

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TexPreambleTest : FreeSpec({
    tags(scriptTag)

    "test documentclass() with an option" {
        val texPreamble = TexPreamble {
            documentclass("article", "dvipdfmx")
        }
        texPreamble.toString() shouldBe "\\documentclass[dvipdfmx]{article}"
    }

    "test documentclass() without option" {
        val texPreamble = TexPreamble {
            documentclass("article")
        }
        texPreamble.toString() shouldBe "\\documentclass{article}"
    }

    "test usepackage() with options" {
        val texPreamble = TexPreamble {
            usepackage("geometry", listOf("left=1cm, right=1cm, top=0.5cm, bottom=1cm"))
        }
        texPreamble.toString() shouldBe "\\usepackage[left=1cm, right=1cm, top=0.5cm, bottom=1cm]{geometry}"
    }
})
