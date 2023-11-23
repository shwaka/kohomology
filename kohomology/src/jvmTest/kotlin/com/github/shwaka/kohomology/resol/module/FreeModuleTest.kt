package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FreeModuleTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val coefficientAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val generatingBasisNames = listOf("x", "y").map { StringBasisName(it) }
    val freeModule = FreeModule(coefficientAlgebra, generatingBasisNames)

    val (e, t1, t2) = coefficientAlgebra.getBasis()
    val (x, y) = freeModule.getGeneratingBasis()

    "test freeModule.generatingBasisNames" {
        freeModule.generatingBasisNames shouldBe generatingBasisNames
    }

    "test freeModule.fromGeneratingBasisName" {
        freeModule.fromGeneratingBasisName(StringBasisName("x")) shouldBe x
    }

    "listOf(x, y) should not be a basis" {
        val freeModuleBasis = freeModule.context.run {
            listOf(x, y)
        }
        freeModule.underlyingVectorSpace.isBasis(freeModuleBasis, matrixSpace).shouldBeFalse()
    }

    "listOf(x, y, t1*x, t1*y, t2*x, t2*y) should be a basis" {
        val freeModuleBasis = freeModule.context.run {
            listOf(x, y, t1 * x, t1 * y, t2 * x, t2 * y)
        }
        freeModule.underlyingVectorSpace.isBasis(freeModuleBasis, matrixSpace).shouldBeTrue()
    }

    "(e+t1)*x should be (x + t1*x)" {
        freeModule.context.run {
            freeModule.coefficientAlgebra.context.run {
                ((e + t1) * x) shouldBe (x + t1 * x)
            }
        }
    }

    "t1*(t1*(t1*y)) should be y" {
        freeModule.context.run {
            freeModule.coefficientAlgebra.context.run {
                (t1 * (t1 * (t1 * y))) shouldBe y
            }
        }
    }

    "t1*y should be different from y" {
        freeModule.context.run {
            freeModule.coefficientAlgebra.context.run {
                (t1 * y) shouldNotBe y
            }
        }
    }
})
