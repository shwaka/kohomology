package com.github.shwaka.kohomology.util

internal enum class Alignment {
    LEFT,
    RIGHT,
}

private fun <T> Collection<Collection<T>>.assertRectangle(): Boolean {
    if (this.isEmpty()) {
        return true
    }
    return this.map { row -> row.size }.distinct().size <= 1
}

internal class TableFormatter(
    private val data: List<List<String>>,
    private val separator: String = " ",
    alignments: List<Alignment>? = null,
) {
    init {
        require(this.data.assertRectangle()) { "Non-rectangle" }
    }

    private val colCount: Int = this.data.firstOrNull()?.size ?: 0

    private val alignments: List<Alignment> = alignments ?: List(this.colCount) { Alignment.RIGHT }

    init {
        require(this.alignments.size == this.colCount) {
            "The size of alignments must be equal to the number of columns"
        }
    }

    private val colLengthList: List<Int> = when {
        this.data.isEmpty() -> emptyList()
        else -> this.data[0].indices.map { j ->
            this.data.map { row -> row[j] }.maxOf { it.length }
        }
    }

    fun formatRows(): List<String> {
        return this.data.map { row ->
            row.zip(this.colLengthList).zip(this.alignments)
                .joinToString(this.separator) { (entry, alignment) ->
                    val (elm, length) = entry
                    when (alignment) {
                        Alignment.LEFT -> elm.padEnd(length)
                        Alignment.RIGHT -> elm.padStart(length)
                    }
                }
        }
    }

    fun formatPlain(): String {
        return this.formatRows().joinToString("\n")
    }
}
