package com.github.shwaka.kohomology.tex

public interface ScriptInterface<T : ScriptInterface<T>> {
    public fun addLines(lines: List<String>)
    public fun toStringList(): List<String>
    public fun newScript(linePrefix: String): T

    public fun addLines(lines: String) {
        this.addLines(lines.split("\n"))
    }
    public fun addLines(vararg lines: String) {
        this.addLines(lines.toList())
    }

    public fun addScript(script: ScriptInterface<*>) {
        this.addLines(script.toStringList())
    }
    public fun addScript(scripts: List<ScriptInterface<*>>) {
        for (script in scripts) {
            this.addScript(script)
        }
    }
    public fun addScript(vararg scripts: ScriptInterface<*>) {
        this.addScript(scripts.toList())
    }

    public fun withLinePrefix(linePrefix: String, block: T.() -> Unit) {
        val builder = this.newScript(linePrefix).apply(block)
        this.addScript(builder)
    }

    public fun withIndent(indent: Int, block: T.() -> Unit) {
        val linePrefix = " ".repeat(indent)
        this.withLinePrefix(linePrefix, block)
    }
}

public abstract class ScriptBase<T : ScriptBase<T>>(
    public val linePrefix: String = ""
) : ScriptInterface<T> {
    private val lines: MutableList<String> = mutableListOf()

    abstract override fun newScript(linePrefix: String): T

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

public class Script(linePrefix: String = "") : ScriptBase<Script>(linePrefix) {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: Script.() -> Unit): Script {
            return Script(linePrefix).apply(block)
        }
    }

    override fun newScript(linePrefix: String): Script {
        return Script(linePrefix)
    }
}
