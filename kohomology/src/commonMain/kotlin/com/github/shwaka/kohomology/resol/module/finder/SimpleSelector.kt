package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public class SimpleSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    >(
    coeffAlgebra: Alg,
) : SelectorBase<BA, S, V, M, Alg>(coeffAlgebra) {
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
