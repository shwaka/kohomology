package com.github.shwaka.kohomology.util

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

public data class PartialIdentifier(val name: String) {
    init {
        PartialIdentifier.validateName(name)
    }

    public companion object {
        public fun fromInt(int: Int): PartialIdentifier {
            return if (int >= 0) {
                PartialIdentifier(int.toString())
            } else {
                // Since "-" is not available in Identifier,
                // replace it with "m", the initial character of "minus".
                // Note that -int does not work for Int.MIN_VALUE.
                val withoutSign = int.toString().removePrefix("-")
                PartialIdentifier("m$withoutSign")
            }
        }

        public fun fromIntList(intList: List<Int>): PartialIdentifier {
            val name = intList.joinToString("_") { PartialIdentifier.fromInt(it).name }
            return PartialIdentifier(name)
        }

        internal val charCategoryList: List<CharCategory> =
            alphabeticalCategories + punctuationCategories + numericalCategories

        internal fun isValidChar(char: Char): Boolean {
            return char.category in this.charCategoryList
        }

        internal fun validateName(name: String, className: String? = PartialIdentifier::class.simpleName) {
            for (c in name) {
                require(this.isValidChar(c)) {
                    "${className ?: "Identifier"} name ($name) can only contain " +
                        "alphabets (including greeks), numbers or underscore, " +
                        "but it contains \"$c\"."
                }
            }
        }
    }
}

public data class Identifier(val name: String) {
    init {
        Identifier.validateName(name)
    }

    public companion object {
        internal val firstCharCategoryList: List<CharCategory> =
            alphabeticalCategories + punctuationCategories

        internal fun isValidAsFirstChar(char: Char): Boolean {
            return char.category in this.firstCharCategoryList
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
            PartialIdentifier.validateName(name, Identifier::class.simpleName)
        }
    }
}
