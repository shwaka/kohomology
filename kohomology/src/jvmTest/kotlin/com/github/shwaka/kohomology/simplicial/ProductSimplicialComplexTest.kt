package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.specific.DenseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.specific.f2.SetMatrixSpaceOverF2Boolean
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

    "check homology of RP^2 × ∂Δ^2 with rational coefficient" {
        val boundaryDelta2 = boundaryDelta(2)
        val product = productOf(projectivePlane(), boundaryDelta2)
        val dgVectorSpace = product.dgVectorSpace(SparseMatrixSpaceOverRational)
        forAll(
            row(0, 1),
            row(1, 1),
            row(2, 0),
            row(3, 0),
        ) { minusDegree, expectedDim ->
            dgVectorSpace.cohomology[-minusDegree].dim shouldBe expectedDim
        }
    }

    "check homology of RP^2 × ∂Δ^2 with Z/2 coefficient" {
        val boundaryDelta2 = boundaryDelta(2)
        val product = productOf(projectivePlane(), boundaryDelta2)
        val dgVectorSpace = product.dgVectorSpace(SetMatrixSpaceOverF2Boolean)
        forAll(
            row(0, 1),
            row(1, 2),
            row(2, 2),
            row(3, 1),
        ) { minusDegree, expectedDim ->
            dgVectorSpace.cohomology[-minusDegree].dim shouldBe expectedDim
        }
    }
})
