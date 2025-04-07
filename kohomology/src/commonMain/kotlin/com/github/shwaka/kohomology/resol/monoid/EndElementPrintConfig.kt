package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfigDefaultRegistry
import com.github.shwaka.kohomology.util.PrintConfigEntry

public sealed interface EndElementFormat {
    public object Raw : EndElementFormat
    public data class Indexed(public val symbol: String) : EndElementFormat
}

public data class EndElementPrintConfig(
    public val format: EndElementFormat = EndElementFormat.Indexed("f"),
) : PrintConfigEntry {
    public companion object {
        public fun registerDefault() {
            PrintConfigDefaultRegistry.registerDefault(EndElementPrintConfig())
        }
    }
}
