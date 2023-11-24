package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeModuleMapTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational

    val coeffAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val (e, t1, _) = coeffAlgebra.getBasis()

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
    }
})
