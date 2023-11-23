package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val coefficientAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val generatingBasisNames = listOf("x", "y", "z").map { StringBasisName(it) }
    val freeModule = FreeModule(coefficientAlgebra, generatingBasisNames)

    "test freeModule.generatingBasisNames" {
        freeModule.generatingBasisNames shouldBe generatingBasisNames
    }
})
