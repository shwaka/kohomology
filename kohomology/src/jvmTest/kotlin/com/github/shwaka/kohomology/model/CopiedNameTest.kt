package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverBigRational
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType
import com.github.shwaka.kohomology.vectsp.Printer
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val copiedNameTag = NamedTag("CopiedName")

class CopiedNameTest : FreeSpec({
    tags(copiedNameTag)
    "CopiedName test" - {
        val n = 3
        val indeterminate = Indeterminate("x", n).copy(1, null)
        val basisName = Monomial(listOf(indeterminate), listOf(1))
        val numVectorSpace = SparseNumVectorSpaceOverBigRational
        val vectorSpace = VectorSpace(numVectorSpace, listOf(basisName))
        val (sx) = vectorSpace.getBasis()
        "sx should be printed as \"s{x}\" when the printer is TexVectorPrinterForCopiedName with useBar = false" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = false))
            texPrinter(sx) shouldBe "s{x}"
        }
        "sx should be printed as \"\\bar{x}\" when the printer is TexVectorPrinterForCopiedName with useBar = true" {
            val texPrinter = Printer(PrintConfig(PrintType.TEX, useBar = true))
            texPrinter(sx) shouldBe "\\bar{x}"
        }
    }
})
