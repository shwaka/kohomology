package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.withTrivialDifferential
import com.github.shwaka.kohomology.example.sphere
import com.github.shwaka.kohomology.forAll
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

val minimalModelTag = NamedTag("MinimalModel")

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> minimalModelTest(matrixSpace: MatrixSpace<S, V, M>) = freeSpec {
    for (n in listOf(1, 2, 10)) {
        "minimal model of ${2 * n}-sphere" - {
            val cohomologyAsDGA = sphere(matrixSpace, 2 * n).cohomology.withTrivialDifferential()
            val isomorphismUpTo = 5 * n
            val minimalModel = MinimalModel(
                targetDGAlgebra = cohomologyAsDGA,
                isomorphismUpTo = isomorphismUpTo,
            )
            "the number of generators should be 2" {
                minimalModel.freeDGAlgebra.generatorList.size shouldBe 2
            }
            "the degrees of generators should be 2n and 4n-1" {
                minimalModel.freeDGAlgebra.generatorList[0].degree shouldBe IntDegree(2 * n)
                minimalModel.freeDGAlgebra.generatorList[1].degree shouldBe IntDegree(4 * n - 1)
            }
            "should be quasi-isomorphism up to degree $isomorphismUpTo" {
                (0..isomorphismUpTo).forAll { degree ->
                    minimalModel.dgAlgebraMap.inducedMapOnCohomology[degree].isIsomorphism().shouldBeTrue()
                }

            }
        }
    }
}

class MinimalModelTest : FreeSpec({
    tags(minimalModelTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(minimalModelTest(matrixSpace))
})
