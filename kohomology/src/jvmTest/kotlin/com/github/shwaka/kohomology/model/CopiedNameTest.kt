package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.UseBar
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val copiedNameTag = NamedTag("CopiedName")

class CopiedNameTest : FreeSpec({
    tags(copiedNameTag)
    "CopiedName test (shift = 1)" - {
        val n = 3
        val shift = 1
        val indeterminate = Indeterminate("x", n).copy(shift, null)
        val basisName = Monomial(listOf(indeterminate), listOf(1))
        val numVectorSpace = SparseNumVectorSpaceOverBigRational
        val vectorSpace = VectorSpace(numVectorSpace, listOf(basisName))
        val (sx) = vectorSpace.getBasis()
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
        val indeterminate = Indeterminate("x", n).copy(shift, null)
        val basisName = Monomial(listOf(indeterminate), listOf(1))
        val numVectorSpace = SparseNumVectorSpaceOverBigRational
        val vectorSpace = VectorSpace(numVectorSpace, listOf(basisName))
        val (sx) = vectorSpace.getBasis()
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
        val indeterminate = Indeterminate("x", n).copy(shift = 0, index = index)
        val basisName = Monomial(listOf(indeterminate), listOf(1))
        val numVectorSpace = SparseNumVectorSpaceOverBigRational
        val vectorSpace = VectorSpace(numVectorSpace, listOf(basisName))
        val (x1) = vectorSpace.getBasis()
        for (useBar in UseBar.values()) {
            "print x1 when useBar = $useBar" {
                val plainPrinter = Printer(PrintType.PLAIN, useBar = useBar)
                val texPrinter = Printer(PrintType.TEX, useBar = useBar)
                plainPrinter(x1) shouldBe "x1"
                texPrinter(x1) shouldBe "{x}_{(1)}"
            }
        }
    }
})
