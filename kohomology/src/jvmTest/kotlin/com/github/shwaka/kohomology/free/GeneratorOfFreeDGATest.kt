package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.IntDegree
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
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

    "toIndeterminate() should sanitize name containing multiple underscores" {
        val data = listOf(
            "v123" to "v123",
            "v_1" to "v_1",
            "v_1_2" to "v_{1,2}",
            "v_a_b" to "v_{a,b}",
            "v__1" to "v_{,1}",
        )
        data.forAll { (name, tex) ->
            val generator = GeneratorOfFreeDGA(name, IntDegree(0), "0")
            generator.toIndeterminate().name.tex shouldBe tex
        }
    }
})
