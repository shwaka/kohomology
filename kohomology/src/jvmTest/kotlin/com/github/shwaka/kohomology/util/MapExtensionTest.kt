package com.github.shwaka.kohomology.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val mapExtensionTag = NamedTag("MapExtension")

class MapExtensionTest : FreeSpec({
    tags(mapExtensionTag)

    "both keys exists" {
        val mutableMap = mutableMapOf(
            0 to "0",
            1 to "1",
            2 to "2",
        )
        val expected = mutableMapOf(
            0 to "1",
            1 to "0",
            2 to "2",
        )
        mutableMap.exchange(0, 1)
        mutableMap shouldBe expected
    }

    "key1 exists but key2 does not" {
        val mutableMap = mutableMapOf(
            0 to "0",
        )
        val expected = mutableMapOf(
            1 to "0",
        )
        mutableMap.exchange(0, 1)
        mutableMap shouldBe expected
    }

    "key2 exists but key1 does not" {
        val mutableMap = mutableMapOf(
            0 to "0",
        )
        val expected = mutableMapOf(
            1 to "0",
        )
        mutableMap.exchange(1, 0)
        mutableMap shouldBe expected
    }

    "both keys do not exist" {
        val mutableMap = mutableMapOf(
            2 to "2",
            3 to "3",
        )
        val expected = mutableMapOf(
            2 to "2",
            3 to "3",
        )
        mutableMap.exchange(0, 1)
        mutableMap shouldBe expected
    }

    "should throw IllegalArgumentException if keys are equal" {
        shouldThrow<IllegalArgumentException> {
            mutableMapOf<Int, String>().exchange(0, 0)
        }
    }
})
