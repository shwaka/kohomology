package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.dg.degree.degreeTag
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.isEven
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FreeDGAlgebraWithMultiDegreeTest : FreeSpec({
    tags(freeDGAlgebraTag, degreeTag)
    "FreeDGAlgebra with MultiDegree" - {
        val degreeIndeterminateList = listOf(
            DegreeIndeterminate("n", 1),
        )
        val degreeGroup = MultiDegreeGroup(degreeIndeterminateList)
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
            (1 until 20).forAll { degree ->
                freeLoopSpace.gAlgebra[degree].dim shouldBe 0
                freeLoopSpace.cohomology[degree].dim shouldBe 0
            }
        }
        degreeGroup.context.run {
            "non-trivial cohomology" {
                (1 until 10).forAll { i ->
                    val degree = i * (2 * n - 1)
                    val expectedDim = if (i.isEven()) 0 else 1
                    freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
                }
            }
            "trivial cohomology (with non-zero cochain)" {
                (0 until 10).forAll { i ->
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
            (0 until 20).forAll { degree ->
                // basis for DGA
                sphere.gAlgebra.getBasisForAugmentedDegree(degree).size shouldBe
                    intSphere.gAlgebra.getBasis(degree).size
                freeLoopSpace.gAlgebra.getBasisForAugmentedDegree(degree).size shouldBe
                    intFreeLoopSpace.gAlgebra.getBasis(degree).size
                // basis for cohomology
                sphere.cohomology.getBasisForAugmentedDegree(degree).size shouldBe
                    intSphere.cohomology.getBasis(degree).size
                freeLoopSpace.cohomology.getBasisForAugmentedDegree(degree).size shouldBe
                    intFreeLoopSpace.cohomology.getBasis(degree).size
            }
        }
    }
})
