package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Scalar

enum class PrintType {
    PLAIN, TEX
}

interface Printable {
    fun toString(printType: PrintType): String
}

class Printer(
    private val type: PrintType,
    private val value: String = "",
) {
    override fun toString(): String {
        return this.value
    }
    operator fun plus(str: String): Printer {
        val value = this.value + str
        return Printer(this.type, value)
    }
    operator fun plus(printable: Printable): Printer {
        val value = this.value + printable.toString(this.type)
        return Printer(this.type, value)
    }
}

data class PrintConfig<B : BasisName, S : Scalar>(
    val beforeSign: String = " ",
    val afterSign: String = " ",
    val afterCoeff: String = " ",
    val coeffToString: (S) -> String = { it.toString() },
    val coeffToStringWithoutSign: (S) -> String = { it.toStringWithoutSign() },
    val basisToString: (B) -> String = { it.toString() },
    val basisComparator: Comparator<B>? = null,
) {
    companion object {
        fun <B : BasisName, S : Scalar> plain(): PrintConfig<B, S> = PrintConfig()
        fun <B : BasisName, S : Scalar> tex(): PrintConfig<B, S> = PrintConfig(
            coeffToString = { it.toTex() },
            coeffToStringWithoutSign = { it.toTexWithoutSign() },
            basisToString = { it.toTex() },
        )
        fun <B : BasisName, S : Scalar> default(printType: PrintType): PrintConfig<B, S> {
            return when (printType) {
                PrintType.PLAIN -> PrintConfig.plain()
                PrintType.TEX -> PrintConfig.tex()
            }
        }
    }
}
