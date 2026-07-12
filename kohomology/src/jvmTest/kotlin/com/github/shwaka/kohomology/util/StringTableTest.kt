package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class StringTableTest : FreeSpec({
    "toString for empty table" {
        val table = StringTable(emptyList())

        table.toString() shouldBe "[  ]"
    }

    "toPrettyString for empty table" {
        val table = StringTable(emptyList())

        table.toPrettyString() shouldBe "[ ]"
    }

    "toString for multiple rows" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val table = StringTable(data)

        table.toString() shouldBe "[ [1, 22, 333] [44, 5, 6] ]"
    }

    "toPrettyString for one row" {
        val data = listOf(
            listOf("1", "22", "333"),
        )
        val table = StringTable(data)

        table.toPrettyString() shouldBe "[ 1 22 333 ]"
    }

    "toPrettyString for multiple rows" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val table = StringTable(data)
        val expected = """
            |⎡  1 22 333 ⎤
            |⎣ 44  5   6 ⎦
        """.trimMargin()

        table.toPrettyString() shouldBe expected
    }

    "toPrettyString with custom separator and paren" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val paren = Paren(separator = ",")
        val table = StringTable(data, paren)
        val expected = """
            |[  1,22,333 ]
            |[ 44, 5,  6 ]
        """.trimMargin()

        table.toPrettyString() shouldBe expected
    }

    "non-rectangle table should throw IllegalArgumentException" {
        val data = listOf(
            listOf("1", "2"),
            listOf("3"),
        )

        shouldThrow<IllegalArgumentException> {
            StringTable(data)
        }
    }
})
