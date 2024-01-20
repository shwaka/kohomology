package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TrivialModuleTest : FreeSpec({
    tags(moduleTag)

    "test trivial module" - {
        val matrixSpace = SparseMatrixSpaceOverRational
        val monoidRing = MonoidRing(
            CyclicGroup(3),
            matrixSpace,
        )
        val (e, t1, t2) = monoidRing.getBasis()

        val vectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val (x, y) = vectorSpace.getBasis()
        val trivialModule = TrivialModule(vectorSpace, monoidRing)

        "trivialModule.underlyingVectorSpace.dim should be same as vectorSpace.dim" {
            trivialModule.underlyingVectorSpace.dim shouldBe vectorSpace.dim
        }

        "test trivial action" {
            trivialModule.context.run {
                (e * x) shouldBe x
                (t1 * x) shouldBe x
                (t2 * x) shouldBe x
                (e * y) shouldBe y
                (t1 * y) shouldBe y
                (t2 * y) shouldBe y
            }
        }
    }
})
