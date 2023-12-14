package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

internal sealed interface SmallGeneratorFinder {
    fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> find(
        module: Module<BA, B, S, V, M>,
        generator: List<Vector<B, S, V>>,
    ): List<Vector<B, S, V>>

    abstract class FinderBase : SmallGeneratorFinder {
        protected abstract fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findMostEfficientVector(
            module: Module<BA, B, S, V, M>,
            alreadyAdded: List<Vector<B, S, V>>,
            candidates: List<Vector<B, S, V>>,
        ): Pair<Int, SubVectorSpace<B, S, V, M>>

        override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> find(
            module: Module<BA, B, S, V, M>,
            generator: List<Vector<B, S, V>>
        ): List<Vector<B, S, V>> {
            var remainingGenerator: List<Vector<B, S, V>> = generator
            val result = mutableListOf<Vector<B, S, V>>()
            while (remainingGenerator.isNotEmpty()) {
                val (selectedIndex, generatedSubVectorSpace) = this.findMostEfficientVector(module, result, remainingGenerator)
                result.add(remainingGenerator[selectedIndex])
                remainingGenerator = remainingGenerator.filterIndexed { index, vector ->
                    (index != selectedIndex) &&
                        !generatedSubVectorSpace.subspaceContains(vector)
                }
            }
            return result
        }
    }

    object SimpleFinder : FinderBase() {
        override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findMostEfficientVector(
            module: Module<BA, B, S, V, M>,
            alreadyAdded: List<Vector<B, S, V>>,
            candidates: List<Vector<B, S, V>>,
        ): Pair<Int, SubVectorSpace<B, S, V, M>> {
            return candidates.withIndex().map { (index, candidate) ->
                Pair(
                    index,
                    module.generateSubVectorSpaceOverCoefficient(alreadyAdded + listOf(candidate))
                )
            }.maxBy { (_, subVectorSpace) -> subVectorSpace.dim }
        }
    }
}
