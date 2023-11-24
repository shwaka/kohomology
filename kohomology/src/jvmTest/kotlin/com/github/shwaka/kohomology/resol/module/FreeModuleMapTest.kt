package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class FreeModuleMapTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational

    val coeffAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val (e, t1, t2) = coeffAlgebra.getBasis()

    val generatingBasisNames1 = listOf("x", "y").map { StringBasisName(it) }
    val freeModule1 = FreeModule(coeffAlgebra, generatingBasisNames1)
    val (x, y) = freeModule1.getGeneratingBasis()

    val generatingBasisNames2 = listOf("u", "v").map { StringBasisName(it) }
    val freeModule2 = FreeModule(coeffAlgebra, generatingBasisNames2)
    val (u, v) = freeModule2.getGeneratingBasis()

    "test with the canonical isomorphism" - {
        val freeModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
            source = freeModule1,
            target = freeModule2,
            values = listOf(u, v),
        )
        "check values on generating basis" {
            freeModuleMap(x) shouldBe u
            freeModuleMap(y) shouldBe v
        }
        "check values on basis with coefficient" {
            coeffAlgebra.context.run {
                val arg = freeModule1.context.run { (e + t1) * x }
                val expected = freeModule2.context.run { (e + t1) * u }
                freeModuleMap(arg) shouldBe expected
            }
        }
        "check values of freeModuleMap.inducedMapWithoutCoeff" {
            val (x0, y0) = freeModule1.vectorSpaceWithoutCoeff.getBasis()
            val (u0, v0) = freeModule2.vectorSpaceWithoutCoeff.getBasis()
            val f = freeModuleMap.inducedMapWithoutCoeff
            f(x0) shouldBe u0
            f(y0) shouldBe v0
        }
        "freeModuleMap.kernel() should be zero" {
            freeModuleMap.kernel().underlyingVectorSpace.dim shouldBe 0
        }
    }

    "test with a map defined with coeff" - {
        val freeModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
            source = freeModule1,
            target = freeModule2,
            values = freeModule2.context.run {
                coeffAlgebra.context.run {
                    listOf((e + t1 + t2) * u, -t1 * v)
                }
            },
        )
        "check values on generating basis" {
            freeModule2.context.run {
                coeffAlgebra.context.run {
                    freeModuleMap(x) shouldBe (e + t1 + t2) * u
                    freeModuleMap(y) shouldBe (-t1 * v)
                }
            }
        }
        "check values of freeModuleMap.inducedMapWithoutCoeff" {
            val (x0, y0) = freeModule1.vectorSpaceWithoutCoeff.getBasis()
            val (u0, v0) = freeModule2.vectorSpaceWithoutCoeff.getBasis()
            val f = freeModuleMap.inducedMapWithoutCoeff
            freeModule2.vectorSpaceWithoutCoeff.context.run {
                f(x0) shouldBe (3 * u0)
                f(y0) shouldBe (-v0)
            }
        }
    }

    "test with a map with non-zero kernel" - {
        val freeModuleMap = FreeModuleMap.fromValuesOnGeneratingBasis(
            source = freeModule1,
            target = freeModule2,
            values = freeModule2.context.run {
                listOf(u, -u)
            },
        )

        "check values" {
            freeModuleMap(x) shouldBe u
            freeModuleMap(y) shouldBe freeModule2.context.run { -u }
            freeModuleMap(freeModule1.context.run { t1 * x }) shouldBe
                freeModule2.context.run { t1 * u }
        }

        "freeModuleMap.kernel() should be of dim 3" {
            freeModuleMap.kernel().underlyingVectorSpace.dim shouldBe 3
        }

        "freeModuleMap.kernel().findSmallGenerator() should be listOf(x+y)" {
            val kernel = freeModuleMap.kernel()
            val r = kernel.retraction
            val smallGenerator = kernel.findSmallGenerator()
            smallGenerator.shouldHaveSize(1)
            smallGenerator shouldBe freeModule1.context.run { listOf(r(x + y)) }
        }
    }
})
