import com.github.h0tk3y.betterParse.parser.AlternativesFailure
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.NoMatchingToken
import com.github.h0tk3y.betterParse.parser.ParseException

private fun ErrorResult.isFailureAtTheBeginning(): Boolean {
    return when (this) {
        is AlternativesFailure -> this.errors.all { it.isFailureAtTheBeginning() }
        is NoMatchingToken -> this.tokenMismatch.offset == 0
        else -> false
    }
}

fun ParseException.isFailureAtTheBeginning(): Boolean {
    return this.errorResult.isFailureAtTheBeginning()
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

fun ParseException.format(): String {
    return this.errorResult.format()
}
