package com.github.shwaka.kohomology.tex

@TexArticleDslMarker
public class TexArticle {
    public companion object {
        public operator fun invoke(block: TexArticle.() -> Unit): TexArticle {
            return TexArticle().apply(block)
        }
    }

    private val preamble = TexPreamble()
    private val document = TexDocument()

    public fun preamble(block: TexPreamble.() -> Unit) {
        this.preamble.block()
    }

    public fun document(block: TexDocument.() -> Unit) {
        this.document.block()
    }

    override fun toString(): String {
        val stringList = this.preamble.toStringList() +
            listOf("", "\\begin{document}") +
            this.document.toStringList() +
            listOf("\\end{document}")
        return stringList.joinToString("\n")
    }
}
