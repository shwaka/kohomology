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
import io.kotest.matchers.booleans.shouldBeTrue
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
    val freePathSpace = FreePathSpace(sphere)
    val (x1, y1, x2, y2, sx, sy) = freePathSpace.gAlgebra.generatorList

    "[S^$sphereDim] check differential" {
        freePathSpace.context.run {
            d(sx) shouldBe (x2 - x1)
            d(sy) shouldBe (y2 - y1 - (x2 + x1) * sx)
        }
    }
    "[S^$sphereDim] check that inclusions are isomorphism on cohomology" {
        val cohomologyInclusion1 = freePathSpace.inclusion1.inducedMapOnCohomology()
        val cohomologyInclusion2 = freePathSpace.inclusion2.inducedMapOnCohomology()
        for (degree in 0 until sphereDim * 5) {
            cohomologyInclusion1[degree].isIsomorphism().shouldBeTrue()
            cohomologyInclusion2[degree].isIsomorphism().shouldBeTrue()
        }
    }
    "[S^$sphereDim] check that projection is isomorphism on cohomology" {
        val cohomologyProjection = freePathSpace.projection.inducedMapOnCohomology()
        for (degree in 0 until sphereDim * 5) {
            cohomologyProjection[degree].isIsomorphism().shouldBeTrue()
        }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freePathSpaceOfCPnTest(
    matrixSpace: MatrixSpace<S, V, M>,
    n: Int
) = stringSpec {
    if (n <= 0)
        throw IllegalArgumentException("The complex dimension n of CP^n must be positive")
    val indeterminateList = listOf(
        Indeterminate("c", 2),
        Indeterminate("x", 2 * n + 1)
    )
    val cpn = FreeDGAlgebra(matrixSpace, indeterminateList) { (c, _) ->
        listOf(zeroGVector, c.pow(n + 1))
    }
    val (c, _) = cpn.gAlgebra.generatorList
    val freePathSpace = FreePathSpace(cpn)
    val (c1, x1, c2, x2, sc, sx) = freePathSpace.gAlgebra.generatorList

    "[CP^$n] check differential" {
        freePathSpace.context.run {
            d(sc) shouldBe (c2 - c1)
            val dsxExpected = x2 - x1 -
                (0..n).map { i -> c1.pow(i) * c2.pow(n - i) * sc }.reduce { a, b -> a + b }
            d(sx) shouldBe dsxExpected
        }
    }
    "[CP^$n] check that inclusions are isomorphism on cohomology" {
        val cohomologyInclusion1 = freePathSpace.inclusion1.inducedMapOnCohomology()
        val cohomologyInclusion2 = freePathSpace.inclusion2.inducedMapOnCohomology()
        for (degree in 0 until 2 * n + 4) {
            cohomologyInclusion1[degree].isIsomorphism().shouldBeTrue()
            cohomologyInclusion2[degree].isIsomorphism().shouldBeTrue()
        }
    }
    "[CP^$n] check that projection is isomorphism on cohomology" {
        val cohomologyProjection = freePathSpace.projection.inducedMapOnCohomology()
        for (degree in 0 until 2 * n + 4) {
            cohomologyProjection[degree].isIsomorphism().shouldBeTrue()
        }
    }
    "[CP^$n] find cocycle lift" {
        val cocycle = cpn.context.run {
            c.pow(n)
        }
        val lift = freePathSpace.projection.findCocycleLift(cocycle)
        freePathSpace.context.run {
            d(lift).isZero().shouldBeTrue()
        }
        freePathSpace.projection(lift) shouldBe cocycle
    }
}

class FreePathSpaceTest : StringSpec({
    tags(freePathSpaceTag, bigRationalTag)

    include(freePathSpaceOfEvenSphereTest(DenseMatrixSpaceOverBigRational, 2))
    include(freePathSpaceOfCPnTest(DenseMatrixSpaceOverBigRational, 4))
})
