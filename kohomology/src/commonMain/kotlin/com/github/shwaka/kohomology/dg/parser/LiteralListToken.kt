package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.lexer.Token

internal class LiteralListToken(
    name: String?,
    private val textList: List<String>,
    ignored: Boolean = false
) : Token(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        for (text in this.textList) {
            if (input.startsWith(text, fromIndex))
                return text.length
        }
        return 0
    }

    override fun toString(): String {
        return "${name ?: ""} ($textList)" + if (ignored) " [ignorable]" else ""
    }
}

internal fun literalListToken(
    textList: List<String>,
    ignore: Boolean = false
): LiteralListToken {
    return LiteralListToken(null, textList, ignore)
}
