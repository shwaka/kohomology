package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.lexer.Token

internal class CharCategoryToken(
    name: String?,
    private val beginningCharCategoryList: List<CharCategory>,
    private val nonBeginningCharCategoryList: List<CharCategory>,
    ignored: Boolean = false,
) : Token(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        if (input[fromIndex].category !in this.beginningCharCategoryList) {
            return 0
        }
        var index = fromIndex + 1
        while (
            (index < input.length) &&
            (input[index].category in this.nonBeginningCharCategoryList)
        ) {
            index++
        }
        return index - fromIndex
    }

    override fun toString(): String {
        return "${name ?: ""} ($beginningCharCategoryList, $nonBeginningCharCategoryList)" +
            if (ignored) " [ignorable]" else ""
    }
}

internal fun charCategoryToken(
    beginningCharCategoryList: List<CharCategory>,
    nonBeginningCharCategoryList: List<CharCategory>,
): CharCategoryToken {
    return CharCategoryToken(null, beginningCharCategoryList, nonBeginningCharCategoryList)
}
