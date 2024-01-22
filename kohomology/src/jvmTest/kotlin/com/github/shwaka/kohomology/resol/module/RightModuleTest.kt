package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RightModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
    val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
    val (x, y) = underlyingVectorSpace.getBasis()
    val (one, t) = coeffAlgebra.getBasis()
    val module = run {
        // Z/2 acting on Q{x, y} by
        //   x*t = y, y*t = x
        val action = ValueBilinearMap(
            source1 = underlyingVectorSpace,
            source2 = coeffAlgebra,
            target = underlyingVectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(x, y), // x*(-)
                listOf(y, x), // y*(-)
            )
        )
        RightModule(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
    }

    "test action" {
        module.context.run {
            (x * one) shouldBe x
            (y * one) shouldBe y
            (x * t) shouldBe y
            (y * t) shouldBe x
            ((x + 2 * y) * t) shouldBe (y + 2 * x)
        }
    }

    "nested context should work" {
        module.coeffAlgebra.context.run {
            module.context.run {
                (x * (one + t)) shouldBe (x + y)
                ((3 * x + y) * (one - 2 * t)) shouldBe (x - 5 * y)
                (x * (one + t.pow(3))) shouldBe (x + y)
            }
        }
    }
})
