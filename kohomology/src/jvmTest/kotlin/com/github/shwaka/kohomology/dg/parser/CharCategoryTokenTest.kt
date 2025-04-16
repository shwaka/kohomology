package com.github.shwaka.kohomology.dg.parser

import com.github.shwaka.kohomology.util.Identifier
import com.github.shwaka.kohomology.util.PartialIdentifier
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

data class TokenTestData(
    val input: String,
    val fromIndex: Int,
    val expectedMatchLength: Int,
) {
    fun getTestName(tokenName: String): String {
        val substring = this.input.substring(
            this.fromIndex until (this.fromIndex + this.expectedMatchLength)
        )
        return "$tokenName.match(\\\"${this.input}\\\", ${this.fromIndex}) should " +
            if (substring.isEmpty()) {
                "not match"
            } else {
                "match at \"$substring\""
            }
    }
}

class CharCategoryTokenTest : FreeSpec({
    "test with identifier token" - {
        val identifierToken = charCategoryToken(
            Identifier.firstCharCategoryList,
            PartialIdentifier.charCategoryList,
        )
        val dataList = listOf(
            TokenTestData("abc", fromIndex = 0, expectedMatchLength = 3),
            TokenTestData("a bc", fromIndex = 0, expectedMatchLength = 1),
            TokenTestData("x+y", fromIndex = 0, expectedMatchLength = 1),
            TokenTestData("x+y", fromIndex = 1, expectedMatchLength = 0),
            TokenTestData("x+y", fromIndex = 2, expectedMatchLength = 1),
            TokenTestData("ad-bc", fromIndex = 0, expectedMatchLength = 2),
            TokenTestData("ad-bc", fromIndex = 2, expectedMatchLength = 0),
            TokenTestData("ad-bc", fromIndex = 3, expectedMatchLength = 2),
            TokenTestData("a_b", fromIndex = 0, expectedMatchLength = 3),
            TokenTestData("t0", fromIndex = 0, expectedMatchLength = 2),
            TokenTestData("0t", fromIndex = 0, expectedMatchLength = 0),
            TokenTestData("_x", fromIndex = 0, expectedMatchLength = 2),
        )
        for (data in dataList) {
            val testName = data.getTestName("identifierToken")
            testName {
                identifierToken.match(data.input, data.fromIndex) shouldBe data.expectedMatchLength
            }
        }
    }
})
