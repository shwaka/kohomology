package com.github.shwaka.kohomology.util

public data class Paren(
    val upperLeft: String = "[",
    val left: String = "[",
    val lowerLeft: String = "[",
    val leftOneRow: String = "[",
    val upperRight: String = "]",
    val right: String = "]",
    val lowerRight: String = "]",
    val rightOneRow: String = "]",
    val separator: String = " "
)

// private val OnlyBracket = Paren()
private val PrettyParen = Paren(
    upperLeft = "⎡",
    left = "⎥",
    lowerLeft = "⎣",
    upperRight = "⎤",
    right = "⎥",
    lowerRight = "⎦"
)

public class StringTable(private val data: List<List<String>>, private val paren: Paren = PrettyParen) {
    override fun toString(): String {
        val rowStringList = this.data.map { row -> row.toString() }
        val joinedRowStrings = rowStringList.joinToString(this.paren.separator)
        return "${this.paren.leftOneRow} $joinedRowStrings ${this.paren.rightOneRow}"
    }

    public fun toPrettyString(): String {
        if (this.data.isEmpty()) {
            // 下で this.data[0] とするので、ここの分岐は必要
            return "${this.paren.leftOneRow} ${this.paren.rightOneRow}"
        }
        val colLengthList: List<Int> = (this.data[0].indices).map { j ->
            this.data.map { row -> row[j] }.maxOf { it.length }
        }
        val rowStringList = this.data.map { row ->
            row.zip(colLengthList).joinToString(this.paren.separator) { (elm, length) -> elm.padStart(length) }
        }
        return this.joinRows(rowStringList)
    }

    private fun joinRows(rows: List<String>): String {
        return when (rows.size) {
            0 -> "${this.paren.leftOneRow} ${this.paren.rightOneRow}"
            1 -> "${this.paren.leftOneRow} ${rows[0]} ${this.paren.rightOneRow}"
            else -> {
                val firstRow = "${this.paren.upperLeft} ${rows[0]} ${this.paren.upperRight}\n"
                val middleRows = rows.slice(1 until rows.size - 1)
                    .joinToString("") { "${this.paren.left} $it ${this.paren.right}\n" }
                val lastRow = "${this.paren.lowerLeft} ${rows[rows.size - 1]} ${this.paren.lowerRight}"
                firstRow + middleRows + lastRow
            }
        }
    }
}
