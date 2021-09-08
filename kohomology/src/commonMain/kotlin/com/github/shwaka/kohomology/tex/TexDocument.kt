package com.github.shwaka.kohomology.tex

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
