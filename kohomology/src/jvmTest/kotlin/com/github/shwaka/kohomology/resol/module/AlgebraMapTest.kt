package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.algebra.MonoidRingMap
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AlgebraMapTest : FreeSpec({
    tags(algebraTag)

    "test AlgebraMap induced by FiniteMonoidMap" - {
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

        "test invoke" {
            algebraMap(x) shouldBe y
            val xSquare = sourceAlgebra.context.run { x.pow(2) }
            algebraMap(xSquare) shouldBe targetAlgebra.unit
        }

        "invoke should throw IllegalArgumentException when invalid vector is given" {
            shouldThrow<IllegalArgumentException> {
                algebraMap(y)
            }
        }
    }
})
