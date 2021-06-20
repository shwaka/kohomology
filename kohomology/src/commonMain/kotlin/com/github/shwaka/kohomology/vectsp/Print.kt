package com.github.shwaka.kohomology.vectsp

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
