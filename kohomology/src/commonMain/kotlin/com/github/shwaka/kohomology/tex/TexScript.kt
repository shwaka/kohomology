package com.github.shwaka.kohomology.tex

public class TexScript(linePrefix: String = "") : ScriptBuilder(linePrefix) {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: TexScript.() -> Unit): TexScript {
            return TexScript(linePrefix).apply(block)
        }
    }

    public fun newcommand(name: String, value: String) {
        val nameWithBackslash = if (name.startsWith("\\")) name else "\\$name"
        this.addLines("\\newcommand{$nameWithBackslash}{$value}")
    }

    public fun comment(commentText: String) {
        this.withLinePrefix("% ") {
            addLines(commentText)
        }
    }
}
