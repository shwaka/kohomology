package com.github.shwaka.kohomology.tex

public interface TexPreambleInterface<T : TexScriptInterface<T>> : TexScriptInterface<T> {
    public fun documentclass(name: String, options: List<String> = emptyList()) {
        this.simpleCommand("documentclass", name, options)
    }
    public fun documentclass(name: String, option: String) {
        this.documentclass(name, listOf(option))
    }

    public fun usepackage(name: String, options: List<String> = emptyList()) {
        this.simpleCommand("usepackage", name, options)
    }
    public fun usepackage(name: String, option: String) {
        this.usepackage(name, listOf(option))
    }
}

public class TexPreamble(linePrefix: String = "") :
    ScriptBase<TexPreamble>(linePrefix),
    TexPreambleInterface<TexPreamble> {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: TexPreamble.() -> Unit): TexPreamble {
            return TexPreamble(linePrefix).apply(block)
        }
    }

    override fun newScript(linePrefix: String): TexPreamble {
        return TexPreamble(linePrefix)
    }
}
