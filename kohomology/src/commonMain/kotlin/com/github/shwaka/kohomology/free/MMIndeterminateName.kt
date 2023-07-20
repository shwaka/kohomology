package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.util.Identifier
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public enum class MMIndeterminateType {
    COCYCLE, COCHAIN,
}

public data class MMIndeterminateName(
    val degree: Int,
    val index: Int,
    val totalNumberInDegree: Int,
    val type: MMIndeterminateType,
) : IndeterminateName {
    init {
        require(index >= 0) {
            "index must be non-negative, but $index was given"
        }
        require(index < totalNumberInDegree) {
            "index must be less than totalNumberInDegree, " +
                "but the given values were $index and $totalNumberInDegree"
        }
    }

    override val identifier: Identifier
        get() {
            val printConfig = PrintConfig(printType = PrintType.PLAIN)
            return Identifier(this.toString(printConfig))
        }

    override fun toString(printConfig: PrintConfig): String {
        val char = when (this.type) {
            MMIndeterminateType.COCYCLE -> "v"
            MMIndeterminateType.COCHAIN -> "w"
        }
        return when (this.totalNumberInDegree) {
            1 -> when (printConfig.printType) {
                PrintType.PLAIN, PrintType.CODE -> "${char}_${this.degree}"
                PrintType.TEX -> "${char}_{${this.degree}}"
            }
            else -> when (printConfig.printType) {
                PrintType.PLAIN, PrintType.CODE -> "${char}_${this.degree}_${this.index}"
                PrintType.TEX -> "${char}_{${this.degree},${this.index}}"
            }
        }
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }
}
