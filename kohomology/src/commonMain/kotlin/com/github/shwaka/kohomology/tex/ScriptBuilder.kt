package com.github.shwaka.kohomology.tex

public interface Builder<T : Builder<T>> {
    public fun addLines(lines: List<String>)
    public fun toStringList(): List<String>
    public fun newBuilder(linePrefix: String): T

    public fun addLines(lines: String) {
        this.addLines(lines.split("\n"))
    }

    public fun addLines(builder: Builder<*>) {
        this.addLines(builder.toStringList())
    }

    public fun withLinePrefix(linePrefix: String, block: T.() -> Unit) {
        val builder = this.newBuilder(linePrefix).apply(block)
        this.addLines(builder)
    }

    public fun withIndent(indent: Int, block: T.() -> Unit) {
        val linePrefix = " ".repeat(indent)
        this.withLinePrefix(linePrefix, block)
    }
}

public abstract class ScriptBuilderBase<T : ScriptBuilderBase<T>>(
    public val linePrefix: String = "",
    lines: MutableList<String> = mutableListOf(),
) : Builder<T> {
    private val lines: MutableList<String> = lines.toMutableList() // copy

    abstract override fun newBuilder(linePrefix: String): T

    override fun addLines(lines: List<String>) {
        this.lines.addAll(lines)
    }

    override fun toStringList(): List<String> {
        return this.lines.map { this.linePrefix + it }.toList()
    }

    override fun toString(): String {
        return this.toStringList().joinToString("\n")
    }
}

public class ScriptBuilder(
    linePrefix: String = "",
    lines: MutableList<String> = mutableListOf()
) : ScriptBuilderBase<ScriptBuilder>(linePrefix, lines) {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: ScriptBuilder.() -> Unit): ScriptBuilder {
            return ScriptBuilder(linePrefix).apply(block)
        }
    }

    override fun newBuilder(linePrefix: String): ScriptBuilder {
        return ScriptBuilder(linePrefix)
    }
}
