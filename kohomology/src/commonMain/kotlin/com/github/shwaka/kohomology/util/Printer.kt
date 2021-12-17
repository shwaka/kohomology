package com.github.shwaka.kohomology.util

import com.github.shwaka.kohomology.vectsp.BasisName

public enum class PrintType {
    PLAIN, TEX
}

public interface Printable {
    // In most cases, printConfig.printType is sufficient.
    // Other properties are used in SubQuotVectorSpace.
    public fun toString(printConfig: PrintConfig): String
}

public class Printer private constructor(
    private val printConfig: PrintConfig,
    private val value: String,
) {
    public constructor(printConfig: PrintConfig) : this(printConfig, "")
    public constructor(printType: PrintType) : this(PrintConfig(printType))

    override fun toString(): String {
        return this.value
    }
    public operator fun plus(str: String): Printer {
        val value: String = this.value + str
        return Printer(this.printConfig, value)
    }
    public operator fun plus(printable: Printable?): Printer {
        val value: String = this(printable)
        return Printer(this.printConfig, value)
    }
    public operator fun invoke(printable: Printable?): String {
        val stringFromPrintable: String = printable?.toString(this.printConfig) ?: "null"
        return this.value + stringFromPrintable
    }
}

/**
 * An interface used in [InternalPrintConfig].
 *
 * This is inherited by the interface [com.github.shwaka.kohomology.linalg.Scalar].
 */
public interface PrintableWithSign : Printable {
    public fun toString(printConfig: PrintConfig, withSign: Boolean): String
    public fun toString(printType: PrintType, withSign: Boolean): String =
        this.toString(PrintConfig(printType), withSign)
    override fun toString(printConfig: PrintConfig): String = this.toString(printConfig, true)
    public fun toString(printType: PrintType): String = this.toString(PrintConfig(printType))
}

public data class InternalPrintConfig<B : BasisName, S : PrintableWithSign>(
    val coeffToString: (S, Boolean) -> String = { coeff, withSign -> coeff.toString(PrintType.PLAIN, withSign) },
    val basisToString: (B) -> String = { it.toString() },
    val basisComparator: Comparator<B>? = null,
) {
    public companion object {
        public fun <B : BasisName, S : PrintableWithSign> default(printConfig: PrintConfig): InternalPrintConfig<B, S> {
            return InternalPrintConfig(
                coeffToString = { coeff, withSign -> coeff.toString(printConfig, withSign) },
                basisToString = { it.toString(printConfig) }
            )
        }
    }
}

/**
 * Determines how to print [com.github.shwaka.kohomology.model.CopiedName].
 */
public enum class UseBar {
    ALWAYS, ONE, NEVER
}

public data class PrintConfig(
    val printType: PrintType = PrintType.PLAIN,
    val beforeSign: String = " ",
    val afterSign: String = " ",
    val afterCoeff: String = " ",
    val useBar: UseBar = UseBar.NEVER,
)
