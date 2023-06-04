package com.github.shwaka.kohomology.util

public data class Identifier(val name: String) {
    init {
        Identifier.validateName(name)
    }

    public companion object {
        public fun format(int: Int): String {
            return if (int >= 0) {
                int.toString()
            } else {
                // Since "-" is not available in Identifier,
                // replace it with "m", the initial character of "minus".
                // Note that -int does not work for Int.MIN_VALUE.
                val withoutSign = int.toString().removePrefix("-")
                "m$withoutSign"
            }
        }

        public fun format(intList: List<Int>): String {
            return intList.joinToString("_") { Identifier.format(it) }
        }

        private val alphabeticalCategories: List<CharCategory> = listOf(
            CharCategory.LOWERCASE_LETTER, // Also contains greek letters.
            CharCategory.UPPERCASE_LETTER,
            // CharCategory.TITLECASE_LETTER, // Very few characters (about 30) and seems useless.
            // CharCategory.MODIFIER_LETTER, // What is this category?
            CharCategory.OTHER_LETTER, // Contains Japanese characters.
        )
        private val numericalCategories: List<CharCategory> = listOf(
            CharCategory.DECIMAL_DIGIT_NUMBER,
            // CharCategory.LETTER_NUMBER, // Contains roman numeral
            // CharCategory.OTHER_NUMBER,
        )
        private val punctuationCategories: List<CharCategory> = listOf(
            CharCategory.CONNECTOR_PUNCTUATION, // Contains _ (underscore)
            // CharCategory.DASH_PUNCTUATION, // Contains - (minus)
            // CharCategory.START_PUNCTUATION, // Contains (, [, {
            // CharCategory.END_PUNCTUATION, // Contains ), ], }
            // CharCategory.INITIAL_QUOTE_PUNCTUATION,
            // CharCategory.FINAL_QUOTE_PUNCTUATION,
            // CharCategory.OTHER_PUNCTUATION
        )

        internal val firstCharCategoryList: List<CharCategory> =
            alphabeticalCategories + punctuationCategories

        internal val nonFirstCharCategoryList: List<CharCategory> =
            alphabeticalCategories + punctuationCategories + numericalCategories

        // The following functions are internal for test.
        internal fun isValidAsFirstChar(char: Char): Boolean {
            return char.category in this.firstCharCategoryList
        }

        internal fun isValidAsNonFirstChar(char: Char): Boolean {
            return char.category in this.nonFirstCharCategoryList
        }

        internal fun validateName(name: String) {
            require(name.isNotEmpty()) {
                "Identifier name ($name) must be non-empty."
            }
            require(this.isValidAsFirstChar(name[0])) {
                "Identifier name ($name) must start with " +
                    "alphabets (including greeks) or underscore, " +
                    "but it starts with \"${name[0]}\"."
            }
            for (c in name.drop(1)) {
                require(this.isValidAsNonFirstChar(c)) {
                    "Identifier name ($name) can only contain " +
                        "alphabets (including greeks), numbers or underscore, " +
                        "but it contains \"$c\"."
                }
            }
        }
    }
}
