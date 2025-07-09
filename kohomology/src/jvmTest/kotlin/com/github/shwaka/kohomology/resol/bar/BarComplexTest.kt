package com.github.shwaka.kohomology.resol.bar

import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF7
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.pow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BarComplexTest : FreeSpec({
    tags(barTag)

    "compute homology of Z/2" - {
        val cyclicGroup = CyclicGroup(2)

        "over F2" {
            val dgVectorSpace = barComplex(cyclicGroup, SparseMatrixSpaceOverF2)
            (1..5).forAll { degree ->
                dgVectorSpace[degree].dim shouldBe 0
            }
            (0..5).forAll { n ->
                dgVectorSpace[-n].dim shouldBe 2.pow(n)
                dgVectorSpace.cohomology[-n].dim shouldBe 1
            }
        }

        "over F3" {
            val dgVectorSpace = barComplex(cyclicGroup, SparseMatrixSpaceOverF3)
            (1..5).forAll { degree ->
                dgVectorSpace[degree].dim shouldBe 0
            }
            (0..5).forAll { n ->
                dgVectorSpace[-n].dim shouldBe 2.pow(n)
                dgVectorSpace.cohomology[-n].dim shouldBe when (n) {
                    0 -> 1
                    else -> 0
                }
            }
        }

        "over Q" {
            val dgVectorSpace = barComplex(cyclicGroup, SparseMatrixSpaceOverRational)
            (1..5).forAll { degree ->
                dgVectorSpace[degree].dim shouldBe 0
            }
            (0..5).forAll { n ->
                dgVectorSpace[-n].dim shouldBe 2.pow(n)
                dgVectorSpace.cohomology[-n].dim shouldBe when (n) {
                    0 -> 1
                    else -> 0
                }
            }
        }
    }

    "compute homology of Z/7" - {
        val cyclicGroup = CyclicGroup(7)

        "over F7" {
            val dgVectorSpace = barComplex(cyclicGroup, SparseMatrixSpaceOverF7)
            (1..5).forAll { degree ->
                dgVectorSpace[degree].dim shouldBe 0
            }
            (0..2).forAll { n ->
                dgVectorSpace[-n].dim shouldBe 7.pow(n)
                dgVectorSpace.cohomology[-n].dim shouldBe 1
            }
        }
    }

    "print basis of homology of Z/2" {
        val cyclicGroup = CyclicGroup(2)
        val dgVectorSpace = barComplex(cyclicGroup, SparseMatrixSpaceOverF2)
        val cohomology = dgVectorSpace.cohomology

        cohomology.getBasis(-1).map { it.toString() } shouldBe
            listOf("[[t^1]]")
        cohomology.getBasis(-2).map { it.toString() } shouldBe
            listOf("[[t^0,t^0] + [t^1,t^1]]")
        cohomology.getBasis(-3).map { it.toString() } shouldBe
            listOf("[[t^0,t^0,t^1] + [t^1,t^0,t^0] + [t^1,t^1,t^1]]")
    }
})
