package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.LinearDegreeGroup
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
        val degreeGroup = LinearDegreeGroup(degreeIndeterminateList)
        val (n) = degreeGroup.generatorList
        val indeterminateList = degreeGroup.context.run {
            listOf(
                Indeterminate("x", 2 * n),
                Indeterminate("y", 4 * n - 1)
            )
        }
        val matrixSpace = SparseMatrixSpaceOverBigRational
        val sphere = FreeDGAlgebra(matrixSpace, degreeGroup, indeterminateList) { (x, _) ->
            listOf(zeroGVector, x.pow(2))
        }
        val freeLoopSpace = FreeLoopSpace(sphere)
        "degree 0 should be 1-dim" {
            freeLoopSpace.cohomology[0].dim shouldBe 1
        }
        "positive integer degree should be 0-dim" {
            for (degree in 1 until 20) {
                freeLoopSpace.gAlgebra[degree].dim shouldBe 0
                freeLoopSpace.cohomology[degree].dim shouldBe 0
            }
        }
        degreeGroup.context.run {
            "non-trivial cohomology" {
                for (i in 1 until 10) {
                    val degree = i * (2 * n - 1)
                    val expectedDim = if (i.isEven()) 0 else 1
                    freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
                }
            }
            "trivial cohomology (with non-zero cochain)" {
                for (i in 0 until 10) {
                    val degree = 2 * n + i * (2 * n - 1)
                    val expectedDim = if (i.isEven()) 1 else 0
                    freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
                }
            }
        }
        "getBasisForAugmentedDegree test" {
            val intIndeterminateList = indeterminateList.map { indeterminate ->
                Indeterminate(
                    indeterminate.name,
                    degreeGroup.augmentation(indeterminate.degree)
                )
            }
            val intSphere = FreeDGAlgebra(matrixSpace, IntDegreeGroup, intIndeterminateList) { (x, _) ->
                listOf(zeroGVector, x.pow(2))
            }
            val intFreeLoopSpace = FreeLoopSpace(intSphere)
            for (degree in 0 until 20) {
                sphere.gAlgebra.getBasisForAugmentedDegree(degree).size shouldBe
                    intSphere.gAlgebra.getBasis(degree).size
                freeLoopSpace.gAlgebra.getBasisForAugmentedDegree(degree).size shouldBe
                    intFreeLoopSpace.gAlgebra.getBasis(degree).size
            }
        }
    }
})
