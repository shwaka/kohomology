package com.github.shwaka.kohomology.tex

public interface TexScriptInterface<T : ScriptInterface<T>> : ScriptInterface<T> {
    private companion object {
        fun newcommandBase(
            newcommandType: String,
            name: String,
            value: String,
            numArgs: Int = 0,
            defaultArg: String? = null,
        ): String {
            val nameWithBackslash = if (name.startsWith("\\")) name else "\\$name"
            val numArgsString: String = when {
                (numArgs == 0) -> ""
                (numArgs > 0) -> "[$numArgs]"
                else -> throw Exception("numArgs must be non-negative, but $numArgs is given")
            }
            val defaultArgString = if (defaultArg == null) "" else "[$defaultArg]"
            return "\\$newcommandType{$nameWithBackslash}$numArgsString$defaultArgString{$value}"
        }
    }

    public fun newcommand(name: String, value: String, numArgs: Int = 0, defaultArg: String? = null) {
        this.addLines(TexScriptInterface.newcommandBase("newcommand", name, value, numArgs, defaultArg))
    }
    public fun newcommandStar(name: String, value: String, numArgs: Int = 0, defaultArg: String? = null) {
        this.addLines(TexScriptInterface.newcommandBase("newcommand*", name, value, numArgs, defaultArg))
    }

    public fun renewcommand(name: String, value: String, numArgs: Int = 0, defaultArg: String? = null) {
        this.addLines(TexScriptInterface.newcommandBase("renewcommand", name, value, numArgs, defaultArg))
    }
    public fun renewcommandStar(name: String, value: String, numArgs: Int = 0, defaultArg: String? = null) {
        this.addLines(TexScriptInterface.newcommandBase("renewcommand*", name, value, numArgs, defaultArg))
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
