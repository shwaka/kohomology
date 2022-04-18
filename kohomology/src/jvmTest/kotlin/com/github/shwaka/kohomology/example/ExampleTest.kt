package com.github.shwaka.kohomology.example

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.rationalTag
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import io.kotest.core.NamedTag
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeScope
import io.kotest.matchers.shouldBe

val exampleTag = NamedTag("Example")

class ExampleTest : FreeSpec({
    tags(exampleTag, rationalTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    "compare sphere and sphereWithMultiDegree in even dim" - {
        val dim = 4
        compare(
            sphere(matrixSpace, dim),
            sphereWithMultiDegree(matrixSpace, dim),
            30
        )
    }
    "compare sphere and sphereWithMultiDegree in odd dim" - {
        val dim = 3
        compare(
            sphere(matrixSpace, dim),
            sphereWithMultiDegree(matrixSpace, dim),
            30
        )
    }
    "compare complexProjectiveSpace and complexProjectiveSpaceWithMultiDegree" - {
        val n = 4
        compare(
            complexProjectiveSpace(matrixSpace, n),
            complexProjectiveSpaceWithMultiDegree(matrixSpace, n),
            30
        )
    }
    "compare pullbackOfHopfFibrationOverS4 and pullbackOfHopfFibrationOverS4WithMultiDegree" - {
        compare(
            pullbackOfHopfFibrationOverS4(matrixSpace),
            pullbackOfHopfFibrationOverS4WithMultiDegree(matrixSpace),
            30
        )
    }
})

suspend inline fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> FreeScope.compare(
    freeDGAlgebra: FreeDGAlgebra<IntDegree, I, S, V, M>,
    freeDGAlgebraWithMultiDegree: FreeDGAlgebra<MultiDegree, I, S, V, M>,
    maxDegree: Int,
) {
    "dimensions should be the same" {
        (0..maxDegree).forAll { degree ->
            freeDGAlgebraWithMultiDegree.cohomology.getBasisForAugmentedDegree(degree).size shouldBe
                freeDGAlgebra.cohomology.getBasisForAugmentedDegree(degree).size
        }
    }
}
