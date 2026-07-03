package com.github.shwaka.kohomology.linalg.log

private fun <T> Collection<T>.allElementsAreSame(): Boolean {
    if (this.isEmpty()) {
        return true
    }
    val value: T = this.first()
    return this.all { it == value }
}

private fun <T> Collection<Collection<T>>.assertRectangle(): Boolean {
    if (this.isEmpty()) {
        return true
    }
    return this.map { row -> row.size }.allElementsAreSame()
}

internal class RawTextTable(
    val data: List<List<String>>,
    val separator: String = " ",
    sameWidth: Boolean = true,
) {
    init {
        require(this.data.assertRectangle()) { "Non-rectangle" }
    }

    private val colLengthList: List<Int> = if (data.isEmpty()) {
        emptyList()
    } else if (sameWidth) {
        val length = data.flatMap { row -> row.map { it.length } }.max()
        List(data[0].size) { length }
    } else {
        data[0].indices.map { j ->
            data.map { row -> row[j] }.maxOf { it.length }
        }
    }

    fun toPrettyString(): String {
        if (this.data.isEmpty()) {
            // 下で this.data[0] とするので、ここの分岐は必要
            return ""
        }
        val rowStringList: List<String> = this.data.map { row ->
            row.zip(this.colLengthList).joinToString(this.separator) { (elm, length) ->
                elm.padStart(length)
            }
        }
        return rowStringList.joinToString("\n")
    }
}

internal class TextTable(
    val data: List<List<String>>,
    val rowLabel: String,
    val colLabel: String,
    val sameWidth: Boolean,
) {
    init {
        require(this.data.assertRectangle()) { "Non-rectangle" }
    }
    val rowCount: Int = data.size
    val colCount: Int = if (data.isEmpty()) {
        0
    } else {
        data[0].size
    }

    fun toPrettyString(): String {
        val dataWithLabel: List<List<String>> =
            listOf(
                listOf("") + (0 until this.colCount).map { j -> "${this.colLabel}=$j" }
            ) + (0 until this.rowCount).map { i ->
                listOf("${this.rowLabel}=$i") + this.data[i]
            }
        val rawTextTable = RawTextTable(dataWithLabel, sameWidth = sameWidth)
        return rawTextTable.toPrettyString()
    }
}
