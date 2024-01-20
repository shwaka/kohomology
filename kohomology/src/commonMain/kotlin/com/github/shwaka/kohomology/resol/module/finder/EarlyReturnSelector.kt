package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public class EarlyReturnSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    >(
    private val coeffAlgebra: Alg,
) : SmallGeneratorSelector<BA, S, V, M, Alg> {
    // This method has arguments previousDim and previousMax
    // and hence its signature is slightly different from EfficientVectorSelector.selectMostEfficientVector
    private fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> selectMostEfficientVector(
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
        require(module.coeffAlgebra == this.coeffAlgebra) {
            "Coefficient algebra is expected to be ${this.coeffAlgebra}, " +
                "but ${module.coeffAlgebra} was given"
        }
        var remainingGenerator: List<Vector<B, S, V>> = generator
        val result = mutableListOf<Vector<B, S, V>>()
        var previousDim = 0
        var previousMax = module.coeffAlgebra.dim
        while (remainingGenerator.isNotEmpty()) {
            val (selectedIndex, generatedSubVectorSpace) = this.selectMostEfficientVector(
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
