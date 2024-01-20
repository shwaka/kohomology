package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

public abstract class EfficientVectorSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    >(
    private val coeffAlgebra: Alg,
) : SmallGeneratorSelector<BA, S, V, M, Alg> {

    protected abstract fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> selectMostEfficientVector(
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
            val (selectedIndex, generatedSubVectorSpace) = this.selectMostEfficientVector(module, result, remainingGenerator)
            result.add(remainingGenerator[selectedIndex])
            remainingGenerator = remainingGenerator.filterIndexed { index, vector ->
                (index != selectedIndex) &&
                    !generatedSubVectorSpace.subspaceContains(vector)
            }
        }
        return result
    }
}
