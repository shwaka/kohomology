package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.free.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.SparseNumVector
import com.github.shwaka.kohomology.specific.BigRational
import com.github.shwaka.kohomology.specific.SparseNumVectorSpaceOverBigRational
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
        "sx.toString() should be \"s{x}\" when the printer is TexVectorPrinterForCopiedName with useBar = false" {
            vectorSpace.printer = TexVectorPrinterForCopiedName<IntDegree, StringIndeterminateName, BigRational, SparseNumVector<BigRational>>(useBar = false)
            sx.toString() shouldBe "s{x}"
        }
        "sx.toString() should be \"\\bar{x}\" when the printer is TexVectorPrinterForCopiedName with useBar = true" {
            vectorSpace.printer = TexVectorPrinterForCopiedName<IntDegree, StringIndeterminateName, BigRational, SparseNumVector<BigRational>>(useBar = true)
            sx.toString() shouldBe "\\bar{x}"
        }
    }
})
