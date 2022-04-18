package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

val cyclicModelTag = NamedTag("CyclicModel")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> cyclicModelOfEvenSphereTest(
    matrixSpace: MatrixSpace<S, V, M>,
    sphereDim: Int
) = freeSpec {
    "[dim=$sphereDim]" - {
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
        val cyclicModel = CyclicModel(sphere)
        val (u, sx, x, sy, y) = cyclicModel.gAlgebra.generatorList

        cyclicModel.context.run {
            "check differential" {
                d(u).isZero().shouldBeTrue()
                d(x) shouldBe (u * sx)
                d(sx).isZero().shouldBeTrue()
                d(y) shouldBe (u * sy + x.pow(2))
                d(sy) shouldBe (-2 * x * sx)
            }
            fun expectedCohomologyBasis(degree: Int): List<GVector<IntDegree, Monomial<IntDegree, CopiedName<IntDegree, StringIndeterminateName>>, S, V>> {
                val basisFromU = if (degree % 2 == 0) {
                    listOf(u.pow(degree / 2))
                } else {
                    emptyList()
                }
                val basisFromSxAndSy = if ((degree - sphereDim + 1) % (sphereDim * 2 - 2) == 0) {
                    val exponent = (degree - sphereDim + 1) / (sphereDim * 2 - 2)
                    listOf(sx * sy.pow(exponent))
                } else {
                    emptyList()
                }
                return basisFromU + basisFromSxAndSy
            }
            "check cohomology dimension" {
                (0 until sphereDim * 5).forAll { degree ->
                    cyclicModel.cohomology[degree].dim shouldBe expectedCohomologyBasis(degree).size
                }
            }
            "check basis of cohomology" {
                (0 until sphereDim * 5).forAll { degree ->
                    val basis = expectedCohomologyBasis(degree).map { it.cohomologyClass() }
                    cyclicModel.cohomology.isBasis(basis, degree).shouldBeTrue()
                }
            }
            "check suspension" {
                val s = cyclicModel.suspension
                s(u).isZero().shouldBeTrue()
                s(x) shouldBe sx
                s(y) shouldBe sy
                s(sx).isZero().shouldBeTrue()
                s(sy).isZero().shouldBeTrue()
                s(u * x) shouldBe (u * sx)
                s(x * y) shouldBe (sx * y + x * sy)
            }
            "check inclusion" {
                val i = cyclicModel.inclusion
                val (x_, y_) = sphere.gAlgebra.generatorList
                i(x_) shouldBe x
                i(y_) shouldBe y
            }
        }
    }
}

class CyclicModelTest : FreeSpec({
    tags(cyclicModelTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(cyclicModelOfEvenSphereTest(matrixSpace, 2))
    include(cyclicModelOfEvenSphereTest(matrixSpace, 4))
})
