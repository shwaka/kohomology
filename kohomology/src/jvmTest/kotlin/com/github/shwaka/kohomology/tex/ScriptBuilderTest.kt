package com.github.shwaka.kohomology.tex

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val scriptBuilderTag = NamedTag("ScriptBuilder")

class ScriptBuilderTest : FreeSpec({
    tags(scriptBuilderTag)

    "newline should be added between lines" {
        val scriptBuilder = ScriptBuilder {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe "foo\nbar"
    }

    "linePrefix should be added" {
        val scriptBuilder = ScriptBuilder(linePrefix = "  ") {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe "  foo\n  bar"
    }

    "addLines(multiLineString) should be split by newlines" {
        val scriptBuilder = ScriptBuilder(linePrefix = " ") {
            addLines("foo\nbar\nbaz")
        }
        scriptBuilder.toString() shouldBe " foo\n bar\n baz"
    }

    "withLinePrefix() should work" {
        val scriptBuilder = ScriptBuilder {
            withLinePrefix("% ") {
                addLines("foo")
                addLines("bar")
            }
        }
        scriptBuilder.toString() shouldBe "% foo\n% bar"
    }

    "nested withLinePrefix() should nest prefixes" {
        val scriptBuilder = ScriptBuilder {
            addLines("foo")
            withLinePrefix("%") {
                addLines("bar")
                withLinePrefix(".") {
                    addLines("baz")
                }
            }
        }
        scriptBuilder.toString() shouldBe "foo\n%bar\n%.baz"
    }
})
