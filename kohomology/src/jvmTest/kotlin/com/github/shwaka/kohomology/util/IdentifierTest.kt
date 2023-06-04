package com.github.shwaka.kohomology.util

import com.github.shwaka.kohomology.myArbList
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

val identifierTag = NamedTag("Identifier")

data class CharacterData(
    val name: String,
    val characters: String,
    val validAtFirst: Boolean,
    val validAtSuccessor: Boolean,
)

class IdentifierTest : FreeSpec({
    tags(identifierTag)

    "test Identifier.format" - {
        "result of Identifier.format(int) should be valid as successor" {
            checkAll(Arb.int()) { n ->
                val formatted = Identifier.format(n)
                shouldNotThrowAny {
                    Identifier.validateName("_$formatted")
                }
            }
        }

        "check return value of Identifier.format(int)" {
            Identifier.format(3) shouldBe "3"
            Identifier.format(0) shouldBe "0"
            Identifier.format(-1) shouldBe "m1"
            Identifier.format(-10) shouldBe "m10"
        }

        "result of Identifier.format(intList) should be valid as successor" {
            checkAll(Arb.int(0..5)) { n ->
                checkAll(myArbList(Arb.int(), n)) { intList ->
                    val formatted = Identifier.format(intList)
                    shouldNotThrowAny {
                        Identifier.validateName("_$formatted")
                    }
                }
            }
        }

        "check return value of Identifier.format(intList)" {
            Identifier.format(emptyList()) shouldBe ""
            Identifier.format(listOf(1, 2, 3)) shouldBe "1_2_3"
            Identifier.format(listOf(1, -2, -3)) shouldBe "1_m2_m3"
            Identifier.format(listOf(-1, 2)) shouldBe "m1_2"
        }
    }

    "test isValidAsFirstChar and isValidAsNonFirstChar" - {
        val latinAlphabets = CharacterData(
            name = "latin alphabets",
            characters = "abcdefghijklmnopqrstuvwxyz",
            validAtFirst = true,
            validAtSuccessor = true,
        )
        val greekAlphabets = CharacterData(
            name = "greek alphabets",
            characters = "αβγδεζηθικλμνξοπρστυφχψω",
            validAtFirst = true,
            validAtSuccessor = true,
        )
        val numbers = CharacterData(
            name = "numbers",
            characters = "0123456789",
            validAtFirst = false,
            validAtSuccessor = true,
        )
        val underscore = CharacterData(
            name = "underscore",
            characters = "_",
            validAtFirst = true,
            validAtSuccessor = true,
        )
        val illegalSymbols = CharacterData(
            name = "illegal symbols",
            characters = " +-*/.()[]<>%$\\" +
                "　" /* U+3000, Ideographic Space (full-width space used in Japanese) */,
            validAtFirst = false,
            validAtSuccessor = false,
        )

        val characterDataList = listOf(
            latinAlphabets,
            greekAlphabets,
            numbers,
            underscore,
            illegalSymbols,
        )

        for (characterData in characterDataList) {
            "isValidAsFirstChar should be ${characterData.validAtFirst} for ${characterData.name}" {
                characterData.characters.toList().forAll { char ->
                    Identifier.isValidAsFirstChar(char) shouldBe
                        characterData.validAtFirst
                }
            }

            "isValidAsNonFirstChar should be ${characterData.validAtSuccessor} for ${characterData.name}" {
                characterData.characters.toList().forAll { char ->
                    Identifier.isValidAsNonFirstChar(char) shouldBe
                        characterData.validAtSuccessor
                }
            }
        }
    }

    "test validateIndeterminateName" - {
        "validateIndeterminateName should not throw for valid names" {
            validNameList.forAll { validName ->
                shouldNotThrow<IllegalArgumentException> {
                    Identifier.validateName(validName)
                }
            }
        }

        "validateIndeterminateName should throw for invalid names" {
            invalidNameList.forAll { invalidName ->
                shouldThrow<IllegalArgumentException> {
                    Identifier.validateName(invalidName)
                }
            }
        }
    }
}) {
    companion object {
        // These are also used in IndeterminateTest.
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
    }
}
