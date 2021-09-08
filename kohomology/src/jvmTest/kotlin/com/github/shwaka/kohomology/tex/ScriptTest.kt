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

    "addLines(str1, str2) should work" {
        val script = Script {
            addLines("foo", "bar", "baz")
        }
        script.toString() shouldBe """
            |foo
            |bar
            |baz
        """.trimMargin()
    }

    "addLines(script) should work" {
        val script1 = Script {
            addLines("foo1")
            val script2 = Script {
                addLines("foo2")
                addLines("bar2")
            }
            addScript(script2)
            addLines("bar1")
        }
        script1.toString() shouldBe """
            |foo1
            |foo2
            |bar2
            |bar1
        """.trimMargin()
    }

    "addLines(listOfScripts) should work" {
        val script = Script {
            addLines("foo")
            val script1 = Script {
                addLines("foo1")
                addLines("bar1")
            }
            val script2 = Script {
                addLines("foo2")
            }
            addScript(listOf(script1, script2))
            addLines("bar")
        }
        script.toString() shouldBe """
            |foo
            |foo1
            |bar1
            |foo2
            |bar
        """.trimMargin()
    }

    "addLines(script1, script2) should work" {
        val script = Script {
            addLines("foo")
            val script1 = Script {
                addLines("foo1")
                addLines("bar1")
            }
            val script2 = Script {
                addLines("foo2")
            }
            addScript(script1, script2)
            addLines("bar")
        }
        script.toString() shouldBe """
            |foo
            |foo1
            |bar1
            |foo2
            |bar
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
