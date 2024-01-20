package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FreeModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val coeffAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val generatingBasisNames = listOf("x", "y").map { StringBasisName(it) }
    val freeModule = FreeModule(coeffAlgebra, generatingBasisNames)

    val (e, t1, t2) = coeffAlgebra.getBasis()
    val (x, y) = freeModule.getGeneratingBasis()

    "freeModule.underlyingVectorSpace.dim should be coeffAlgebra.dim * generatingBasisNames.size" {
        freeModule.underlyingVectorSpace.dim shouldBe (coeffAlgebra.dim * generatingBasisNames.size)
    }

    "freeModule.tensorWithBaseField.dim should be generatingBasisNames.size" {
        freeModule.tensorWithBaseField.dim shouldBe generatingBasisNames.size
    }

    "test freeModule.generatingBasisNames" {
        freeModule.generatingBasisNames shouldBe generatingBasisNames
    }

    "test freeModule.fromGeneratingBasisName" {
        freeModule.fromGeneratingBasisName(StringBasisName("x")) shouldBe x
    }

    "listOf(x, y) should not be a basis" {
        val freeModuleBasisName = freeModule.context.run {
            listOf(x, y)
        }
        freeModule.underlyingVectorSpace.isBasis(freeModuleBasisName, matrixSpace).shouldBeFalse()
    }

    "listOf(x, y, t1*x, t1*y, t2*x, t2*y) should be a basis" {
        val freeModuleBasisName = freeModule.context.run {
            listOf(x, y, t1 * x, t1 * y, t2 * x, t2 * y)
        }
        freeModule.underlyingVectorSpace.isBasis(freeModuleBasisName, matrixSpace).shouldBeTrue()
    }

    "(e+t1)*x should be (x + t1*x)" {
        freeModule.context.run {
            freeModule.coeffAlgebra.context.run {
                ((e + t1) * x) shouldBe (x + t1 * x)
            }
        }
    }

    "t1*(t1*(t1*y)) should be y" {
        freeModule.context.run {
            freeModule.coeffAlgebra.context.run {
                (t1 * (t1 * (t1 * y))) shouldBe y
            }
        }
    }

    "t1*y should be different from y" {
        freeModule.context.run {
            freeModule.coeffAlgebra.context.run {
                (t1 * y) shouldNotBe y
            }
        }
    }

    "freeModule.findSmallGenerator() should be [x, y]" {
        val smallGenerator = freeModule.findSmallGenerator()
        smallGenerator shouldHaveSize 2
        smallGenerator shouldBe listOf(x, y)
    }

    "test freeModule.projection" {
        val p = freeModule.projection
        val (x0, y0) = freeModule.tensorWithBaseField.getBasis()
        freeModule.context.run {
            p(x) shouldBe x0
            p(t1 * x) shouldBe x0
            p(t2 * x) shouldBe x0
            p(y) shouldBe y0
            p(t1 * y) shouldBe y0
            p(t2 * y) shouldBe y0
        }
    }

    "test freeModule.inclusion" {
        val i = freeModule.inclusion
        val (x0, y0) = freeModule.tensorWithBaseField.getBasis()
        freeModule.context.run {
            i(x0) shouldBe x
            i(y0) shouldBe y
        }
    }
})
