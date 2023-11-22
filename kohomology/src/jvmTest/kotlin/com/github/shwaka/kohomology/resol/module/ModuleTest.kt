package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val moduleTag = NamedTag("Module")

class ModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
    val coefficientAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
    val (x, y) = underlyingVectorSpace.getBasis()
    val (one, t) = coefficientAlgebra.getBasis()
    val module = run {
        val action = ValueBilinearMap(
            source1 = coefficientAlgebra,
            source2 = underlyingVectorSpace,
            target = underlyingVectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(x, y), // one*(-)
                listOf(y, x), // t*(-)
            )
        )
        Module(matrixSpace, underlyingVectorSpace, coefficientAlgebra, action)
    }

    "test action" {
        module.context.run {
            (one * x) shouldBe x
            (one * y) shouldBe y
            (t * x) shouldBe y
            (t * y) shouldBe x
            (t * (x + 2 * y)) shouldBe (y + 2 * x)
        }
    }
})
