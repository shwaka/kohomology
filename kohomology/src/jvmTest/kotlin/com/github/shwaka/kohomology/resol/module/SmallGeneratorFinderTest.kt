package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

private fun smallGeneratorFinderTest(
    finder: SmallGeneratorFinder,
) = freeSpec {
    "test SmallGeneratorFinder implementation: ${finder::class.simpleName}" - {
        val matrixSpace = SparseMatrixSpaceOverRational
        val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
        val (x, y) = underlyingVectorSpace.getBasis()
        // val (one, t) = coeffAlgebra.getBasis()
        val module = run {
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
            Module(matrixSpace, underlyingVectorSpace, coeffAlgebra, action)
        }

        "finder.find(module, module.underlyingVectorSpace.getBasis()) should return listOf(x)" {
            val smallGenerator = finder.find(module, module.underlyingVectorSpace.getBasis())
            smallGenerator.shouldHaveSize(1)
            smallGenerator shouldBe listOf(x)
        }

        "finder.find(module, listOf(x)) should return listOf(x)" {
            module.context.run {
                val smallGenerator = finder.find(module, listOf(x))
                smallGenerator.shouldHaveSize(1)
                smallGenerator shouldBe listOf(x)
            }
        }

        "finder.find(module, listOf(x+y, x)) should return listOf(x)" {
            module.context.run {
                val smallGenerator = finder.find(module, listOf(x + y, x))
                smallGenerator.shouldHaveSize(1)
                smallGenerator shouldBe listOf(x)
            }
        }

        "finder.find(module, listOf(x, x+y)) should return listOf(x)" {
            module.context.run {
                val smallGenerator = finder.find(module, listOf(x, x + y))
                smallGenerator.shouldHaveSize(1)
                smallGenerator shouldBe listOf(x)
            }
        }

        "finder.find(module, listOf(2x+y)) should return listOf(2x+y)" {
            module.context.run {
                val smallGenerator = finder.find(module, listOf(2 * x + y))
                smallGenerator.shouldHaveSize(1)
                smallGenerator shouldBe listOf(2 * x + y)
            }
        }

        "finder.find(module, listOf(x+y, x-y)) should return listOf(x+y, x-y)" {
            module.context.run {
                val smallGenerator = finder.find(module, listOf(x + y, x - y))
                smallGenerator.shouldHaveSize(2)
                smallGenerator shouldBe listOf(x + y, x - y)
            }
        }
    }
}

class SmallGeneratorFinderTest : FreeSpec({
    tags(moduleTag)

    include(smallGeneratorFinderTest(SmallGeneratorFinder.SimpleFinder))
})
