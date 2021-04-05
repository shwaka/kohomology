package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverBigRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val freeLoopSpaceTag = NamedTag("FreeLoopSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeLoopSpaceOfEvenSphereTest(
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
    val freeLoopSpace = freeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

    freeLoopSpace.withDGAlgebraContext {
        "[dim=$sphereDim] check differential" {
            d(x).isZero().shouldBeTrue()
            d(y) shouldBe (x * x)
            d(sx).isZero().shouldBeTrue()
            d(sy) shouldBe (-2 * x * sx)
        }
    }
}

class FreeDGAlgebraTest : StringSpec({
    tags(freeLoopSpaceTag, bigRationalTag)

    include(freeLoopSpaceOfEvenSphereTest(DenseMatrixSpaceOverBigRational, 2))
})
