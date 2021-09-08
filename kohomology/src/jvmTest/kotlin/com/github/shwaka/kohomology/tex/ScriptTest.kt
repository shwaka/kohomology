package com.github.shwaka.kohomology.tex

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val scriptTag = NamedTag("Script")

class ScriptTest : FreeSpec({
    tags(scriptTag)

    "newline should be added between lines" {
        val scriptBuilder = Script {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe """
            |foo
            |bar
        """.trimMargin()
    }

    "linePrefix should be added" {
        val scriptBuilder = Script(linePrefix = "  ") {
            addLines("foo")
            addLines("bar")
        }
        scriptBuilder.toString() shouldBe """
            |  foo
            |  bar
        """.trimMargin()
    }

    "addLines(multiLineString) should be split by newlines" {
        val scriptBuilder = Script(linePrefix = " ") {
            addLines("foo\nbar\nbaz")
        }
        scriptBuilder.toString() shouldBe """
            | foo
            | bar
            | baz
        """.trimMargin()
    }

    "addLines(listOfString) should work" {
        val scriptBuilder = Script {
            addLines(listOf("foo", "bar", "baz"))
        }
        scriptBuilder.toString() shouldBe """
            |foo
            |bar
            |baz
        """.trimMargin()
    }

    "addLines(scriptBuilder) should work" {
        val scriptBuilder1 = Script {
            addLines("foo1")
            val scriptBuilder2 = Script {
                addLines("foo2")
                addLines("bar2")
            }
            addLines(scriptBuilder2)
            addLines("bar1")
        }
        scriptBuilder1.toString() shouldBe """
            |foo1
            |foo2
            |bar2
            |bar1
        """.trimMargin()
    }

    "withLinePrefix() should work" {
        val scriptBuilder = Script {
            withLinePrefix("% ") {
                addLines("foo")
                addLines("bar")
            }
        }
        scriptBuilder.toString() shouldBe """
            |% foo
            |% bar
        """.trimMargin()
    }

    "withIndent() should work" {
        val scriptBuilder = Script {
            withIndent(2) {
                addLines("foo")
                addLines("bar")
            }
        }
        scriptBuilder.toString() shouldBe """
            |  foo
            |  bar
        """.trimMargin()
    }

    "nested withLinePrefix() should nest prefixes" {
        val scriptBuilder = Script {
            addLines("a")
            withLinePrefix("%") {
                addLines("b")
                withLinePrefix(".") {
                    addLines("c")
                }
                addLines("d")
            }
            addLines("e")
        }
        scriptBuilder.toString() shouldBe """
            |a
            |%b
            |%.c
            |%d
            |e
        """.trimMargin()
    }

    "trailing newline should be preserved" {
        val scriptBuilder = Script {
            addLines("foo\nbar\nbaz\n")
        }
        scriptBuilder.toString() shouldBe """
            |foo
            |bar
            |baz
            |
        """.trimMargin()
    }

    "trailing newline should be preserved with prefix added" {
        // TODO: Is this behavior "good"?
        val scriptBuilder = Script {
            withLinePrefix(".") {
                addLines("foo\nbar\nbaz\n")
            }
        }
        scriptBuilder.toString() shouldBe """
            |.foo
            |.bar
            |.baz
            |.
        """.trimMargin()
    }

    "lines should be copied in addLines(lines)" {
        val lines = mutableListOf("foo", "bar")
        val scriptBuilder = Script().apply {
            addLines(lines)
            addLines("baz")
        }
        scriptBuilder.toString() shouldBe """
            |foo
            |bar
            |baz
        """.trimMargin()
        lines shouldBe mutableListOf("foo", "bar")
    }
})
