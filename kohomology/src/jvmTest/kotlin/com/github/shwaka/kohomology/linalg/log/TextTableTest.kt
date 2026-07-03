package com.github.shwaka.kohomology.linalg.log

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TextTableTest : FreeSpec({
    "table of 1-digit integers" {
        val data = listOf(
            listOf(1, 2, 3).map { it.toString() },
            listOf(4, 5, 6).map { it.toString() },
        )
        val table = RawTextTable(data, separator = ",")
        val expected = """
            |1,2,3
            |4,5,6
        """.trimMargin()
        table.toPrettyString() shouldBe expected
    }

    "table of different digit integers, sameWidth=true" {
        val data = listOf(
            listOf(1, 22, 333).map { it.toString() },
            listOf(44, 5, 6).map { it.toString() },
        )
        val table = RawTextTable(data, separator = ",", sameWidth = true)
        val expected = """
            |  1, 22,333
            | 44,  5,  6
        """.trimMargin()
        table.toPrettyString() shouldBe expected
    }

    "table of different digit integers, sameWidth=false" {
        val data = listOf(
            listOf(1, 22, 333).map { it.toString() },
            listOf(44, 5, 6).map { it.toString() },
        )
        val table = RawTextTable(data, separator = ",", sameWidth = false)
        val expected = """
            | 1,22,333
            |44, 5,  6
        """.trimMargin()
        table.toPrettyString() shouldBe expected
    }
})
