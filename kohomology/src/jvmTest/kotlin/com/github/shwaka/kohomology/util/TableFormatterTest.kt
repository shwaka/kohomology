package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TableFormatterTest : FreeSpec({
    "formatRows should align columns to the right by default" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val formatter = TableFormatter(data, separator = ",")

        formatter.formatRows() shouldBe listOf(
            " 1,22,333",
            "44, 5,  6",
        )
    }

    "formatPlain should join formatted rows with newline" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val formatter = TableFormatter(data, separator = ",")
        val expected = """
            | 1,22,333
            |44, 5,  6
        """.trimMargin()

        formatter.formatPlain() shouldBe expected
    }

    "formatRows should support column alignments" {
        val data = listOf(
            listOf("1", "22", "333"),
            listOf("44", "5", "6"),
        )
        val formatter = TableFormatter(
            data = data,
            separator = ",",
            alignments = listOf(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT),
        )

        formatter.formatRows() shouldBe listOf(
            "1 ,22,333",
            "44, 5,6  ",
        )
    }

    "empty table should be formatted as empty rows and empty plain text" {
        val formatter = TableFormatter(emptyList())

        formatter.formatRows() shouldBe emptyList()
        formatter.formatPlain() shouldBe ""
    }

    "table with empty rows should be formatted as empty strings" {
        val data = listOf(
            emptyList<String>(),
            emptyList(),
            emptyList(),
        )
        val formatter = TableFormatter(data)

        formatter.formatRows() shouldBe listOf("", "", "")
        formatter.formatPlain() shouldBe "\n\n"
    }

    "non-rectangle table should throw IllegalArgumentException" {
        val data = listOf(
            listOf("1", "2"),
            listOf("3"),
        )

        shouldThrow<IllegalArgumentException> {
            TableFormatter(data)
        }
    }

    "alignments size should be equal to the number of columns" {
        val data = listOf(
            listOf("1", "2"),
            listOf("3", "4"),
        )

        shouldThrow<IllegalArgumentException> {
            TableFormatter(data, alignments = listOf(Alignment.RIGHT))
        }
    }
})
