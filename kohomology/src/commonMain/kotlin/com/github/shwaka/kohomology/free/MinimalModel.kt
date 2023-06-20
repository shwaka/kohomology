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
        val matrixSpace = this.targetDGAlgebra.matrixSpace
        val freeGAlgebra = FreeGAlgebra(matrixSpace, this.nextIndeterminateList)
        val differential = MinimalModel.getDifferential(
            degree = degree,
            previousFreeDGAlgebra = this.freeDGAlgebra,
            currentFreeGAlgebra = freeGAlgebra,
            numberOfCocyclesToHit = this.cocyclesToHit.size,
            cocyclesToKill = this.cocyclesToKill,
        )
        val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M> =
            FreeDGAlgebra(freeGAlgebra, differential)
        val dgAlgebraMap = MinimalModel.getDGAlgebraMap(
            targetDGAlgebra = targetDGAlgebra,
            previousFreeDGAlgebra = this.freeDGAlgebra,
            previousDGAlgebraMap = this.dgAlgebraMap,
            currentFreeDGAlgebra = freeDGAlgebra,
            cocyclesToHit = this.cocyclesToHit,
            cocyclesToKill = this.cocyclesToKill,
        )
        return MinimalModel(
            targetDGAlgebra = targetDGAlgebra,
            freeDGAlgebra = freeDGAlgebra,
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

        private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInclusion(
            previousFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            currentFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
        ): GAlgebraMap<
            IntDegree,
            Monomial<IntDegree, MMIndeterminateName>,
            Monomial<IntDegree, MMIndeterminateName>,
            S,
            V,
            M
            > {
            val valueList = currentFreeGAlgebra.generatorList.slice(
                previousFreeGAlgebra.generatorList.indices
            )
            return previousFreeGAlgebra.getGAlgebraMap(currentFreeGAlgebra, valueList)
        }

        private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getDifferential(
            degree: Int,
            previousFreeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            currentFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            numberOfCocyclesToHit: Int,
            cocyclesToKill: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>>,
        ): Derivation<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V, M> {
            val incl = this.getInclusion(previousFreeDGAlgebra, currentFreeGAlgebra)
            val differentialValueList: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>> =
                previousFreeDGAlgebra.generatorList.map {
                    incl(previousFreeDGAlgebra.differential(it))
                } + List(numberOfCocyclesToHit) {
                    currentFreeGAlgebra.getZero(degree + 1)
                } + cocyclesToKill.map {
                    incl(it)
                }
            return currentFreeGAlgebra.getDerivation(
                valueList = differentialValueList,
                derivationDegree = 1,
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
