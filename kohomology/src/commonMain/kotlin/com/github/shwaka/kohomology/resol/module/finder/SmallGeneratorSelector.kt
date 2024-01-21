package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.Vector

public interface SmallGeneratorSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    in Alg : Algebra<BA, S, V, M>,
    > : SmallGeneratorFinder<BA, S, V, M, Alg> {

    public fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> select(
        module: Module<BA, B, S, V, M>,
        candidates: List<Vector<B, S, V>> = module.underlyingVectorSpace.getBasis(),
        alreadySelected: List<Vector<B, S, V>> = emptyList(),
    ): List<Vector<B, S, V>>

    public fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> selectWithIndex(
        module: Module<BA, B, S, V, M>,
        candidates: List<Vector<B, S, V>> = module.underlyingVectorSpace.getBasis(),
        alreadySelected: List<Vector<B, S, V>> = emptyList(),
    ): Pair<List<Vector<B, S, V>>, List<IndexedValue<Vector<B, S, V>>>> {
        val selected = this.select(module, candidates = candidates, alreadySelected = alreadySelected)
        val indexMap: Map<Vector<B, S, V>, Int> =
            candidates.mapIndexed { index, vector -> vector to index }.toMap()
        val newlySelected = selected.drop(alreadySelected.size).map { vector ->
            val index = indexMap[vector] ?: throw Exception("This can't happen!")
            IndexedValue(index = index, value = vector)
        }
        return Pair(alreadySelected, newlySelected)
    }

    override fun <B : BasisName> find(
        module: Module<BA, B, S, V, M>,
    ): List<Vector<B, S, V>> {
        return this.select(module, module.underlyingVectorSpace.getBasis())
    }
}
