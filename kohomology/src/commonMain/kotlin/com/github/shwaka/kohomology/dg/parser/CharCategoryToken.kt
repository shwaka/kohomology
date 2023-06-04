package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.lexer.Token

internal class CharCategoryToken(
    name: String?,
    private val firstCharCategoryList: List<CharCategory>,
    private val nonFirstCharCategoryList: List<CharCategory>,
    ignored: Boolean = false,
) : Token(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        if (input[fromIndex].category !in this.firstCharCategoryList) {
            return 0
        }
        var index = fromIndex + 1
        while (
            (index < input.length) &&
            (input[index].category in this.nonFirstCharCategoryList)
        ) {
            index++
        }
        return index - fromIndex
    }

    override fun toString(): String {
        return "${name ?: ""} ($firstCharCategoryList, $nonFirstCharCategoryList)" +
            if (ignored) " [ignorable]" else ""
    }
}

internal fun charCategoryToken(
    firstCharCategoryList: List<CharCategory>,
    nonFirstCharCategoryList: List<CharCategory>,
): CharCategoryToken {
    return CharCategoryToken(null, firstCharCategoryList, nonFirstCharCategoryList)
}
