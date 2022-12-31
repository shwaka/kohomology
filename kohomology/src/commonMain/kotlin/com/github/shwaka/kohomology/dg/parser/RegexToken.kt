package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.lexer.Language
import com.github.h0tk3y.betterParse.lexer.Token

// RegexToken in better-parse has a bug in JS(IR) compiler in kotlin 1.7.
// See https://github.com/h0tk3y/better-parse/issues/57
// The workaround (disabling minification) didn't work here.
internal class RegexToken private constructor(
    name: String?,
    // private val pattern: String,
    private val regex: Regex,
    ignored: Boolean = false
) : Token(name, ignored) {
    constructor(
        name: String?,
        pattern: String,
        ignored: Boolean = false
    ) : this(name, Regex(pattern), ignored)

    override fun match(input: CharSequence, fromIndex: Int): Int {
        // Bad performance: this will find any match AFTER fromIndex
        // and then check if it starts with fromIndex.
        val matchResult: MatchResult? = this.regex.find(input, fromIndex)
        if (matchResult != null && matchResult.range.first == fromIndex) {
            return matchResult.range.last - fromIndex + 1
        }
        return 0
    }

    override fun toString(): String = "${name ?: ""} [${regex.pattern}]" + if (ignored) " [ignorable]" else ""
}

internal fun regexToken(
    @Language("RegExp", "", "") pattern: String,
    ignore: Boolean = false
): RegexToken {
    return RegexToken(null, pattern, ignore)
}
