package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.moduleTag
import com.github.shwaka.kohomology.resol.monoid.CyclicGroup
import com.github.shwaka.kohomology.resol.monoid.CyclicGroupElement
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.shouldBe

private typealias GetSelector<BA, S, V, M> =
    (coeffAlgebra: Algebra<BA, S, V, M>) -> SmallGeneratorSelector<BA, S, V, M, Algebra<BA, S, V, M>>

private fun <
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > smallGeneratorSelectorTest(
    matrixSpace: MatrixSpace<S, V, M>,
    selectorName: String,
    strict: Boolean,
    getSelector: GetSelector<CyclicGroupElement, S, V, M>,
) = freeSpec {
    "test SmallGeneratorSelector implementation: $selectorName" - {
        val underlyingVectorSpace = VectorSpace(matrixSpace.numVectorSpace, listOf("x", "y"))
        val coeffAlgebra = MonoidRing(CyclicGroup(2), matrixSpace)
        val selector = getSelector(coeffAlgebra)
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

        "selector.select(module) should generate module" {
            val smallGenerator = selector.select(module)
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
        }

        "selector.select(module, module.underlyingVectorSpace.getBasis()) should generate module" {
            val smallGenerator = selector.select(module, module.underlyingVectorSpace.getBasis())
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
        }

        "selector.select(module, listOf(x+y, x)) should generate module" {
            val smallGenerator = module.context.run {
                selector.select(module, listOf(x + y, x))
            }
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
        }

        "selector.select(module, listOf(x-y), listOf(y)) should generate module and start with listOf(y)" {
            val smallGenerator = module.context.run {
                selector.select(module, listOf(x - y), listOf(y))
            }
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
            smallGenerator shouldStartWith listOf(y)
        }

        "selector.select(module, listOf(x-y), listOf(y, x)) should generate module and start with listOf(y, x)" {
            val smallGenerator = module.context.run {
                selector.select(module, listOf(x - y), listOf(y, x))
            }
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(smallGenerator)
            subVectorSpace.isProperSubspace().shouldBeFalse()
            smallGenerator shouldStartWith listOf(y, x)
        }

        "check indices in selector.selectWithIndex(module, listOf(x, y))" {
            val candidates = listOf(x, y)
            val indexedCandidates = candidates.withIndex().toList()
            val (alreadySelectedReturned, newlySelected) = selector.selectWithIndex(module, candidates)
            alreadySelectedReturned.shouldBeEmpty()
            newlySelected.forAll { indexedVector ->
                indexedVector shouldBeIn indexedCandidates
            }
        }

        "check indices in selector.selectWithIndex(module, listOf(x, y), listOf(x+y))" {
            val candidates = listOf(x, y)
            val indexedCandidates = candidates.withIndex().toList()
            val alreadySelected = module.context.run {
                listOf(x + y)
            }
            val (alreadySelectedReturned, newlySelected) =
                selector.selectWithIndex(module, candidates, alreadySelected)
            alreadySelectedReturned shouldBe alreadySelected
            newlySelected.forAll { indexedVector ->
                indexedVector shouldBeIn indexedCandidates
            }
        }

        if (strict) {
            "selector.select(module, module.underlyingVectorSpace.getBasis()) should return listOf(x)" {
                val smallGenerator = selector.select(module, module.underlyingVectorSpace.getBasis())
                smallGenerator shouldHaveSize 1
                smallGenerator shouldBe listOf(x)
            }

            "selector.select(module, listOf(x)) should return listOf(x)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(x))
                    smallGenerator shouldHaveSize 1
                    smallGenerator shouldBe listOf(x)
                }
            }

            "selector.select(module, listOf(x+y, x)) should return listOf(x)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(x + y, x))
                    smallGenerator shouldHaveSize 1
                    smallGenerator shouldBe listOf(x)
                }
            }

            "selector.select(module, listOf(x, x+y)) should return listOf(x)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(x, x + y))
                    smallGenerator shouldHaveSize 1
                    smallGenerator shouldBe listOf(x)
                }
            }

            "selector.select(module, listOf(2x+y)) should return listOf(2x+y)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(2 * x + y))
                    smallGenerator shouldHaveSize 1
                    smallGenerator shouldBe listOf(2 * x + y)
                }
            }

            "selector.select(module, listOf(x+y, x-y)) should return listOf(x+y, x-y)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(x + y, x - y))
                    smallGenerator shouldHaveSize 2
                    smallGenerator shouldBe listOf(x + y, x - y)
                }
            }

            "selector.select(module, emptyList()) should throw IllegalArgumentException" {
                module.context.run {
                    val smallGenerator = selector.select(module, emptyList())
                    smallGenerator.shouldBeEmpty()
                }
            }

            "selector.select(module, listOf(x+y)) should throw IllegalArgumentException" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(x + y))
                    smallGenerator shouldHaveSize 1
                    smallGenerator shouldBe listOf(x + y)
                }
            }

            "selector.select(module, listOf(y, x), listOf(x+y) should return listOf(x+y, y)" {
                module.context.run {
                    val smallGenerator = selector.select(module, listOf(y, x), listOf(x + y))
                    smallGenerator shouldHaveSize 2
                    smallGenerator shouldBe listOf(x + y, y)
                }
            }
        }
    }
}

class SmallGeneratorSelectorTest : FreeSpec({
    tags(moduleTag)

    val matrixSpace = SparseMatrixSpaceOverRational
    include(
        smallGeneratorSelectorTest(
            matrixSpace = matrixSpace,
            selectorName = SimpleSelector::class.simpleName ?: "null",
            strict = true,
        ) { coeffAlgebra -> SimpleSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorSelectorTest(
            matrixSpace = matrixSpace,
            selectorName = FilteredSelector::class.simpleName ?: "null",
            strict = true,
        ) { coeffAlgebra -> FilteredSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorSelectorTest(
            matrixSpace = matrixSpace,
            selectorName = EarlyReturnSelector::class.simpleName ?: "null",
            strict = true,
        ) { coeffAlgebra -> EarlyReturnSelector(coeffAlgebra) }
    )
    include(
        smallGeneratorSelectorTest(
            matrixSpace = matrixSpace,
            selectorName = TrivialSelector::class.simpleName ?: "null",
            strict = false,
        ) { TrivialSelector() }
    )
    include(
        smallGeneratorSelectorTest(
            matrixSpace = matrixSpace,
            selectorName = BasisSelector::class.simpleName ?: "null",
            strict = false,
        ) { BasisSelector() }
    )
})
