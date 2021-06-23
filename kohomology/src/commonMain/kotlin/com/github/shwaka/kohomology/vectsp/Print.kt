package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Scalar

enum class PrintType {
    PLAIN, TEX
}

interface Printable {
    fun toString(printType: PrintType): String
}

class Printer private constructor(
    private val printType: PrintType,
    private val value: String,
) {
    constructor(type: PrintType) : this(type, "")

    override fun toString(): String {
        return this.value
    }
    operator fun plus(str: String): Printer {
        val value = this.value + str
        return Printer(this.printType, value)
    }
    operator fun plus(printable: Printable): Printer {
        val value = this.value + printable.toString(this.printType)
        return Printer(this.printType, value)
    }
}

data class InternalPrintConfig<B : BasisName, S : Scalar>(
    val beforeSign: String = " ",
    val afterSign: String = " ",
    val afterCoeff: String = " ",
    val coeffToString: (S) -> String = { it.toString() },
    val coeffToStringWithoutSign: (S) -> String = { it.toStringWithoutSign() },
    val basisToString: (B) -> String = { it.toString() },
    val basisComparator: Comparator<B>? = null,
) {
    companion object {
        fun <B : BasisName, S : Scalar> plain(): InternalPrintConfig<B, S> = InternalPrintConfig()
        fun <B : BasisName, S : Scalar> tex(): InternalPrintConfig<B, S> = InternalPrintConfig(
            coeffToString = { it.toTex() },
            coeffToStringWithoutSign = { it.toTexWithoutSign() },
            basisToString = { it.toTex() },
        )
        fun <B : BasisName, S : Scalar> default(printType: PrintType): InternalPrintConfig<B, S> {
            return when (printType) {
                PrintType.PLAIN -> InternalPrintConfig.plain()
                PrintType.TEX -> InternalPrintConfig.tex()
            }
        }
    }
}
