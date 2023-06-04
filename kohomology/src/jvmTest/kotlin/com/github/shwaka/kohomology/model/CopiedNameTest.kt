package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
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
        val sx = Indeterminate("x", "X", n).copy(shift, null)
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{X}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{X}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{X}"
        }
    }
    "CopiedName test (shift = 2)" - {
        val n = 3
        val shift = 2
        val sx = Indeterminate("x", "X", n).copy(shift, null)
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^2x"
            texPrinter(sx) shouldBe "s^{2}{X}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{X}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{X}"
        }
    }
    "CopiedName test (index = 1)" - {
        val n = 3
        val index = 1
        val x1 = Indeterminate("x", "X", n).copy(shift = 0, index = index)
        for (showShift in ShowShift.values()) {
            "print x1 when showShift = $showShift" {
                val plainPrinter = Printer(PrintType.PLAIN, showShift = showShift)
                val texPrinter = Printer(PrintType.TEX, showShift = showShift)
                plainPrinter(x1) shouldBe "x1"
                texPrinter(x1) shouldBe "{X}_{(1)}"
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
            Indeterminate("x", "X", fromInt(3)).copy(degreeGroup, shift = 1 + 2 * n)
        }
        "print sx when showShift = S_WITH_DEGREE" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S_WITH_DEGREE)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S_WITH_DEGREE)
            plainPrinter(sx) shouldBe "s^{1 + 2N}x"
            texPrinter(sx) shouldBe "s^{1 + 2N}{X}"
        }
        "print sx when showShift = S" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.S)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.S)
            plainPrinter(sx) shouldBe "sx"
            texPrinter(sx) shouldBe "s{X}"
        }
        "print sx when showShift = BAR" {
            val plainPrinter = Printer(PrintType.PLAIN, showShift = ShowShift.BAR)
            val texPrinter = Printer(PrintType.TEX, showShift = ShowShift.BAR)
            plainPrinter(sx) shouldBe "_x"
            texPrinter(sx) shouldBe "\\bar{X}"
        }
    }

    "test equality of CopiedName" - {
        "Two CopiedName should be equal if the given arguments are equal" {
            CopiedName(
                original = StringIndeterminateName("x"),
                shift = IntDegree(3),
            ) shouldBe CopiedName(
                original = StringIndeterminateName("x"),
                shift = IntDegree(3),
            )

            CopiedName(
                original = StringIndeterminateName("y"),
                shift = IntDegree(1),
                index = 1,
            ) shouldBe CopiedName(
                original = StringIndeterminateName("y"),
                shift = IntDegree(1),
                index = 1,
            )

            val multiDegreeGroup = MultiDegreeGroup(
                listOf(
                    DegreeIndeterminate("N", 1)
                )
            )
            CopiedName(
                original = StringIndeterminateName("z"),
                shift = multiDegreeGroup.fromList(listOf(3, 4)),
                index = 2,
            ) shouldBe CopiedName(
                original = StringIndeterminateName("z"),
                shift = multiDegreeGroup.fromList(listOf(3, 4)),
                index = 2,
            )
        }

        "Equality of two CopiedName should not depend on showShiftExponentInIdentifier" {
            CopiedName(
                original = StringIndeterminateName("a"),
                shift = IntDegree(3),
                showShiftExponentInIdentifier = true,
            ) shouldBe CopiedName(
                original = StringIndeterminateName("a"),
                shift = IntDegree(3),
                showShiftExponentInIdentifier = true,
            )
        }
    }

    "test CopiedName.identifier.value" {
        CopiedName(
            original = StringIndeterminateName("x"),
            shift = IntDegree(3),
        ).identifier.value shouldBe "s_3x"

        CopiedName(
            original = StringIndeterminateName("x"),
            shift = IntDegree(-3),
        ).identifier.value shouldBe "s_m3x"

        CopiedName(
            original = StringIndeterminateName("x"),
            shift = IntDegree(3),
            showShiftExponentInIdentifier = true,
        ).identifier.value shouldBe "s_3x"

        CopiedName(
            original = StringIndeterminateName("x"),
            shift = IntDegree(3),
            showShiftExponentInIdentifier = false,
        ).identifier.value shouldBe "sx"

        CopiedName(
            original = StringIndeterminateName("y"),
            shift = IntDegree(1),
            index = 1,
        ).identifier.value shouldBe "sy1"

        val multiDegreeGroup = MultiDegreeGroup(
            listOf(
                DegreeIndeterminate("N", 1)
            )
        )
        CopiedName(
            original = StringIndeterminateName("z"),
            shift = multiDegreeGroup.fromList(listOf(3, 4)),
            index = 2,
        ).identifier.value shouldBe "s_3_4z2"
        CopiedName(
            original = StringIndeterminateName("z"),
            shift = multiDegreeGroup.fromList(listOf(1, -2)),
        ).identifier.value shouldBe "s_1_m2z"
        CopiedName(
            original = StringIndeterminateName("z"),
            shift = multiDegreeGroup.fromList(listOf(3, 4)),
            index = 2,
            showShiftExponentInIdentifier = true,
        ).identifier.value shouldBe "s_3_4z2"
        CopiedName(
            original = StringIndeterminateName("z"),
            shift = multiDegreeGroup.fromList(listOf(3, 4)),
            index = 2,
            showShiftExponentInIdentifier = false,
        ).identifier.value shouldBe "sz2"
    }
})
