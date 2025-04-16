package com.github.shwaka.kohomology.dg.parser

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RegexTokenTest : FreeSpec({
    "test with white-space token" - {
        val wsToken = regexToken("\\s*", ignore = true)
        val dataList = listOf(
            TokenTestData("abc", fromIndex = 0, expectedMatchLength = 0),
            TokenTestData("a b", fromIndex = 0, expectedMatchLength = 0),
            TokenTestData("a b", fromIndex = 1, expectedMatchLength = 1),
            TokenTestData("a  b", fromIndex = 1, expectedMatchLength = 2),
            TokenTestData(" a", fromIndex = 0, expectedMatchLength = 1),
            TokenTestData("a　b", fromIndex = 1, expectedMatchLength = 0), // Ideographic space (全角スペース)

        )
        for (data in dataList) {
            val substring = data.input.substring(
                data.fromIndex until (data.fromIndex + data.expectedMatchLength)
            )
            val testName = "wsToken.match(\\\"${data.input}\\\", ${data.fromIndex}) should " +
                if (substring.isEmpty()) {
                    "not match"
                } else {
                    "match at \"$substring\""
                }
            testName {
                wsToken.match(data.input, data.fromIndex) shouldBe data.expectedMatchLength
            }
        }
    }

    "test with natural number token" - {
        val natToken = regexToken("\\d+")
        val dataList = listOf(
            TokenTestData("123", fromIndex = 0, expectedMatchLength = 3),
            TokenTestData("1 23", fromIndex = 0, expectedMatchLength = 1),
            TokenTestData("1 23", fromIndex = 1, expectedMatchLength = 0),
            TokenTestData("1 23", fromIndex = 2, expectedMatchLength = 2),
            TokenTestData("12x345", fromIndex = 0, expectedMatchLength = 2),
            TokenTestData("12x345", fromIndex = 2, expectedMatchLength = 0),
            TokenTestData("12x345", fromIndex = 3, expectedMatchLength = 3),
            TokenTestData("-1", fromIndex = 0, expectedMatchLength = 0),
            TokenTestData("-1", fromIndex = 1, expectedMatchLength = 1),
        )
        for (data in dataList) {
            val testName = data.getTestName("natToken")
            testName {
                natToken.match(data.input, data.fromIndex) shouldBe data.expectedMatchLength
            }
        }
    }
})
