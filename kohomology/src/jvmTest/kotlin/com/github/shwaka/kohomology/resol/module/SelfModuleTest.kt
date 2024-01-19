package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SelfModuleTest : FreeSpec({
    tags(moduleTag)

    "test SelfModule of monoid ring" - {
        val matrixSpace = SparseMatrixSpaceOverRational
        val monoidRing = MonoidRing(CyclicGroup(5), matrixSpace)
        val selfModule = SelfModule(monoidRing)

        "selfModule should have the same dimension as its coeffAlgebra" {
            selfModule.underlyingVectorSpace.dim shouldBe monoidRing.dim
        }

        "selfModule.getGeneratingBasis() should have size 1 " {
            selfModule.getGeneratingBasis() shouldHaveSize 1
        }
    }
})
