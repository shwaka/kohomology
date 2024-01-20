package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public interface SmallGeneratorFinder {
    public fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> find(
        module: Module<BA, B, S, V, M>,
    ): List<Vector<B, S, V>>

    public companion object {
        public val default: SmallGeneratorFinder = SmallGeneratorSelector.EarlyReturnFinder
    }
}

public interface SmallGeneratorSelector : SmallGeneratorFinder {
    public fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> select(
        module: Module<BA, B, S, V, M>,
        generator: List<Vector<B, S, V>>,
    ): List<Vector<B, S, V>>

    override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> find(
        module: Module<BA, B, S, V, M>,
    ): List<Vector<B, S, V>> {
        return this.select(module, module.underlyingVectorSpace.getBasis())
    }

    public abstract class FinderBase : SmallGeneratorSelector {
        protected abstract fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findMostEfficientVector(
            module: Module<BA, B, S, V, M>,
            alreadyAdded: List<Vector<B, S, V>>,
            candidates: List<Vector<B, S, V>>,
        ): Pair<Int, SubVectorSpace<B, S, V, M>>

        override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> select(
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

    public object SimpleFinder : FinderBase() {
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

    public object FilteredFinder : FinderBase() {
        override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findMostEfficientVector(
            module: Module<BA, B, S, V, M>,
            alreadyAdded: List<Vector<B, S, V>>,
            candidates: List<Vector<B, S, V>>
        ): Pair<Int, SubVectorSpace<B, S, V, M>> {
            var remainingCandidates: List<IndexedValue<Vector<B, S, V>>> = candidates.withIndex().toList()
            val finishedCandidates = mutableListOf<Pair<Int, SubVectorSpace<B, S, V, M>>>()
            while (remainingCandidates.isNotEmpty()) {
                val (index, candidate) = remainingCandidates.first()
                val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(alreadyAdded + listOf(candidate))
                finishedCandidates.add(
                    Pair(index, subVectorSpace)
                )
                remainingCandidates = remainingCandidates.drop(1).filter {
                    !subVectorSpace.subspaceContains(it.value)
                }
            }
            return finishedCandidates.maxBy { (_, subVectorSpace) -> subVectorSpace.dim }
        }
    }

    public object EarlyReturnFinder : SmallGeneratorSelector {
        // slightly different interface of findMostEfficientVector (previousDim)
        private fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> findMostEfficientVector(
            module: Module<BA, B, S, V, M>,
            alreadyAdded: List<Vector<B, S, V>>,
            previousDim: Int,
            previousMax: Int,
            candidates: List<Vector<B, S, V>>,
        ): Pair<Int, SubVectorSpace<B, S, V, M>> {
            var remainingCandidates: List<IndexedValue<Vector<B, S, V>>> = candidates.withIndex().toList()
            val finishedCandidates = mutableListOf<Pair<Int, SubVectorSpace<B, S, V, M>>>()
            while (remainingCandidates.isNotEmpty()) {
                val (index, candidate) = remainingCandidates.first()
                val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(alreadyAdded + listOf(candidate))
                val pair = Pair(index, subVectorSpace)
                if (subVectorSpace.dim == previousDim + previousMax) {
                    return pair
                }
                finishedCandidates.add(pair)
                remainingCandidates = remainingCandidates.drop(1).filter {
                    !subVectorSpace.subspaceContains(it.value)
                }
            }
            return finishedCandidates.maxBy { (_, subVectorSpace) -> subVectorSpace.dim }
        }

        override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> select(
            module: Module<BA, B, S, V, M>,
            generator: List<Vector<B, S, V>>
        ): List<Vector<B, S, V>> {
            var remainingGenerator: List<Vector<B, S, V>> = generator
            val result = mutableListOf<Vector<B, S, V>>()
            var previousDim = 0
            var previousMax = module.coeffAlgebra.dim
            while (remainingGenerator.isNotEmpty()) {
                val (selectedIndex, generatedSubVectorSpace) = this.findMostEfficientVector(
                    module,
                    alreadyAdded = result,
                    previousDim = previousDim,
                    previousMax = previousMax,
                    candidates = remainingGenerator,
                )
                previousMax = generatedSubVectorSpace.dim - previousDim
                previousDim = generatedSubVectorSpace.dim
                result.add(remainingGenerator[selectedIndex])
                remainingGenerator = remainingGenerator.filterIndexed { index, vector ->
                    (index != selectedIndex) &&
                        !generatedSubVectorSpace.subspaceContains(vector)
                }
            }
            return result
        }
    }
}
