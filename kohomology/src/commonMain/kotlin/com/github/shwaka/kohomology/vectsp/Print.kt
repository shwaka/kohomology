package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Scalar

enum class PrintType {
    PLAIN, TEX
}

interface Printable {
    fun toString(type: PrintType): String
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
)
