package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.Augmentation
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidFromList
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.math.min

class QuotModuleAlongAlgebraMapTest : FreeSpec({
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
    val quotModule = QuotModuleAlongAlgebraMap(totalModule, augmentation)

    "monoid should satisfy the axioms" {
        shouldNotThrow<IllegalStateException> {
            monoid.checkMonoidAxioms()
        }
    }

    "quotModule should have dimension 1" {
        quotModule.underlyingVectorSpace.dim shouldBe 1
    }

    "quotModule.projection should be surjective" {
        quotModule.projection.underlyingLinearMap.isSurjective().shouldBeTrue()
    }

    "quotModule.section should be injective" {
        quotModule.section.isInjective().shouldBeTrue()
    }
})
