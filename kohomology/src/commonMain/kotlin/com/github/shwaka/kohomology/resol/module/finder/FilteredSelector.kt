package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public class FilteredSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    >(
    coeffAlgebra: Alg,
) : EfficientVectorSelector<BA, S, V, M, Alg>(coeffAlgebra) {
    override fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> selectMostEfficientVector(
        module: Module<BA, B, S, V, M>,
        candidates: List<Vector<B, S, V>>,
        alreadySelected: List<Vector<B, S, V>>,
    ): Pair<Int, SubVectorSpace<B, S, V, M>> {
        var remainingCandidates: List<IndexedValue<Vector<B, S, V>>> = candidates.withIndex().toList()
        val finishedCandidates = mutableListOf<Pair<Int, SubVectorSpace<B, S, V, M>>>()
        while (remainingCandidates.isNotEmpty()) {
            val (index, candidate) = remainingCandidates.first()
            val subVectorSpace = module.generateSubVectorSpaceOverCoefficient(alreadySelected + listOf(candidate))
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
