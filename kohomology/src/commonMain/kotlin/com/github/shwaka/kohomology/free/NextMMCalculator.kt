package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public interface GenericMinimalModel<I : IndeterminateName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>
    public val freeDGAlgebra: FreeDGAlgebra<IntDegree, I, S, V, M>
    public val dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, I>, B, S, V, M>
    public val isomorphismUpTo: Int
}

// This is added to distinguish I and INext in the type level.
internal abstract class AbstractNextMMCalculator<
    I : IndeterminateName,
    INext : IndeterminateName,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    MMNext : GenericMinimalModel<INext, B, S, V, M>>(
    protected val minimalModel: GenericMinimalModel<I, B, S, V, M>,
) {
    abstract fun getIndeterminateName(
        degree: Int,
        index: Int,
        totalNumberInDegree: Int,
        type: MMIndeterminateType
    ): INext

    abstract fun getNextIsomorphismUpTo(): Int
    abstract fun getDegreeToAddIndeterminate(): Int

    abstract fun convertIndeterminate(
        indeterminate: Indeterminate<IntDegree, I>
    ): Indeterminate<IntDegree, INext>

    abstract fun createNextMinimalModel(
        targetDGAlgebra: DGAlgebra<IntDegree, B, S, V, M>,
        freeDGAlgebra: FreeDGAlgebra<IntDegree, INext, S, V, M>,
        dgAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, INext>, B, S, V, M>,
        isomorphismUpTo: Int,
    ): MMNext

    val nextMinimalModel: MMNext by lazy {
        this.createNextMinimalModel(
            this.minimalModel.targetDGAlgebra,
            this.nextFreeDGAlgebra,
            this.nextDGAlgebraMap,
            this.getNextIsomorphismUpTo(),
        )
    }

    private val cocyclesToHit: List<GVector<IntDegree, B, S, V>> by lazy {
        val degree = this.getDegreeToAddIndeterminate()
        val targetDGAlgebra = this.minimalModel.targetDGAlgebra
        val inducedMapOnCohomology = this.minimalModel.dgAlgebraMap.inducedMapOnCohomology
        val cokernel = inducedMapOnCohomology.cokernel()
        val section = cokernel.section
        cokernel.getBasis(degree)
            .map { cokernelCohomologyClass -> section(cokernelCohomologyClass) }
            .map { cohomologyClass ->
                targetDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
            }
    }

    private val cocyclesToKill: List<GVector<IntDegree, Monomial<IntDegree, I>, S, V>> by lazy {
        val degree = this.getDegreeToAddIndeterminate()
        val inducedMapOnCohomology = this.minimalModel.dgAlgebraMap.inducedMapOnCohomology
        val kernel = inducedMapOnCohomology.kernel()
        val incl = kernel.inclusion
        kernel.getBasis(degree + 1)
            .map { kernelCohomologyClass -> incl(kernelCohomologyClass) }
            .map { cohomologyClass ->
                this.minimalModel.freeDGAlgebra.cocycleRepresentativeOf(cohomologyClass)
            }
    }

    private val nextIndeterminateList: List<Indeterminate<IntDegree, INext>> by lazy {
        val degree = this.getDegreeToAddIndeterminate()
        this.minimalModel.freeDGAlgebra.indeterminateList.map(this::convertIndeterminate) +
            (0 until this.cocyclesToHit.size).map { index ->
                val name = this.getIndeterminateName(
                    degree,
                    index,
                    this.cocyclesToHit.size,
                    MMIndeterminateType.COCYCLE,
                )
                Indeterminate(name, degree)
            } +
            (0 until this.cocyclesToKill.size).map { index ->
                val name = this.getIndeterminateName(
                    degree,
                    index,
                    this.cocyclesToKill.size,
                    MMIndeterminateType.COCHAIN,
                )
                Indeterminate(name, degree)
            }
    }

    private val nextFreeGAlgebra: FreeGAlgebra<IntDegree, INext, S, V, M> by lazy {
        FreeGAlgebra(this.minimalModel.freeDGAlgebra.matrixSpace, this.nextIndeterminateList)
    }

    private val inclusionToNext: GAlgebraMap<
        IntDegree,
        Monomial<IntDegree, I>,
        Monomial<IntDegree, INext>,
        S, V, M> by lazy {
        val valueList = this.nextFreeGAlgebra.generatorList.slice(
            this.minimalModel.freeDGAlgebra.generatorList.indices
        )
        this.minimalModel.freeDGAlgebra.getGAlgebraMap(this.nextFreeGAlgebra, valueList)
    }

    private val nextDifferential: Derivation<IntDegree, Monomial<IntDegree, INext>, S, V, M> by lazy {
        val degree = this.getDegreeToAddIndeterminate() + 1
        val incl = this.inclusionToNext
        val differentialValueList: List<GVector<IntDegree, Monomial<IntDegree, INext>, S, V>> =
            this.minimalModel.freeDGAlgebra.generatorList.map {
                incl(this.minimalModel.freeDGAlgebra.differential(it))
            } + List(this.cocyclesToHit.size) {
                this.nextFreeGAlgebra.getZero(degree)
            } + this.cocyclesToKill.map {
                incl(it)
            }
        this.nextFreeGAlgebra.getDerivation(
            valueList = differentialValueList,
            derivationDegree = 1,
        )
    }

    private val nextFreeDGAlgebra: FreeDGAlgebra<IntDegree, INext, S, V, M> by lazy {
        FreeDGAlgebra(this.nextFreeGAlgebra, this.nextDifferential)
    }

    private val nextDGAlgebraMap: DGAlgebraMap<IntDegree, Monomial<IntDegree, INext>, B, S, V, M> by lazy {
        val dgAlgebraMapValueList = this.minimalModel.freeDGAlgebra.generatorList.map {
            this.minimalModel.dgAlgebraMap(it)
        } + this.cocyclesToHit + this.cocyclesToKill.map { cocycleToKill ->
            val targetCoboundary = this.minimalModel.dgAlgebraMap(cocycleToKill)
            val boundingTargetCochain = this.minimalModel.targetDGAlgebra.differential.findPreimage(targetCoboundary)
                ?: throw IllegalStateException("This can't happen! Failed to find preimage of d.")
            boundingTargetCochain
        }
        this.nextFreeDGAlgebra.getDGAlgebraMap(this.minimalModel.targetDGAlgebra, dgAlgebraMapValueList)
    }
}

internal abstract class NextMMCalculator<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    MMNext : GenericMinimalModel<MMIndeterminateName, B, S, V, M>,
    >(minimalModel: GenericMinimalModel<MMIndeterminateName, B, S, V, M>) :
    AbstractNextMMCalculator<
        MMIndeterminateName,
        MMIndeterminateName,
        B,
        S,
        V,
        M,
        MMNext,
        >(minimalModel) {

    final override fun getIndeterminateName(
        degree: Int,
        index: Int,
        totalNumberInDegree: Int,
        type: MMIndeterminateType
    ): MMIndeterminateName {
        return MMIndeterminateName(degree, index, totalNumberInDegree, type)
    }

    final override fun convertIndeterminate(
        indeterminate: Indeterminate<IntDegree, MMIndeterminateName>
    ): Indeterminate<IntDegree, MMIndeterminateName> {
        return indeterminate
    }
}
