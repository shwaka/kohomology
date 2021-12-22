package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.util.PrintConfig
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
        "sx should be printed as \"s{x}\" when useBar = S_WITH_DEGREE" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.S_WITH_DEGREE))
            texPrinter(sx) shouldBe "s{x}"
        }
        "sx should be printed as \"\\bar{x}\" when useBar = S" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.S))
            texPrinter(sx) shouldBe "s{x}"
        }
        "sx should be printed as \"\\bar{x}\" when useBar = BAR" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.BAR))
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
        "sx should be printed as \"s^{2}{x}\" when useBar = S_WITH_DEGREE" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.S_WITH_DEGREE))
            texPrinter(sx) shouldBe "s^{2}{x}"
        }
        "sx should be printed as \"\\bar{x}\" when useBar = S" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.S))
            texPrinter(sx) shouldBe "s{x}"
        }
        "sx should be printed as \"\\bar{x}\" when useBar = BAR" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = UseBar.BAR))
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
})
