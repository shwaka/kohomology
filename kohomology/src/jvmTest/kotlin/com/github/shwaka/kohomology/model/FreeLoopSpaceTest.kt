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
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import mu.KotlinLogging

val freeLoopSpaceTag = NamedTag("FreeLoopSpace")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeLoopSpaceOfEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    val logger = KotlinLogging.logger {}
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
    val freeLoopSpace = FreeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

    freeLoopSpace.context.run {
        "[dim=$sphereDim] check differential" {
            d(x).isZero().shouldBeTrue()
            d(y) shouldBe (x * x)
            d(sx).isZero().shouldBeTrue()
            d(sy) shouldBe (-2 * x * sx)
        }
        "[dim=$sphereDim] check cohomology" {
            for (degree in 0 until sphereDim * 5) {
                val expectedDim = when {
                    degree == 0 -> 1
                    ((degree % (sphereDim - 1) == 0) && ((degree / (sphereDim - 1)) % 2 == 1)) -> 1
                    (((degree - 1) % (sphereDim - 1) == 0) && (((degree - 1) / (sphereDim - 1)) % 2 == 1)) -> 1
                    else -> 0
                }
                freeLoopSpace.cohomology[degree].dim shouldBe expectedDim
            }
        }
        "[dim=$sphereDim] check basis of cohomology" {
            for (n in 0 until 5) {
                val degree = (2 * n + 1) * (sphereDim - 1)
                logger.debug { "check basis (1) n = $n, degree = $degree" }
                val basis = listOf(freeLoopSpace.cohomologyClassOf(sx * sy.pow(n)))
                freeLoopSpace.cohomology.isBasis(basis, degree).shouldBeTrue()
            }
            for (n in 0 until 5) {
                val degree = (2 * n + 1) * (sphereDim - 1) + 1
                logger.debug { "check basis (2) n = $n, degree = $degree" }
                val basis = listOf(
                    freeLoopSpace.cohomologyClassOf(
                        if (n == 0)
                            x
                        else
                            2 * n * y * sx * sy.pow(n - 1) + x * sy.pow(n)
                    )
                )
                freeLoopSpace.cohomology.isBasis(basis, degree).shouldBeTrue()
            }
        }
    }
}

class FreeLoopSpaceTest : FreeSpec({
    tags(freeLoopSpaceTag, bigRationalTag)

    include(freeLoopSpaceOfEvenSphereTest(DenseMatrixSpaceOverBigRational, 2))
    include(freeLoopSpaceOfEvenSphereTest(DenseMatrixSpaceOverBigRational, 4))
})
