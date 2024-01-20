package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.Augmentation
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.math.min

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

class IdealSubModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val order = 5
    val monoid = FiniteMonoidFromList(
        elements = (0 until order).map { "x$it" },
        multiplicationTable = (0 until order).map { i ->
            (0 until order).map { j ->
                val k = min(i + j, order - 1)
                "x$k"
            }
        },
        name = "M_$order"
    )
    val coeffAlgebra = MonoidRing(monoid, matrixSpace)
    val totalModule = FreeModule(coeffAlgebra, listOf(StringBasisName("v")))
    val augmentation = Augmentation(coeffAlgebra)
    val ideal = augmentation.kernel()
    val subModule = SubModule(totalModule, ideal)

    "monoid should satisfy the axioms" {
        shouldNotThrow<IllegalStateException> {
            monoid.checkMonoidAxioms()
        }
    }

    "totalModule should have dimension $order" {
        totalModule.underlyingVectorSpace.dim shouldBe order
    }

    "subModule should have dimension ${order - 1}" {
        subModule.underlyingVectorSpace.dim shouldBe (order - 1)
    }

    "subModule.inclusion should be injective" {
        subModule.inclusion.underlyingLinearMap.isInjective().shouldBeTrue()
    }

    "subModule.retraction" {
        subModule.retraction.underlyingLinearMap.isSurjective().shouldBeTrue()
    }
})
