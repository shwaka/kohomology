package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeModuleMapAlongAlgebraMapTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational

    val sourceOrder = 6
    val targetOrder = 2

    val sourceGroup = CyclicGroup(sourceOrder)
    val sourceAlgebra = MonoidRing(sourceGroup, matrixSpace)
    val sourceModule = FreeModule(sourceAlgebra, listOf("x", "y").map { StringBasisName(it) })
    val (x, y) = sourceModule.getGeneratingBasis()

    val targetGroup = CyclicGroup(targetOrder)
    val targetAlgebra = MonoidRing(targetGroup, matrixSpace)
    val targetModule = FreeModule(targetAlgebra, listOf("u").map { StringBasisName(it) })
    val (u) = targetModule.getGeneratingBasis()
    val t = targetAlgebra.getBasis()[1]
    val tPlusUnit = targetAlgebra.context.run { t + unit }
    val tPlusUnitTimesU = targetModule.context.run { tPlusUnit * u }

    val groupMap = run {
        val values = sourceGroup.elements.map { cyclicGroupElement ->
            CyclicGroupElement(
                value = cyclicGroupElement.value % targetOrder,
                order = targetOrder,
            )
        }
        FiniteMonoidMap(sourceGroup, targetGroup, values)
    }
    val algebraMap = AlgebraMap.fromFiniteMonoidMap(
        groupMap,
        matrixSpace,
        source = sourceAlgebra,
        target = targetAlgebra,
    )

    val freeModuleMap = FreeModuleMapAlongAlgebraMap.fromValuesOnGeneratingBasis(
        source = sourceModule,
        target = targetModule,
        algebraMap = algebraMap,
        values = listOf(u, tPlusUnitTimesU),
    )

    "check values on generating basis" {
        freeModuleMap(x) shouldBe u
        freeModuleMap(y) shouldBe tPlusUnitTimesU
    }

    "check values of freeModuleMap.tensorWithBaseField" {
        val (x0, y0) = sourceModule.tensorWithBaseField.getBasis()
        val (u0) = targetModule.tensorWithBaseField.getBasis()
        val f = freeModuleMap.tensorWithBaseField
        f(x0) shouldBe u0
        f(y0) shouldBe targetModule.tensorWithBaseField.context.run {
            2 * u0
        }
    }
})
