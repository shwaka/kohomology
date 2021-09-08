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

public interface TexDocumentInterface<T : TexScriptInterface<T>> : TexScriptInterface<T> {
    public fun begin(name: String, block: T.() -> Unit) {
        this.addLines("\\begin{$name}")
        this.withIndent(2) {
            block()
        }
        this.addLines("\\end{$name}")
    }
}

public class TexDocument(linePrefix: String = "") :
    ScriptBase<TexDocument>(linePrefix),
    TexDocumentInterface<TexDocument> {
    public companion object {
        public operator fun invoke(linePrefix: String = "", block: TexDocument.() -> Unit): TexDocument {
            return TexDocument(linePrefix).apply(block)
        }
    }

    override fun newScript(linePrefix: String): TexDocument {
        return TexDocument(linePrefix)
    }
}
