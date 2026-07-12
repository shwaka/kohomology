package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.util.TableFormatter

internal class TextTable(
    data: List<List<String>>,
    separator: String = " ",
) {
    private val formatter: TableFormatter = TableFormatter(data, separator = separator)

    fun toPrettyString(): String {
        return this.formatter.formatPlain()
    }
}
