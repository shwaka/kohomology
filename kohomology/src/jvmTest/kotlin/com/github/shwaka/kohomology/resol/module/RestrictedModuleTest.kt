package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue

val restrictedModuleTag = NamedTag("RestrictedModule")

class RestrictedModuleTest : FreeSpec({
    tags(moduleTag, restrictedModuleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    val monoidMap = run {
        val source = CyclicGroup(6)
        val target = CyclicGroup(2)
        val values = (0 until 6).map { CyclicGroupElement(it % 2, 2) }
        FiniteMonoidMap(source, target, values)
    }
    val algebraMap = AlgebraMap.fromFiniteMonoidMap(monoidMap, matrixSpace)
    val sourceAlgebra = algebraMap.source
    val (_, x) = sourceAlgebra.getBasis() // x is the generator
    val targetAlgebra = algebraMap.target
    val (_, y) = targetAlgebra.getBasis() // y is the generator

    val generatingBasisNames = listOf("v", "w").map { StringBasisName(it) }
    val freeModule = FreeModule(targetAlgebra, generatingBasisNames)
    val restrictedModule = RestrictedModule(freeModule, algebraMap)

    val v = restrictedModule.underlyingVectorSpace.getBasis()[0]

    "test action" {
        val lhs = restrictedModule.context.run { x * v }
        val rhs = freeModule.context.run { y * v }
        (lhs == rhs).shouldBeTrue()
    }

    "action should throw IllegalArgumentException if invalid element is given" {
        shouldThrow<IllegalArgumentException> {
            restrictedModule.context.run {
                y * v
            }
        }
    }
})
