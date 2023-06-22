package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

private class CohomologicalNextMMCalculator<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    minimalModel: CohomologicalMinimalModel<B, S, V, M>,
) : NextMMCalculator<B, S, V, M, CohomologicalMinimalModel<B, S, V, M>>(minimalModel) {
    override fun getNextIsomorphismUpTo(): Int {
        return this.minimalModel.isomorphismUpTo + 1
    }

    override fun getDegreeToAddIndeterminate(): Int {
        return this.minimalModel.isomorphismUpTo + 1
    }

    override fun createNextMinimalModel(
        targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
        freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
        dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M>,
        isomorphismUpTo: Int
    ): CohomologicalMinimalModel<B, S, V, M> {
        return CohomologicalMinimalModel(targetDGAlgebra, freeDGAlgebra, dgAlgebraMap, isomorphismUpTo)
    }
}

public data class CohomologicalMinimalModel<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
    override val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
    override val dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M>,
    override val isomorphismUpTo: Int,
) : MinimalModel<B, S, V, M> {
    override fun computeNext(): CohomologicalMinimalModel<B, S, V, M> {
        val calculator = CohomologicalNextMMCalculator(this)
        return calculator.nextMinimalModel
    }

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> of(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
            isomorphismUpTo: Int,
        ): CohomologicalMinimalModel<B, S, V, M> {
            require(targetDGAlgebra.boundedness.lowerBound == 0) {
                "targetDGAlgebra must be bounded below by 0"
            }
            require(targetDGAlgebra[0].dim == 1) {
                "targetDGAlgebra[0].dim must be 1 (i.e. contains only the unit)"
            }
            require(targetDGAlgebra.cohomology[1].dim == 0) {
                "The 1-st cohomology of targetDGAlgebra must be 0 (i.e. 1-connected)"
            }
            var minimalModel = this.getInitial(targetDGAlgebra)
            while (minimalModel.isomorphismUpTo < isomorphismUpTo) {
                minimalModel = minimalModel.computeNext()
            }
            return minimalModel
        }

        private fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInitial(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
        ): CohomologicalMinimalModel<B, S, V, M> {
            // The following type annotation is necessary to infer I = MMIndeterminateName
            val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M> =
                FreeDGAlgebra.fromMap(targetDGAlgebra.matrixSpace, emptyList()) { _ -> emptyMap() }
            val dgAlgebraMap = freeDGAlgebra.getDGAlgebraMap(targetDGAlgebra, emptyList())
            return CohomologicalMinimalModel(
                targetDGAlgebra = targetDGAlgebra,
                freeDGAlgebra = freeDGAlgebra,
                dgAlgebraMap = dgAlgebraMap,
                isomorphismUpTo = 1,
            )
        }
    }
}
