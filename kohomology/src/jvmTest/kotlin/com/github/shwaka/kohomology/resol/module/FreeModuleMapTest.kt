package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeModuleMapTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational

    val coefficientAlgebra = MonoidRing(CyclicGroup(3), matrixSpace)
    val (e, t1, _) = coefficientAlgebra.getBasis()

    val generatingBasisNames1 = listOf("x", "y").map { StringBasisName(it) }
    val freeModule1 = FreeModule(coefficientAlgebra, generatingBasisNames1)
    val (x, y) = freeModule1.getGeneratingBasis()

    val generatingBasisNames2 = listOf("u", "v").map { StringBasisName(it) }
    val freeModule2 = FreeModule(coefficientAlgebra, generatingBasisNames2)
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
            coefficientAlgebra.context.run {
                val arg = freeModule1.context.run { (e + t1) * x }
                val expected = freeModule2.context.run { (e + t1) * u }
                freeModuleMap(arg) shouldBe expected
            }
        }
    }
})
