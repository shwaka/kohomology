package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.PrintType

internal data class Power<I : IndeterminateName>(
    val indeterminateName: I,
    val exponent: Int,
) {
    fun toString(
        printType: PrintType,
        indeterminateNameToString: (IndeterminateName) -> String,
    ): String {
        return when (exponent) {
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
    return powerList
        .filter { (_, exponent: Int) -> exponent != 0 }
        .joinToString(separator) { power -> power.toString(printType, indeterminateNameToString) }
}

internal fun <I : IndeterminateName> toPowerList(word: List<I>): List<Power<I>> {
    if (word.isEmpty()) {
        return emptyList()
    }
    val result = mutableListOf<Power<I>>()
    var previous: I = word.first()
    var count = 1
    for (i in 1..(word.size)) {
        val current: I? = word.getOrNull(i)
        if (current == previous) {
            count++
        } else {
            result.add(Power(previous, count))
            if (current != null) {
                previous = current
                count = 1
            }
        }
    }
    return result
}
