package com.github.shwaka.kohomology.util

data class Paren(
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

private val OnlyBracket = Paren()
private val PrettyParen = Paren(
    upperLeft = "⎡",
    left = "⎥",
    lowerLeft = "⎣",
    upperRight = "⎤",
    right = "⎥",
    lowerRight = "⎦"
)

class StringTable(val data: List<List<String>>, val paren: Paren = PrettyParen) {
    override fun toString(): String {
        val rowStringList = this.data.map { row -> row.toString() }
        val joinedRowStrings = rowStringList.joinToString(this.paren.separator)
        return "${this.paren.leftOneRow} $joinedRowStrings ${this.paren.rightOneRow}"
    }

    fun toPrettyString(): String {
        if (this.data.size == 0) {
            return "${this.paren.leftOneRow} ${this.paren.rightOneRow}"
        }
        val colLengthList: List<Int> = (0 until this.data[0].size).map { j ->
            this.data.map { row -> row[j] }.maxOf { it.length }
        }
        val rowStringList = this.data.map { row ->
            row.zip(colLengthList).joinToString(this.paren.separator) { (elm, length) -> elm.padStart(length) }
        }
        return this.joinRows(rowStringList)
    }

    private fun joinRows(rows: List<String>): String {
        if (rows.size == 0) {
            return "${this.paren.leftOneRow} ${this.paren.rightOneRow}"
        } else if (rows.size == 1) {
            return "${this.paren.leftOneRow} ${rows[0]} ${this.paren.rightOneRow}"
        } else {
            val firstRow = "${this.paren.upperLeft} ${rows[0]} ${this.paren.upperRight}\n"
            val middleRows = rows.slice(1 until rows.size - 1).map { "${this.paren.left} $it ${this.paren.right}\n" }.joinToString("")
            val lastRow = "${this.paren.lowerLeft} ${rows[rows.size - 1]} ${this.paren.lowerRight}"
            return firstRow + middleRows + lastRow
        }
    }
}
