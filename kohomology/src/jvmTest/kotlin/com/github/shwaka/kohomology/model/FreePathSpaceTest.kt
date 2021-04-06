package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import com.github.shwaka.kohomology.util.list.* // ktlint-disable no-wildcard-imports
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

val freePathSpaceTag = NamedTag("FreePathSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freePathSpaceOfEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = stringSpec {
    if (sphereDim <= 0)
        throw IllegalArgumentException("The dimension of a sphere must be positive")
    if (sphereDim % 2 == 1)
        throw IllegalArgumentException("The dimension of a sphere must be even in this test")
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }
    val freePathSpace = freePathSpace(sphere)
    val (x1, y1, x2, y2, sx, sy) = freePathSpace.gAlgebra.generatorList

    "[dim=$sphereDim] check differential" {
        freePathSpace.context.run {
            d(sx) shouldBe (x2 - x1)
            d(sy) shouldBe (y2 - y1 - (x2 + x1) * sx)
        }
    }
}

class FreePathSpaceTest : StringSpec({
    tags(freePathSpaceTag, bigRationalTag)

    include(freePathSpaceOfEvenSphereTest(DenseMatrixSpaceOverBigRational, 2))
})
