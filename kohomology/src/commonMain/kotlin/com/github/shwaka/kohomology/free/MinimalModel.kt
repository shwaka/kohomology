package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Identifier
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.vectsp.BasisName

public enum class MMIndeterminateType {
    COCYCLE, COCHAIN,
}

public data class MMIndeterminateName(
    val degree: Int,
    val index: Int,
    val totalNumberInDegree: Int,
    val type: MMIndeterminateType,
) : IndeterminateName {
    init {
        require(index >= 0) {
            "index must be non-negative, but $index was given"
        }
        require(index < totalNumberInDegree) {
            "index must be less than totalNumberInDegree, " +
                "but the given values were $index and $totalNumberInDegree"
        }
    }

    override val identifier: Identifier
        get() {
            val printConfig = PrintConfig(printType = PrintType.PLAIN)
            return Identifier(this.toString(printConfig))
        }

    override fun toString(printConfig: PrintConfig): String {
        val char = when (this.type) {
            MMIndeterminateType.COCYCLE -> "v"
            MMIndeterminateType.COCHAIN -> "w"
        }
        return when (this.totalNumberInDegree) {
            1 -> "${char}_${this.degree}"
            else -> when (printConfig.printType) {
                PrintType.PLAIN -> "${char}_${this.degree}_${this.index}"
                PrintType.TEX -> "${char}_{${this.degree},${this.index}}"
            }
        }
    }
}

public data class MinimalModel<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
    val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
    val dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, MMIndeterminateName>, B, S, V, M>,
    val isomorphismUpTo: Int,
) {
    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
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
                minimalModel = this.computeNext(minimalModel)
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

        private fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> computeNext(
            minimalModel: MinimalModel<B, S, V, M>
        ): MinimalModel<B, S, V, M> {
            val degree = minimalModel.isomorphismUpTo + 1
            val targetDGAlgebra = minimalModel.targetDGAlgebra
            val cocyclesToHit = this.getCocyclesToHit(minimalModel)
            val cocyclesToKill = this.getCocyclesToKill(minimalModel)
            val indeterminateList: List<Indeterminate<IntDegree, MMIndeterminateName>> =
                this.getIndeterminateList(
                    degree = degree,
                    previousIndeterminateList = minimalModel.freeDGAlgebra.indeterminateList,
                    numberOfCocyclesToHit = cocyclesToHit.size,
                    numberOfCocyclesToKill = cocyclesToKill.size
                )
            val matrixSpace = minimalModel.targetDGAlgebra.matrixSpace
            val freeGAlgebra = FreeGAlgebra(matrixSpace, indeterminateList)
            val incl = this.getInclusion(minimalModel.freeDGAlgebra, freeGAlgebra)
            val differentialValueList: List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>> =
                minimalModel.freeDGAlgebra.generatorList.map {
                    incl(minimalModel.freeDGAlgebra.differential(it))
                } + List(cocyclesToHit.size) {
                    freeGAlgebra.getZero(degree + 1)
                } + cocyclesToKill.map {
                    incl(it)
                }
            val differential = freeGAlgebra.getDerivation(
                valueList = differentialValueList,
                derivationDegree = 1,
            )
            val freeDGAlgebra: FreeDGAlgebra<IntDegree, MMIndeterminateName, S, V, M> =
                FreeDGAlgebra(freeGAlgebra, differential)
            val dgAlgebraMapValueList = minimalModel.freeDGAlgebra.generatorList.map {
                minimalModel.dgAlgebraMap(it)
            } + cocyclesToHit + cocyclesToKill.map { cocycleToKill ->
                val targetCoboundary = minimalModel.dgAlgebraMap(cocycleToKill)
                val boundingTargetCochain = minimalModel.targetDGAlgebra.differential.findPreimage(targetCoboundary)
                    ?: throw IllegalStateException("This can't happen! Failed to find preimage of d.")
                boundingTargetCochain
            }
            val dgAlgebraMap = freeDGAlgebra.getDGAlgebraMap(targetDGAlgebra, dgAlgebraMapValueList)
            return MinimalModel(
                targetDGAlgebra = targetDGAlgebra,
                freeDGAlgebra = freeDGAlgebra,
                dgAlgebraMap = dgAlgebraMap,
                isomorphismUpTo = degree,
            )
        }

        private fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getCocyclesToHit(
            minimalModel: MinimalModel<B, S, V, M>,
        ): List<GVector<IntDegree, B, S, V>> {
            val degree = minimalModel.isomorphismUpTo + 1
            val targetDGAlgebra = minimalModel.targetDGAlgebra
            val inducedMapOnCohomology = minimalModel.dgAlgebraMap.inducedMapOnCohomology
            val cokernel = inducedMapOnCohomology.cokernel()
            val section = cokernel.section
            return cokernel.getBasis(degree)
                .map { cokernelCohomologyClass -> section(cokernelCohomologyClass) }
                .map { cohomologyClass ->
                    targetDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
                }
        }

        private fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getCocyclesToKill(
            minimalModel: MinimalModel<B, S, V, M>,
        ): List<GVector<IntDegree, Monomial<IntDegree, MMIndeterminateName>, S, V>> {
            val degree = minimalModel.isomorphismUpTo + 1
            val inducedMapOnCohomology = minimalModel.dgAlgebraMap.inducedMapOnCohomology
            val kernel = inducedMapOnCohomology.kernel()
            val incl = kernel.inclusion
            return kernel.getBasis(degree + 1)
                .map { kernelCohomologyClass -> incl(kernelCohomologyClass) }
                .map { cohomologyClass ->
                    minimalModel.freeDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
                }
        }

        private fun getIndeterminateList(
            degree: Int,
            previousIndeterminateList: List<Indeterminate<IntDegree, MMIndeterminateName>>,
            numberOfCocyclesToHit: Int,
            numberOfCocyclesToKill: Int,
        ): List<Indeterminate<IntDegree, MMIndeterminateName>> {
            return previousIndeterminateList +
                (0 until numberOfCocyclesToHit).map { index ->
                    val name = MMIndeterminateName(
                        degree = degree,
                        index = index,
                        totalNumberInDegree = numberOfCocyclesToHit,
                        type = MMIndeterminateType.COCYCLE,
                    )
                    Indeterminate(name, degree)
                } +
                (0 until numberOfCocyclesToKill).map { index ->
                    val name = MMIndeterminateName(
                        degree = degree,
                        index = index,
                        totalNumberInDegree = numberOfCocyclesToKill,
                        type = MMIndeterminateType.COCHAIN,
                    )
                    Indeterminate(name, degree)
                }
        }

        private fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInclusion(
            previousFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
            currentFreeGAlgebra: FreeGAlgebra<IntDegree, MMIndeterminateName, S, V, M>,
        ): GAlgebraMap<
            IntDegree,
            Monomial<IntDegree, MMIndeterminateName>,
            Monomial<IntDegree, MMIndeterminateName>,
            S, V, M> {
            val valueList = currentFreeGAlgebra.generatorList.slice(
                previousFreeGAlgebra.generatorList.indices
            )
            return previousFreeGAlgebra.getGAlgebraMap(currentFreeGAlgebra, valueList)
        }
    }
}
