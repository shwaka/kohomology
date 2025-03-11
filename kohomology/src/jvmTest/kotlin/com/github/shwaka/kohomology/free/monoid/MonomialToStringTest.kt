package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.PrintType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

private data class Result(
    val plain: String,
    val tex: String,
    val code: String,
) {
    private fun getValue(printType: PrintType): String {
        return when (printType) {
            PrintType.PLAIN -> this.plain
            PrintType.TEX -> this.tex
            PrintType.CODE -> this.code
        }
    }

    fun check(getString: (PrintType) -> String) {
        PrintType.values().forAll { printType ->
            getString(printType) shouldBe this.getValue(printType)
        }
    }

    companion object {
        fun same(plainTexCode: String): Result {
            return Result(plainTexCode, plainTexCode, plainTexCode)
        }
    }
}

class MonomialToStringTest : FreeSpec({
    "empty monomial should be printed as 1" {
        monomialToString<StringIndeterminateName>(
            emptyList(),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "1"
    }

    "x should be printed as x" {
        monomialToString(
            listOf(Pair(StringIndeterminateName("x"), 1)),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "x"
    }

    "xy should be printed as xy" {
        monomialToString(
            listOf(
                Pair(StringIndeterminateName("x"), 1),
                Pair(StringIndeterminateName("y"), 1),
            ),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "xy"
    }

    "test xy" {
        Result.same("xy").check { printType ->
            monomialToString(
                listOf(
                    Pair(StringIndeterminateName("x"), 1),
                    Pair(StringIndeterminateName("y"), 1),
                ),
                printType
            ) { it.toString() }
        }
    }
})
