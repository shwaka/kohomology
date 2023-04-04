package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ProductSimplicialComplexTest : FreeSpec({
    tags(simplicialComplexTag)

    "check homology of ∂Δ^2 × ∂Δ^2" {
        val boundaryDelta2 = boundaryDelta(2)
        val product = productOf(boundaryDelta2, boundaryDelta2)
        val dgVectorSpace = product.dgVectorSpace(DenseMatrixSpaceOverRational)
        forAll(
            row(0, 1),
            row(1, 2),
            row(2, 1),
            row(3, 0),
            row(4, 0),
        ) { minusDegree, expectedDim ->
            dgVectorSpace.cohomology[-minusDegree].dim shouldBe expectedDim
        }
    }
})
