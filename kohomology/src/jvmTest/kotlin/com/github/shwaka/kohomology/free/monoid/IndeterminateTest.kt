package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.IdentifierTest.Companion.invalidNameList
import com.github.shwaka.kohomology.util.IdentifierTest.Companion.validNameList
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

val indeterminateTag = NamedTag("Indeterminate")

class IndeterminateTest : FreeSpec({
    tags(indeterminateTag)

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

    "test StringIndeterminateName.identifier.value" {
        StringIndeterminateName("x").identifier.value shouldBe "x"
        StringIndeterminateName("sy", "\\bar{y}").identifier.value shouldBe "sy"
        StringIndeterminateName("z", "\\{}.@$?-+*/").identifier.value shouldBe "z"
    }
})
