package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.bigRationalTag
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.pow
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

val dgLinearMapOperationTag = NamedTag("DGLinearMapOperation")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> dgLinearMapOperationTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    "test binary operations on DGLinearMap" - {
        val n = 4
        check(n % 2 == 0)
        val indeterminateList = listOf(
            Indeterminate("x1", n),
            Indeterminate("y1", 2 * n - 1),
            Indeterminate("x2", n),
            Indeterminate("y2", 2 * n - 1),
        )
        val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (x1, _, x2, _) ->
            listOf(zeroGVector, x1.pow(2), zeroGVector, x2.pow(2))
        }
        val (x1, y1, x2, y2) = freeDGAlgebra.gAlgebra.generatorList
        val a = 2
        val f1 = freeDGAlgebra.context.run {
            val valueList = listOf(
                a * x1,
                a.pow(2) * y1,
                x2,
                y2,
            )
            freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, valueList)
        }
        val f2 = freeDGAlgebra.context.run {
            val valueList = listOf(
                x1,
                y1,
                a * x2,
                a.pow(2) * y2,
            )
            freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, valueList)
        }
        val t = freeDGAlgebra.getDGAlgebraMap(freeDGAlgebra, listOf(x2, y2, x1, y1))
        "check composition of two DGAlgebraMap's" {
            val tf1: DGAlgebraMap<IntDegree,
                Monomial<IntDegree, StringIndeterminateName>,
                Monomial<IntDegree, StringIndeterminateName>,
                S, V, M> = t * f1 // This should be DGAlgebraMap
            val f2t = f2 * t
            listOf(x1, y1, x2, y2).forAll { gVector ->
                tf1(gVector) shouldBe f2t(gVector)
            }
        }
        "check sum of two DGAlgebraMap's" {
            val sum: DGLinearMap<IntDegree,
                Monomial<IntDegree, StringIndeterminateName>,
                Monomial<IntDegree, StringIndeterminateName>,
                S, V, M> = f1 + f2 // This should be DGLinearMap
            freeDGAlgebra.context.run {
                sum(x1) shouldBe ((a + 1) * x1)
                sum(x2) shouldBe ((a + 1) * x2)
                sum(y1) shouldBe ((a.pow(2) + 1) * y1)
                sum(y2) shouldBe ((a.pow(2) + 1) * y2)
            }
        }
    }
}

class DGLinearMapOperationTest : FreeSpec({
    tags(dgLinearMapOperationTag, bigRationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(dgLinearMapOperationTest(matrixSpace))
})
