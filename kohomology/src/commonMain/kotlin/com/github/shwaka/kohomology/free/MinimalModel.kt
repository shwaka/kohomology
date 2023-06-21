package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public interface MinimalModel<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GenericMinimalModel<MMIndeterminateName, B, S, V, M> {

    public fun computeNext(): MinimalModel<B, S, V, M>

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> of(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
            isomorphismUpTo: Int,
        ): MinimalModel<B, S, V, M> {
            return CohomologicalMinimalModel.of(targetDGAlgebra, isomorphismUpTo)
        }
    }
}
