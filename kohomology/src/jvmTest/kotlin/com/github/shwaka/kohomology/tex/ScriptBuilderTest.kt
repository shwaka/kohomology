package com.github.shwaka.kohomology.tex

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val scriptBuilderTag = NamedTag("ScriptBuilder")

class ScriptBuilderTest : FreeSpec({
    tags(scriptBuilderTag)

    "newline should be added between lines" {
        val scriptBuilder = ScriptBuilder().apply {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe "foo\nbar"
    }

    "linePrefix should be added" {
        val scriptBuilder = ScriptBuilder(linePrefix = "  ").apply {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe "  foo\n  bar"
    }

    "withLinePrefix() should work" {
        val scriptBuilder = ScriptBuilder().apply {
            withLinePrefix("% ") {
                addLines("foo")
                addLines("bar")
            }
        }
        scriptBuilder.toString() shouldBe "% foo\n% bar"
    }
})
