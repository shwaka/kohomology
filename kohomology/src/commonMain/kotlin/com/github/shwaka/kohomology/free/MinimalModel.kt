package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public data class MinimalModel<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
    val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
    val dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M>,
    val isomorphismUpTo: Int,
) {
    public fun computeNext(): MinimalModel<B, S, V, M> {
        val degree = this.isomorphismUpTo + 1
        val targetDGAlgebra = this.targetDGAlgebra
        val dgAlgebraMap = MinimalModel.getDGAlgebraMap(
            targetDGAlgebra = targetDGAlgebra,
            previousFreeDGAlgebra = this.freeDGAlgebra,
            previousDGAlgebraMap = this.dgAlgebraMap,
            currentFreeDGAlgebra = this.nextFreeDGAlgebra,
            cocyclesToHit = this.cocyclesToHit,
            cocyclesToKill = this.cocyclesToKill,
        )
        return MinimalModel(
            targetDGAlgebra = targetDGAlgebra,
            freeDGAlgebra = this.nextFreeDGAlgebra,
            dgAlgebraMap = dgAlgebraMap,
            isomorphismUpTo = degree,
        )
    }

    private val cocyclesToHit: List<GVector<IntDegree, B, S, V>> by lazy {
        val degree = this.isomorphismUpTo + 1
        val targetDGAlgebra = this.targetDGAlgebra
        val inducedMapOnCohomology = this.dgAlgebraMap.inducedMapOnCohomology
        val cokernel = inducedMapOnCohomology.cokernel()
        val section = cokernel.section
        cokernel.getBasis(degree)
            .map { cokernelCohomologyClass -> section(cokernelCohomologyClass) }
            .map { cohomologyClass ->
                targetDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
            }
    }

    private val cocyclesToKill: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>> by lazy {
        val degree = this.isomorphismUpTo + 1
        val inducedMapOnCohomology = this.dgAlgebraMap.inducedMapOnCohomology
        val kernel = inducedMapOnCohomology.kernel()
        val incl = kernel.inclusion
        kernel.getBasis(degree + 1)
            .map { kernelCohomologyClass -> incl(kernelCohomologyClass) }
            .map { cohomologyClass ->
                this.freeDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
            }
    }

    private val nextIndeterminateList: List<Indeterminate<IntDegree, MMIndeterminateName>> by lazy {
        val degree = this.isomorphismUpTo + 1
        this.freeDGAlgebra.indeterminateList +
            (0 until this.cocyclesToHit.size).map { index ->
                val name = MMIndeterminateName(
                    degree = degree,
                    index = index,
                    totalNumberInDegree = this.cocyclesToHit.size,
                    type = MMIndeterminateType.COCYCLE,
                )
                Indeterminate(name, degree)
            } +
            (0 until this.cocyclesToKill.size).map { index ->
                val name = MMIndeterminateName(
                    degree = degree,
                    index = index,
                    totalNumberInDegree = this.cocyclesToKill.size,
                    type = MMIndeterminateType.COCHAIN,
                )
                Indeterminate(name, degree)
            }
    }

    private val nextFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M> by lazy {
        FreeGAlgebra(this.freeDGAlgebra.matrixSpace, this.nextIndeterminateList)
    }

    private val inclusionToNext: GAlgebraMap<
        IntDegree,
        Monomial<IntDegree, MMIndeterminateName>,
        Monomial<IntDegree, MMIndeterminateName>,
        S, V, M> by lazy {
        val valueList = this.nextFreeGAlgebra.generatorList.slice(
            this.freeDGAlgebra.generatorList.indices
        )
        this.freeDGAlgebra.getGAlgebraMap(this.nextFreeGAlgebra, valueList)
    }

    private val nextDifferential: Derivation<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V, M> by lazy {
        val incl = this.inclusionToNext
        val differentialValueList: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>> =
            this.freeDGAlgebra.generatorList.map {
                incl(this.freeDGAlgebra.differential(it))
            } + List(this.cocyclesToHit.size) {
                this.nextFreeGAlgebra.getZero(this.isomorphismUpTo + 2)
            } + this.cocyclesToKill.map {
                incl(it)
            }
        this.nextFreeGAlgebra.getDerivation(
            valueList = differentialValueList,
            derivationDegree = 1,
        )
    }

    private val nextFreeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M> by lazy {
        FreeDGAlgebra(this.nextFreeGAlgebra, this.nextDifferential)
    }

    public companion object {
        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> of(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
            isomorphismUpTo: Int,
        ): MinimalModel<B, S, V, M> {
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
        ): MinimalModel<B, S, V, M> {
            // The following type annotation is necessary to infer I = MMIndeterminateName
            val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M> =
                FreeDGAlgebra.fromMap(targetDGAlgebra.matrixSpace, emptyList()) { _ -> emptyMap() }
            val dgAlgebraMap = freeDGAlgebra.getDGAlgebraMap(targetDGAlgebra, emptyList())
            return MinimalModel(
                targetDGAlgebra = targetDGAlgebra,
                freeDGAlgebra = freeDGAlgebra,
                dgAlgebraMap = dgAlgebraMap,
                isomorphismUpTo = 1,
            )
        }

        private fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDGAlgebraMap(
            targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
            previousFreeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            previousDGAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M>,
            currentFreeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            cocyclesToHit: List<GVector<IntDegree, B, S, V>>,
            cocyclesToKill: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>>,
        ): DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M> {
            val dgAlgebraMapValueList = previousFreeDGAlgebra.generatorList.map {
                previousDGAlgebraMap(it)
            } + cocyclesToHit + cocyclesToKill.map { cocycleToKill ->
                val targetCoboundary = previousDGAlgebraMap(cocycleToKill)
                val boundingTargetCochain = targetDGAlgebra.differential.findPreimage(targetCoboundary)
                    ?: throw IllegalStateException("This can't happen! Failed to find preimage of d.")
                boundingTargetCochain
            }
            return currentFreeDGAlgebra.getDGAlgebraMap(targetDGAlgebra, dgAlgebraMapValueList)
        }
    }
}
