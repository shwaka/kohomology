object ParenParser {
    const val parenOpen: Char = '('
    const val parenClose: Char = ')'

    private fun findParenClose(text: String, start: Int): Int? {
        require(text[start] == this.parenOpen) {
            "$start-th char of \"$text\" must be ${this.parenOpen}, but was ${text[start]}"
        }
        var position: Int = start
        var depth: Int = 0
        while (position < text.length) {
            when (text[position]) {
                this.parenOpen -> depth++
                this.parenClose -> depth--
            }
            if (depth == 0) {
                return position
            }
            position++
        }
        return null
    }

    private fun isSurroundedByParen(text: String): Boolean {
        val positionClose: Int? = this.findParenClose(text, 0)
        return (positionClose == text.length - 1)
    }

    fun removeSurroundingParen(text: String): String {
        return if (this.isSurroundedByParen(text)) {
            text.drop(1).dropLast(1)
        } else {
            text
        }
    }
}
