package com.github.shwaka.kohomology.vectsp

enum class PrintType {
    PLAIN, TEX
}

interface PrintConfig

interface Printable {
    fun toString(type: PrintType, config: PrintConfig? = null): String
    fun acceptConfig(config: PrintConfig): Boolean
}

class Printer(
    private val type: PrintType,
    private val value: String = "",
    private val configList: List<PrintConfig> = emptyList(),
) {
    override fun toString(): String {
        return this.value
    }
    operator fun plus(str: String): Printer {
        val value = this.value + str
        return Printer(this.type, value, this.configList)
    }
    operator fun plus(printable: Printable): Printer {
        val config = this.filterConfig(printable)
        val value = this.value + printable.toString(this.type, config)
        return Printer(this.type, value, this.configList)
    }
    private fun filterConfig(printable: Printable): PrintConfig? {
        return this.configList.filter { printable.acceptConfig(it) }.let { filteredList ->
            when (filteredList.size) {
                0 -> null
                1 -> filteredList[0]
                else -> throw Exception("More than 2 config's are found")
            }
        }
    }
}
