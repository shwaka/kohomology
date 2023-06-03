package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable

public interface IndeterminateName : Printable {
    public override fun toString(printConfig: PrintConfig): String = this.toString()
}

public class StringIndeterminateName(public val name: String, tex: String? = null) : IndeterminateName {
    public val tex: String = tex ?: name

    init {
        StringIndeterminateName.validateName(name)
        // tex must NOT be validated since it can contain special characters for TeX.
    }

    override fun toString(): String = this.name
    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> this.name
            PrintType.TEX -> this.tex
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StringIndeterminateName

        if (name != other.name) return false
        if (tex != other.tex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tex.hashCode()
        return result
    }

    public companion object {
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

        // The following functions are internal for test.
        internal fun isValidAsBeginningChar(char: Char): Boolean {
            return char.category in
                (this.alphabeticalCategories + this.punctuationCategories)
        }

        internal fun isValidAsNonBeginningChar(char: Char): Boolean {
            return char.category in
                (this.alphabeticalCategories + this.punctuationCategories + this.numericalCategories)
        }

        internal fun validateName(name: String) {
            require(name.isNotEmpty()) {
                "Indeterminate name must be non-empty."
            }
            require(this.isValidAsBeginningChar(name[0])) {
                "Indeterminate name must start with " +
                    "alphabets (including greeks) or underscore, " +
                    "but ${name[0]} was given."
            }
            for (c in name.drop(1)) {
                require(this.isValidAsNonBeginningChar(c)) {
                    "Indeterminate name can only contain " +
                        "alphabets (including greeks), numbers or underscore, " +
                        "but $c was given."
                }
            }
        }
    }
}

public fun <D : Degree> Indeterminate(name: String, degree: D): Indeterminate<D, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name), degree)
}
public fun <D : Degree> Indeterminate(name: String, tex: String, degree: D): Indeterminate<D, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name, tex), degree)
}
public fun <I : IndeterminateName> Indeterminate(name: I, degree: Int): Indeterminate<IntDegree, I> {
    return Indeterminate(name, IntDegree(degree))
}
public fun Indeterminate(name: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name), IntDegree(degree))
}
public fun Indeterminate(name: String, tex: String, degree: Int): Indeterminate<IntDegree, StringIndeterminateName> {
    return Indeterminate(StringIndeterminateName(name, tex), IntDegree(degree))
}

public data class Indeterminate<D : Degree, I : IndeterminateName>(val name: I, val degree: D) : Printable {
    override fun toString(): String {
        return this.name.toString()
    }

    public override fun toString(printConfig: PrintConfig): String {
        return this.name.toString(printConfig)
    }

    public fun <D_ : Degree> convertDegree(degreeMorphism: DegreeMorphism<D, D_>): Indeterminate<D_, I> {
        return Indeterminate(this.name, degreeMorphism(this.degree))
    }
}
