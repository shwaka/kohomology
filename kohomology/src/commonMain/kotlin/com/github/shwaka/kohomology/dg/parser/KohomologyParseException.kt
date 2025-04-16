package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.parser.AlternativesFailure
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.NoMatchingToken
import com.github.h0tk3y.betterParse.parser.ParseException

public class KohomologyParseException(
    private val parseException: ParseException,
) : Exception(parseException.message, parseException.cause) {
    public fun getErrorResult(): String {
        return this.parseException.errorResult.toString()
    }

    public fun isFailureAtTheBeginning(): Boolean {
        return this.parseException.errorResult.isFailureAtTheBeginning()
    }

    public fun format(): String {
        return this.parseException.errorResult.format()
    }
}

private fun ErrorResult.isFailureAtTheBeginning(): Boolean {
    return when (this) {
        is AlternativesFailure -> this.errors.all { it.isFailureAtTheBeginning() }
        is NoMatchingToken -> this.tokenMismatch.offset == 0
        else -> false
    }
}

private const val indent = "  "

private fun ErrorResult.format(): String {
    return when (this) {
        is AlternativesFailure -> {
            "AlternativesFailure(errors=[\n" +
                this.errors.joinToString(",\n") { it.format().prependIndent(indent) } +
                "\n])"
        }
        else -> this.toString()
    }
}
