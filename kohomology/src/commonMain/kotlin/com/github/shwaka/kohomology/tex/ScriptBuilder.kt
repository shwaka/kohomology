package com.github.shwaka.kohomology.tex

public open class ScriptBuilder(
    public val linePrefix: String = "",
    lines: MutableList<String> = mutableListOf(),
) {
    private val lines: MutableList<String> = lines.toMutableList() // copy

    public companion object {
        public operator fun invoke(linePrefix: String, block: ScriptBuilder.() -> Unit): ScriptBuilder {
            return ScriptBuilder(linePrefix).apply(block)
        }

        public operator fun invoke(block: ScriptBuilder.() -> Unit): ScriptBuilder {
            return ScriptBuilder.invoke("", block)
        }
    }

    public fun addLines(lines: String) {
        this.lines.addAll(lines.split("\n"))
    }

    public fun addLines(lines: List<String>) {
        this.lines.addAll(lines)
    }

    public fun addLines(scriptBuilder: ScriptBuilder) {
        this.addLines(scriptBuilder.toStringList())
    }

    public fun withLinePrefix(linePrefix: String, block: ScriptBuilder.() -> Unit) {
        val scriptBuilder = ScriptBuilder(linePrefix).apply(block)
        this.addLines(scriptBuilder)
    }

    public fun withIndent(indent: Int, block: ScriptBuilder.() -> Unit) {
        val linePrefix = " ".repeat(indent)
        this.withLinePrefix(linePrefix, block)
    }

    override fun toString(): String {
        return this.toStringList().joinToString("\n")
    }

    protected open fun toStringList(): List<String> {
        return this.lines.map { this.linePrefix + it }.toList()
    }
}
