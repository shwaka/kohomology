package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> augmentationTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    "test Augmentation with MatrixSpace $matrixSpace" - {
        val monoid = CyclicGroup(3)
        val monoidRing = MonoidRing(monoid, matrixSpace)
        val augmentation = Augmentation(monoidRing)

        "augmentation should be surjective" {
            augmentation.underlyingLinearMap.isSurjective().shouldBeTrue()
        }

        "augmentation should send each basis element to 1" {
            monoidRing.getBasis().forAll { g ->
                augmentation(g) shouldBe augmentation.target.getBasis().first()
            }
        }
    }
}

class AugmentationTest : FreeSpec({
    tags(algebraTag)

    include(augmentationTest(SparseMatrixSpaceOverRational))
    include(augmentationTest(SparseMatrixSpaceOverF2))
})
