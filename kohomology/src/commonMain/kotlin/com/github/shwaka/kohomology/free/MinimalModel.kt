package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public data class MinimalModelProgress(
    val currentIsomorphismUpTo: Int,
    val targetIsomorphismUpTo: Int,
    val currentNumberOfGenerators: Int,
)

public interface MinimalModel<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GenericMinimalModel<MMIndeterminateName, B, S, V, M> {

    public fun computeNext(): MinimalModel<B, S, V, M>

    public fun toProgress(targetIsomorphismUpTo: Int): MinimalModelProgress {
        return MinimalModelProgress(
            currentIsomorphismUpTo = this.isomorphismUpTo,
            targetIsomorphismUpTo = targetIsomorphismUpTo,
            currentNumberOfGenerators = this.freeDGAlgebra.generatorList.size,
        )
    }

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> of(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
            isomorphismUpTo: Int,
            reportProgress: (MinimalModelProgress) -> Unit = {},
        ): MinimalModel<B, S, V, M> {
            if (
                (targetDGAlgebra.boundedness.lowerBound != null) &&
                (targetDGAlgebra.boundedness.lowerBound == 0)
            ) {
                return CohomologicalMinimalModel.of(targetDGAlgebra, isomorphismUpTo, reportProgress)
            }
            if (
                (targetDGAlgebra.boundedness.upperBound != null) &&
                (targetDGAlgebra.boundedness.upperBound == 0)
            ) {
                return HomologicalMinimalModel.of(targetDGAlgebra, isomorphismUpTo, reportProgress)
            }
            throw IllegalArgumentException(
                "Cannot compute minimal model of $targetDGAlgebra " +
                    "since it is not concentrated in non-positive or non-negative degrees"
            )
        }
    }
}
