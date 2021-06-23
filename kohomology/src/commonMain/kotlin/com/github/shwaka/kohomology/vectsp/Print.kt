package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Scalar

enum class PrintType {
    PLAIN, TEX
}

interface Printable {
    // In most cases, printConfig.printType is sufficient.
    // Other properties are used in SubQuotVectorSpace.
    fun toString(printConfig: PrintConfig): String
}

class Printer private constructor(
    private val printConfig: PrintConfig,
    private val value: String,
) {
    constructor(printConfig: PrintConfig) : this(printConfig, "")
    constructor(printType: PrintType) : this(PrintConfig(printType))

    override fun toString(): String {
        return this.value
    }
    operator fun plus(str: String): Printer {
        val value = this.value + str
        return Printer(this.printConfig, value)
    }
    operator fun plus(printable: Printable): Printer {
        val value = this.value + printable.toString(this.printConfig)
        return Printer(this.printConfig, value)
    }
}

data class InternalPrintConfig<B : BasisName, S : Scalar>(
    val coeffToString: (S) -> String = { it.toString() },
    val coeffToStringWithoutSign: (S) -> String = { it.toStringWithoutSign() },
    val basisToString: (B) -> String = { it.toString() },
    val basisComparator: Comparator<B>? = null,
) {
    companion object {
        // fun <B : BasisName, S : Scalar> plain(): InternalPrintConfig<B, S> = InternalPrintConfig()
        // fun <B : BasisName, S : Scalar> tex(): InternalPrintConfig<B, S> = InternalPrintConfig(
        //     coeffToString = { it.toTex() },
        //     coeffToStringWithoutSign = { it.toTexWithoutSign() },
        //     basisToString = { it.toString(PrintConfig(PrintType.TEX)) },
        // )
        fun <B : BasisName, S : Scalar> default(printConfig: PrintConfig): InternalPrintConfig<B, S> {
            return InternalPrintConfig(
                coeffToString = {
                    when (printConfig.printType) {
                        PrintType.PLAIN -> it.toString()
                        PrintType.TEX -> it.toTex()
                    }
                },
                coeffToStringWithoutSign = {
                    when (printConfig.printType) {
                        PrintType.PLAIN -> it.toStringWithoutSign()
                        PrintType.TEX -> it.toTexWithoutSign()
                    }
                },
                basisToString = { it.toString(printConfig) }
            )
        }
    }
}

data class PrintConfig(
    val printType: PrintType = PrintType.PLAIN,
    val beforeSign: String = " ",
    val afterSign: String = " ",
    val afterCoeff: String = " ",
    val useBar: Boolean = false,
)
