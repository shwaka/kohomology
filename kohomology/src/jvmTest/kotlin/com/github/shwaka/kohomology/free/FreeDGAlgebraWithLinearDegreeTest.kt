package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.LinearDegreeMonoid
import com.github.shwaka.kohomology.dg.degree.degreeTag
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.isEven
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeDGAlgebraWithLinearDegreeTest : FreeSpec({
    tags(freeDGAlgebraTag, degreeTag)
    "FreeDGAlgebra with LinearDegree" - {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("n", 1),
        )
        val degreeMonoid = LinearDegreeMonoid(degreeIndeterminateList)
        val (n) = degreeMonoid.generatorList
        val indeterminateList = degreeMonoid.context.run {
            listOf(
                GeneralizedIndeterminate("x", 2 * n),
                GeneralizedIndeterminate("y", 4 * n - 1)
            )
        }
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, degreeMonoid, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        freeLoopSpace.cohomology[0].dim shouldBe 1
        for (degree in 1 until 20) {
            freeLoopSpace.cohomology[degree].dim shouldBe 0
        }
        degreeMonoid.context.run {
            for (i in 1 until 10) {
                val degree = i * (2 * n - 1)
                val expectedDim = if (i.isEven()) 0 else 1
                freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
            }
            for (i in 0 until 10) {
                val degree = 2 * n + i * (2 * n - 1)
                val expectedDim = if (i.isEven()) 1 else 0
                freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
            }
        }
    }
})
