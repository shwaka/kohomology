package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val copiedNameTag = NamedTag("CopiedName")

class CopiedNameTest : FreeSpec({
    tags(copiedNameTag)
    "CopiedName test (shift = 1)" - {
        val n = 3
        val shift = 1
        val sx = Indeterminate("x", n).copy(shift, null)
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
    "CopiedName test (shift = 2)" - {
        val n = 3
        val shift = 2
        val sx = Indeterminate("x", n).copy(shift, null)
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^2x"
            texPrinter(sx) shouldBe "s^{2}{x}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
    "CopiedName test (index = 1)" - {
        val n = 3
        val index = 1
        val x1 = Indeterminate("x", n).copy(shift = 0, index = index)
        for (showShift in ShowShift.values()) {
            "print x1 when showShift = $showShift" {
                val plainPrinter = Printer(PrintType.PLAIN, showShift = showShift)
                val texPrinter = Printer(PrintType.TEX, showShift = showShift)
                plainPrinter(x1) shouldBe "x1"
                texPrinter(x1) shouldBe "{x}_{(1)}"
            }
        }
    }
    "CopiedName test (shift: MultiDegree)" - {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("N", 0),
        )
        val degreeGroup = MultiDegreeGroup(degreeIndeterminateList)
        val (n) = degreeGroup.generatorList
        val sx = degreeGroup.context.run {
            Indeterminate("x", fromInt(3)).copy(degreeGroup, shift = 1 + 2 * n)
        }
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^{1 + 2N}x"
            texPrinter(sx) shouldBe "s^{1 + 2N}{x}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
})
