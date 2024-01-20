package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRingMap
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RestrictedModuleMapTest : FreeSpec({
    tags(moduleTag, restrictedModuleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val monoidMap = run {
        val source = CyclicGroup(6)
        val target = CyclicGroup(2)
        val values = (0 until 6).map { CyclicGroupElement(it % 2, 2) }
        FiniteMonoidMap(source, target, values)
    }
    val algebraMap = MonoidRingMap(monoidMap, matrixSpace)
    val sourceAlgebra = algebraMap.source
    val (_, x) = sourceAlgebra.getBasis() // x is the generator
    val targetAlgebra = algebraMap.target
    val (_, y) = targetAlgebra.getBasis() // y is the generator

    val source = run {
        val generatingBasisNames = listOf("v", "w").map { StringBasisName(it) }
        FreeModule(targetAlgebra, generatingBasisNames)
    }
    val (v, w) = source.getGeneratingBasis()
    val restrictedSource = RestrictedModule(source, algebraMap)

    val target = run {
        val generatingBasisNames = listOf("u").map { StringBasisName(it) }
        FreeModule(targetAlgebra, generatingBasisNames)
    }
    val (u) = target.getGeneratingBasis()
    val restrictedTarget = RestrictedModule(target, algebraMap)

    val originalModuleMap = run {
        val yu = target.context.run {
            y * u
        }
        ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = source,
            target = target,
            values = listOf(u, yu),
        )
    }

    val restrictedModuleMap = RestrictedModuleMap(
        source = restrictedSource,
        target = restrictedTarget,
        originalModuleMap = originalModuleMap,
        algebraMap = algebraMap,
    )

    "test invoke" {
        restrictedModuleMap(v) shouldBe u
        restrictedModuleMap(w) shouldBe restrictedTarget.context.run { x * u }
    }

    "test invoke with action" {
        restrictedModuleMap(restrictedSource.context.run { x * v }) shouldBe restrictedTarget.context.run {
            x * restrictedModuleMap(v)
        }
    }
})
