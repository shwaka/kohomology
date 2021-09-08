package com.github.shwaka.kohomology.tex

public interface TexScriptInterface<T : ScriptInterface<T>> : ScriptInterface<T> {
    public fun newcommand(name: String, value: String) {
        val nameWithBackslash = if (name.startsWith("\\")) name else "\\$name"
        this.addLines("\\newcommand{$nameWithBackslash}{$value}")
    }

    public fun comment(commentText: String) {
        this.withLinePrefix("% ") {
            addLines(commentText)
        }
    }

    public fun simpleCommand(controlSequence: String, arg: String, options: List<String> = listOf()) {
        if (options.isEmpty()) {
            this.addLines("\\$controlSequence{$arg}")
        } else {
            val optionsString = options.joinToString(", ")
            this.addLines("\\$controlSequence[$optionsString]{$arg}")
        }
    }

    public fun simpleCommand(controlSequence: String, arg: String, option: String) {
        this.simpleCommand(controlSequence, arg, listOf(option))
    }
}

public class TexScript(linePrefix: String = "") :
    ScriptBase<TexScript>(linePrefix),
    TexScriptInterface<TexScript> {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: TexScript.() -> Unit): TexScript {
            return TexScript(linePrefix).apply(block)
        }
    }

    override fun newScript(linePrefix: String): TexScript {
        return TexScript(linePrefix)
    }
}
