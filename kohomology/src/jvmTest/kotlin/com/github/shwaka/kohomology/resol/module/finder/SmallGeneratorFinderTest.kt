package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.TrivialModule
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.booleans.shouldBeFalse

private typealias GetFinder<BA, S, V, M, Alg> =
    (coeffAlgebra: Alg) -> SmallGeneratorFinder<BA, S, V, M, Alg>

private fun <
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > smallGeneratorFinderForMonoidRingTest(
    matrixSpace: MatrixSpace<S, V, M>,
    finderName: String,
    getFinder: GetFinder<CyclicGroupElement, S, V, M, MonoidRing<CyclicGroupElement, S, V, M>>,
) = freeSpec {
    "test SmallGeneratorSelector implementation: $finderName" - {
        val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
        val finder = getFinder(coeffAlgebra)
        val (x, y) = underlyingVectorSpace.getBasis()
        // val (one, t) = coeffAlgebra.getBasis()

        "test with the free module of rank 1" {
            // Z/2 acting on Q{x, y} by
            //   t*x = y, t*y = x
            val action = ValueBilinearMap(
                source1 = coeffAlgebra,
                source2 = underlyingVectorSpace,
                target = underlyingVectorSpace,
                matrixSpace = matrixSpace,
                values = listOf(
                    listOf(x, y), // one*(-)
                    listOf(y, x), // t*(-)
                )
            )
            val module = Module(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
            val smallGenerator = finder.find(module)
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
        }

        "test with trivial module" {
            val vectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("v"))
            val module = TrivialModule(vectorSpace, coeffAlgebra)
            val smallGenerator = finder.find(module)
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
        }
    }
}

class SmallGeneratorFinderTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = SimpleSelector::class.simpleName ?: "null",
        ) { coeffAlgebra -> SimpleSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = FilteredSelector::class.simpleName ?: "null",
        ) { coeffAlgebra -> FilteredSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = EarlyReturnSelector::class.simpleName ?: "null",
        ) { coeffAlgebra -> EarlyReturnSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = MonoidRingFinder::class.simpleName ?: "null",
        ) { coeffAlgebra -> MonoidRingFinder(coeffAlgebra) }
    )
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = (MonoidRingFinder::class.simpleName ?: "null") + "_useTotalBasis",
        ) { coeffAlgebra -> MonoidRingFinder(coeffAlgebra, useTotalBasis = true) }
    )
    include(
        smallGeneratorFinderForMonoidRingTest(
            matrixSpace = matrixSpace,
            finderName = (MonoidRingFinder::class.simpleName ?: "null") + "_notUseTotalBasis",
        ) { coeffAlgebra -> MonoidRingFinder(coeffAlgebra, useTotalBasis = false) }
    )
})
