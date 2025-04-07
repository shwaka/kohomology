package com.github.shwaka.kohomology.util

/**
 * Determines how to print [com.github.shwaka.kohomology.model.CopiedName].
 */
public enum class ShowShift {
    BAR, S, S_WITH_DEGREE
}

public data class PrintConfig(
    val printType: PrintType = PrintType.PLAIN,
    val beforeSign: String = " ",
    val afterSign: String = " ",
    val afterCoeff: String = " ",
    val showShift: ShowShift = ShowShift.S_WITH_DEGREE,
) {
    public companion object {
        public val default: PrintConfig = PrintConfig(PrintType.PLAIN)
    }
}
