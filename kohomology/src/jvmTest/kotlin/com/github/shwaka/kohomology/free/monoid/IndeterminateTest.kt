package com.github.shwaka.kohomology.free.monoid

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

val indeterminateTag = NamedTag("Indeterminate")

data class CharacterData(
    val name: String,
    val characters: String,
    val beginning: Boolean,
    val nonBeginning: Boolean,
)

class IndeterminateTest : FreeSpec({
    tags(indeterminateTag)

    "test isValidAsBeginningChar and isValidAsNonBeginningChar" - {
        val latinAlphabets = CharacterData(
            name = "latin alphabets",
            characters = "abcdefghijklmnopqrstuvwxyz",
            beginning = true,
            nonBeginning = true,
        )
        val greekAlphabets = CharacterData(
            name = "greek alphabets",
            characters = "αβγδεζηθικλμνξοπρστυφχψω",
            beginning = true,
            nonBeginning = true,
        )
        val numbers = CharacterData(
            name = "numbers",
            characters = "0123456789",
            beginning = false,
            nonBeginning = true,
        )
        val underscore= CharacterData(
            name = "underscore",
            characters = "_",
            beginning = true,
            nonBeginning = true,
        )
        val illegalSymbols = CharacterData(
            name = "illegal symbols",
            characters = " +-*/.()[]<>%$\\",
            beginning = false,
            nonBeginning = false,
        )

        val characterDataList = listOf(
            latinAlphabets,
            greekAlphabets,
            numbers,
            underscore,
            illegalSymbols,
        )

        for (characterData in characterDataList) {
            "isValidAsBeginningChar should be ${characterData.beginning} for ${characterData.name}" {
                characterData.characters.toList().forAll { char ->
                    StringIndeterminateName.isValidAsBeginningChar(char) shouldBe
                        characterData.beginning
                }
            }

            "isValidAsNonBeginningChar should be ${characterData.nonBeginning} for ${characterData.name}" {
                characterData.characters.toList().forAll { char ->
                    StringIndeterminateName.isValidAsNonBeginningChar(char) shouldBe
                        characterData.nonBeginning
                }
            }
        }
    }

    val validNameList = listOf(
        "x", "y", "z",
        "t0", "t1", "t2",
        "a_1", "b_2", "c_3_4",
        "x_", "y_",
        "sx", "sy",
        "_", "_x", "_y", "_abc",
        "x1y2z3",
        "α", "β", "γ",
        "_α", "_β_", "γ_3"
    )
    val invalidNameList = listOf(
        "",
        "0", "1", "2",
        "0x", "2y", "3z",
        "x-y", "a+b", "1*t",
        "a.b", ".t",
        " ", " x", "y ",
        "\\bar{x}", "x_{3}", "(x)",
    )

    "test validateIndeterminateName" - {
        "validateIndeterminateName should not throw for valid names" {
            validNameList.forAll { validName ->
                shouldNotThrow<IllegalArgumentException> {
                    StringIndeterminateName.validateName(validName)
                }
            }
        }

        "validateIndeterminateName should throw for invalid names" {
            invalidNameList.forAll { invalidName ->
                shouldThrow<IllegalArgumentException> {
                    StringIndeterminateName.validateName(invalidName)
                }
            }
        }
    }

    "test constructor of StringIndeterminateName" - {
        "StringIndeterminateName.name should allow valid names" {
            validNameList.forAll { validName ->
                shouldNotThrow<IllegalArgumentException> {
                    StringIndeterminateName(name = validName)
                }
            }
        }
        "StringIndeterminateName.name should not allow invalid names" {
            invalidNameList.forAll { invalidName ->
                shouldThrow<IllegalArgumentException> {
                    StringIndeterminateName(name = invalidName)
                }
            }
        }
        "StringIndeterminateName.tex should allow string containing special characters" {
            invalidNameList.forAll { invalidName ->
                shouldNotThrow<IllegalArgumentException> {
                    StringIndeterminateName(name = "x", tex = invalidName)
                }
            }
        }
    }
})
