package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.forAll
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.ModuleMapFromFreeModule
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF3
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> testWithScalarMultiplication(
    order: Int,
    scalar: Int,
    matrixSpace: MatrixSpace<S, V, M>,
    maxDeg: Int = 10,
) = freeSpec {
    require(maxDeg > 0)
    val field = matrixSpace.field
    val p = field.characteristic
    val freeResol = freeResolutionOverCyclicGroup(order, matrixSpace)
    val chainMap = ChainMapFromFreeComplex(
        source = freeResol,
        target = freeResol,
        name = "f",
    ) { degree: IntDegree ->
        val module = freeResol.getModule(degree)
        val values = module.underlyingVectorSpace.context.run {
            module.getGeneratingBasis().map {
                scalar * it
            }
        }
        ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = module,
            target = module,
            values = values,
        )
    }

    "test multiplication by $scalar on the free resolution over $field[Z/$order]" - {
        "checkChainMapAxioms should not throw IllegalStateException" {
            shouldNotThrow<IllegalStateException> {
                chainMap.checkChainMapAxioms(-maxDeg, maxDeg)
            }
        }

        "it should be zero or isomorphic depending on the characteristic" {
            (-maxDeg..maxDeg).forAll { n ->
                val degree = IntDegree(n)
                if (scalar % p == 0) {
                    chainMap.getModuleMap(degree).underlyingLinearMap.isZero().shouldBeTrue()
                } else {
                    chainMap.getModuleMap(degree).underlyingLinearMap.isIsomorphism().shouldBeTrue()
                }
            }
        }

        "test invoke" {
            (-maxDeg..0).forAll { n ->
                val x = freeResol.underlyingDGVectorSpace.getBasis(n)[0]
                chainMap(x) shouldBe freeResol.underlyingDGVectorSpace.context.run {
                    scalar * x
                }
            }
        }
    }
}

class ChainMapFromFreeComplexTest : FreeSpec({
    tags(chainMapTag)

    include(testWithScalarMultiplication(order = 2, scalar = 2, matrixSpace = SparseMatrixSpaceOverF2))
    include(testWithScalarMultiplication(order = 2, scalar = 3, matrixSpace = SparseMatrixSpaceOverF2))
    include(testWithScalarMultiplication(order = 3, scalar = 2, matrixSpace = SparseMatrixSpaceOverF3))
    include(testWithScalarMultiplication(order = 3, scalar = 3, matrixSpace = SparseMatrixSpaceOverF3))
})
