package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.util.PrintType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

private data class TestData(
    val monomial: List<Pair<String, Int>>,
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

    fun check() {
        PrintType.values().forAll { printType ->
            val powerList = this.monomial.map { (indeterminateNameString, exponent) ->
                Power(indeterminateNameString, exponent)
            }
            monomialToString(
                powerList,
                printType,
            ) { it.toString() } shouldBe this.getValue(printType)
        }
    }

    companion object {
        fun same(monomial: List<Pair<String, Int>>, plainTexCode: String): TestData {
            return TestData(monomial, plainTexCode, plainTexCode, plainTexCode)
        }
    }
}

class MonomialToStringTest : FreeSpec({
    "empty monomial should be printed as 1 for PrintType.PLAIN" {
        monomialToString<StringIndeterminateName>(
            emptyList(),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "1"
    }

    "x should be printed as x for PrintType.PLAIN" {
        monomialToString(
            listOf(Power("x", 1)),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "x"
    }

    "xy should be printed as xy for PrintType.PLAIN" {
        monomialToString(
            listOf(
                Power("x", 1),
                Power("y", 1),
            ),
            PrintType.PLAIN
        ) { it.toString() } shouldBe "xy"
    }

    "test 1" {
        TestData.same(emptyList(), "1").check()
    }

    "test x" {
        TestData.same(listOf("x" to 1), "x").check()
    }

    "test x^2" {
        TestData(listOf("x" to 2), "x^2", "x^{2}", "x^2").check()
    }

    "test xy" {
        TestData(listOf("x" to 1, "y" to 1), "xy", "xy", "x * y").check()
    }

    "test x^2y" {
        TestData(listOf("x" to 2, "y" to 1), "x^2y", "x^{2}y", "x^2 * y").check()
    }

    "test x^2y^2" {
        TestData(listOf("x" to 2, "y" to 2), "x^2y^2", "x^{2}y^{2}", "x^2 * y^2").check()
    }

    "test xyx" {
        // Non-commutative monomial.
        TestData(listOf("x" to 1, "y" to 1, "x" to 1), "xyx", "xyx", "x * y * x").check()
    }

    "test x^2x" {
        // This may be strange since one might expect "x^3".
        // But monomialToString does not handle such computation.
        TestData(listOf("x" to 2, "x" to 1), "x^2x", "x^{2}x", "x^2 * x").check()
    }
})
