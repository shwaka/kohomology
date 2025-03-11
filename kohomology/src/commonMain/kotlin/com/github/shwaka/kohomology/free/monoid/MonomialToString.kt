package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.util.PrintType

// Previously, this was an inline method and indeterminateNameToString was crossinline.
// They are removed since they caused an error in tests (bug in JUnit?).
internal fun <I : IndeterminateName> monomialToString(
    indeterminateAndExponentList: List<Pair<I, Int>>,
    printType: PrintType,
    indeterminateNameToString: (IndeterminateName) -> String,
): String {
    if (indeterminateAndExponentList.isEmpty())
        return "1"
    val separator = when (printType) {
        PrintType.PLAIN, PrintType.TEX -> ""
        PrintType.CODE -> " * "
    }
    return indeterminateAndExponentList.joinToString(separator) { (indeterminateName, exponent) ->
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
