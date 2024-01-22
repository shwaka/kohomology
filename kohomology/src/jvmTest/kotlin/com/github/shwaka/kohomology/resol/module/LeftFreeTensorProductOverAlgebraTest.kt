package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.algebra.OpAlgebra
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverF2
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> leftFreeTensorProductOverAlgebraTest(
    matrixSpace: MatrixSpace<S, V, M>,
) = freeSpec {
    val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
    val opAlgebra = OpAlgebra(coeffAlgebra)
    val (_, t) = coeffAlgebra.getBasis()
    val rightModule = run {
        // Z/2 acting on Q{x, y} by
        //   x*t = y, y*t = x
        val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val (x, y) = underlyingVectorSpace.getBasis()
        val action = ValueBilinearMap(
            source1 = opAlgebra,
            source2 = underlyingVectorSpace,
            target = underlyingVectorSpace,
            matrixSpace = matrixSpace,
            values = listOf(
                listOf(x, y), // one*(-)
                listOf(y, x), // t*(-)
            )
        )
        Module(matrixSpace, underlyingVectorSpace, opAlgebra, action)
    }
    val leftModule = FreeModule(coeffAlgebra, listOf("a", "b", "c").map(::StringBasisName))
    val tensorProduct = LeftFreeTensorProductOverAlgebra(rightModule, leftModule)

    "test LeftFreeTensorProductOverAlgebra over $matrixSpace" - {
        "tensorProduct.dim should be the product of rightModule.dim and leftModule.generatingBasisNames.size" {
            val expected = rightModule.underlyingVectorSpace.dim * leftModule.generatingBasisNames.size
            tensorProduct.dim shouldBe expected
        }

        val (x, y) = rightModule.underlyingVectorSpace.getBasis()
        val (a, b, c) = leftModule.getGeneratingBasis()
        val f = tensorProduct.tensorProductMap
        val lContext = leftModule.context
        val rContext = rightModule.context
        val tContext = tensorProduct.context

        "test tensorProduct.tensorProductMap" {
            f(x, a) shouldBe tensorProduct.fromBasisName(
                LeftFreeTensorProductBasisName(StringBasisName("x"), StringBasisName("a"))
            )
            f(x, lContext.run { a + b }) shouldBe tContext.run { f(x, a) + f(x, b) }
            f(y, lContext.run { t * c }) shouldBe f(rContext.run { t * y }, c)
        }

        "asPairList(-).map { tensorProductMap(it.first, it.second) }.sum() should be identity" {
            tContext.run {
                listOf(
                    f(x, a),
                    f(rContext.run { x + y }, lContext.run { a + t * c }),
                    f(rContext.run { x - t * y }, b),
                ).forAll { element ->
                    tensorProduct.asPairList(element).map {
                        f(it.first, it.second)
                    }.sum() shouldBe element
                }
            }
        }

        "inducedMapOf(tensorProduct, id, id) should be the identity on tensorProduct" {
            val idTimesId = tensorProduct.inducedMapOf(
                target = tensorProduct,
                rightModuleMap = rightModule.getIdentity(),
                leftModuleMap = leftModule.getIdentity(),
            )
            listOf(
                f(x, a),
                f(rContext.run { x + y }, lContext.run { a + t * c }),
                f(rContext.run { x - t * y }, b),
            ).forAll { element ->
                idTimesId(element) shouldBe element
            }
        }
    }
}

class LeftFreeTensorProductOverAlgebraTest : FreeSpec({
    tags(moduleTag)

    include(leftFreeTensorProductOverAlgebraTest(SparseMatrixSpaceOverRational))
    include(leftFreeTensorProductOverAlgebraTest(SparseMatrixSpaceOverF2))
})
