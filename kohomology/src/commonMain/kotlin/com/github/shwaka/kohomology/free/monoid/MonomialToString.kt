package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.PrintType

internal data class Power<I : IndeterminateName>(
    val indeterminateName: I,
    val exponent: Int,
) {
    companion object {
        operator fun invoke(indeterminateName: String, exponent: Int): Power<StringIndeterminateName> {
            return Power(StringIndeterminateName(indeterminateName), exponent)
        }
    }
}

// Previously, this was an inline method and indeterminateNameToString was crossinline.
// They are removed since they caused an error in tests (bug in JUnit?).
internal fun <I : IndeterminateName> monomialToString(
    powerList: List<Power<I>>,
    printType: PrintType,
    indeterminateNameToString: (IndeterminateName) -> String,
): String {
    if (powerList.isEmpty())
        return "1"
    val separator = when (printType) {
        PrintType.PLAIN, PrintType.TEX -> ""
        PrintType.CODE -> " * "
    }
    return powerList.joinToString(separator) { (indeterminateName: I, exponent: Int) ->
        when (exponent) {
            0 -> throw Exception("This can't happen!")
            1 -> indeterminateNameToString(indeterminateName)
            else -> {
                val exponentStr = when (printType) {
                    PrintType.PLAIN, PrintType.CODE -> exponent.toString()
                    PrintType.TEX -> "{$exponent}"
                }
                "${indeterminateNameToString(indeterminateName)}^$exponentStr"
            }
        }
    }
}
