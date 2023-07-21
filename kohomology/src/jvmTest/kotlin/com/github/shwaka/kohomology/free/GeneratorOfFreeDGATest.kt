package com.github.shwaka.kohomology.free

import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val generatorOfFreeDGATag = NamedTag("GeneratorOfFreeDGA")

class GeneratorOfFreeDGATest : FreeSpec({
    tags(generatorOfFreeDGATag)

    "serialize empty list" {
        generatorListToJson(emptyList()) shouldBe "[]"
    }

    "serialize generator list for S^2" {
        val generatorList = listOf(
            GeneratorOfFreeDGA("x", 2, "0"),
            GeneratorOfFreeDGA("y", 3, "x^2"),
        )
        generatorListToJson(generatorList) shouldBe """[["x",2,"0"],["y",3,"x^2"]]"""
    }

    "deserialize empty list" {
        jsonToGeneratorList("[]") shouldBe emptyList()
    }

    "deserialize generator list for S^2" {
        val expected = listOf(
            GeneratorOfFreeDGA("x", 2, "0"),
            GeneratorOfFreeDGA("y", 3, "x^2"),
        )
        jsonToGeneratorList("""[["x",2,"0"],["y",3,"x^2"]]""") shouldBe expected
    }
})
