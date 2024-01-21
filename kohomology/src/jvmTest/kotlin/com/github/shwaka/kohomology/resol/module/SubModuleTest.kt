package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class SubModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val coeffAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val generatingBasisNames = listOf("x", "y").map { StringBasisName(it) }
    val freeModule = FreeModule(coeffAlgebra, generatingBasisNames)

    val (e, t1, t2) = coeffAlgebra.getBasis()
    val (x, y) = freeModule.getGeneratingBasis()

    val subModule = SubModule(
        totalModule = freeModule,
        generatorOverCoeff = listOf(x),
    )

    "subModule.underlyingVectorSpace.dim should be 3" {
        subModule.underlyingVectorSpace.dim shouldBe 3
    }

    "orbit of x should be contained in the submodule" {
        freeModule.context.run {
            subModule.subspaceContains(e * x).shouldBeTrue()
            subModule.subspaceContains(t1 * x).shouldBeTrue()
            subModule.subspaceContains(t2 * x).shouldBeTrue()
        }
    }

    "orbit of y should not be contained in the submodule" {
        freeModule.context.run {
            subModule.subspaceContains(e * y).shouldBeFalse()
            subModule.subspaceContains(t1 * y).shouldBeFalse()
            subModule.subspaceContains(t2 * y).shouldBeFalse()
        }
    }
})
