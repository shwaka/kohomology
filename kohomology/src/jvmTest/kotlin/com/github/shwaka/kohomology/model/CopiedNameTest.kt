package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.UseBar
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
        "print sx when useBar = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when useBar = S" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when useBar = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.BAR)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
    "CopiedName test (shift = 2)" - {
        val n = 3
        val shift = 2
        val sx = Indeterminate("x", n).copy(shift, null)
        "print sx when useBar = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^2x"
            texPrinter(sx) shouldBe "s^{2}{x}"
        }
        "print sx when useBar = S" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when useBar = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.BAR)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
    "CopiedName test (index = 1)" - {
        val n = 3
        val index = 1
        val x1 = Indeterminate("x", n).copy(shift = 0, index = index)
        for (useBar in UseBar.values()) {
            "print x1 when useBar = $useBar" {
                val plainPrinter = Printer(PrintType.PLAIN, useBar = useBar)
                val texPrinter = Printer(PrintType.TEX, useBar = useBar)
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
        "print sx when useBar = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^{1 + 2N}x"
            texPrinter(sx) shouldBe "s^{1 + 2N}{x}"
        }
        "print sx when useBar = S" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.S)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{x}"
        }
        "print sx when useBar = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, useBar = UseBar.BAR)
            val texPrinter = Printer(PrintType.TEX, useBar = UseBar.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
})
