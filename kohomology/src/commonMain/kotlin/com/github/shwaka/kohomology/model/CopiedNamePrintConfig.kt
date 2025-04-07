package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.util.PrintConfigDefaultRegistry
import com.github.shwaka.kohomology.util.PrintConfigEntry

/**
 * Determines how to print [com.github.shwaka.kohomology.model.CopiedName].
 */
public enum class ShowShift {
    BAR, S, S_WITH_DEGREE
}

public data class CopiedNamePrintConfig(
    val showShift: ShowShift = ShowShift.S_WITH_DEGREE
) : PrintConfigEntry {
    public companion object {
        public fun registerDefault() {
            PrintConfigDefaultRegistry.registerDefault(CopiedNamePrintConfig())
        }
    }
}
