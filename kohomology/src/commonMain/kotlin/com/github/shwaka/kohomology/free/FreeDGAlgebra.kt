package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraContext
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.DGDerivation
import com.github.shwaka.kohomology.dg.DGVectorOperations
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraOperations
import com.github.shwaka.kohomology.dg.GLinearMapWithDegreeChange
import com.github.shwaka.kohomology.dg.GMagmaOperations
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOperations
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.degree.AugmentationDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.model.CopiedName
import com.github.shwaka.kohomology.model.FreePathSpace
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName

public class FreeDGAlgebraContext<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, Monomial<D, I>, S, V>,
    gMagmaOperations: GMagmaOperations<D, Monomial<D, I>, S, V, M>,
    gAlgebraOperations: GAlgebraOperations<D, Monomial<D, I>, S, V, M>,
    dgVectorOperations: DGVectorOperations<D, Monomial<D, I>, S, V, M>,
    freeGAlgebraOperations: FreeGAlgebraOperations<D, I, S, V, M>
) : DGAlgebraContext<D, Monomial<D, I>, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations, gAlgebraOperations, dgVectorOperations),
    FreeGAlgebraOperations<D, I, S, V, M> by freeGAlgebraOperations

public data class GeneratorOfFreeDGA<D : Degree>(val name: String, val degree: D, val differentialValue: String) {
    public companion object {
        public operator fun invoke(name: String, degree: Int, differentialValue: String): GeneratorOfFreeDGA<IntDegree> {
            return GeneratorOfFreeDGA(name, IntDegree(degree), differentialValue)
        }
    }
}

private typealias GetDifferentialValueList<D, I, S, V, M> =
    FreeGAlgebraContext<D, I, S, V, M>.(List<GVector<D, Monomial<D, I>, S, V>>) -> List<GVectorOrZero<D, Monomial<D, I>, S, V>>

public open class FreeDGAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> (
    override val gAlgebra: FreeGAlgebra<D, I, S, V, M>,
    override val differential: Derivation<D, Monomial<D, I>, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGAlgebra<D, Monomial<D, I>, S, V, M>(gAlgebra, differential, matrixSpace),
    Printable {
    override val context: FreeDGAlgebraContext<D, I, S, V, M> by lazy {
        FreeDGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this.gAlgebra, this, this.gAlgebra)
    }
    override val degreeGroup: AugmentedDegreeGroup<D> by lazy {
        // Use by lazy to avoid accessing non-final property in constructor
        this.gAlgebra.degreeGroup
    }

    public companion object {
        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: List<Indeterminate<D, I>>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<Monomial<D, I>, S>,
            getDifferentialValueList: GetDifferentialValueList<D, I, S, V, M>,
        ): FreeDGAlgebra<D, I, S, V, M> {
            val freeGAlgebra: FreeGAlgebra<D, I, S, V, M> = FreeGAlgebra(matrixSpace, degreeGroup, indeterminateList, getInternalPrintConfig)
            val valueList = freeGAlgebra.context.run {
                getDifferentialValueList(freeGAlgebra.generatorList)
            }
            val differential: Derivation<D, Monomial<D, I>, S, V, M> = freeGAlgebra.getDerivation(
                valueList = valueList,
                derivationDegree = 1
            )
            for (i in valueList.indices) {
                val value = valueList[i]
                if (value !is GVector)
                    continue
                if ((i until valueList.size).any { k -> freeGAlgebra.containsIndeterminate(k, value) }) {
                    throw IllegalArgumentException(
                        "The generator list of a FreeDGAlgebra should be sorted along a Sullivan filtration"
                    )
                }
                val dValue = differential(value)
                if (dValue.isNotZero())
                    throw IllegalArgumentException(
                        "d(${indeterminateList[i]}) must be a cocycle, " +
                            "but your input is $value with d(d(${indeterminateList[i]})) = d($value) = $dValue (!= 0)"
                    )
            }
            return FreeDGAlgebra(freeGAlgebra, differential, matrixSpace)
        }

        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: List<Indeterminate<D, I>>,
            getDifferentialValueList: GetDifferentialValueList<D, I, S, V, M>,
        ): FreeDGAlgebra<D, I, S, V, M> {
            return FreeDGAlgebra.invoke(matrixSpace, degreeGroup, indeterminateList, InternalPrintConfig.Companion::default, getDifferentialValueList)
        }

        public operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<IntDegree, I>>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<Monomial<IntDegree, I>, S> = InternalPrintConfig.Companion::default,
            getDifferentialValueList: GetDifferentialValueList<IntDegree, I, S, V, M>,
        ): FreeDGAlgebra<IntDegree, I, S, V, M> {
            return FreeDGAlgebra.invoke(matrixSpace, IntDegreeGroup, indeterminateList, getInternalPrintConfig, getDifferentialValueList)
        }

        public operator fun <D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: AugmentedDegreeGroup<D>,
            generatorList: List<GeneratorOfFreeDGA<D>>
        ): FreeDGAlgebra<D, StringIndeterminateName, S, V, M> {
            val indeterminateList = generatorList.map { Indeterminate(it.name, it.degree) }
            val getDifferentialValueList: GetDifferentialValueList<D, StringIndeterminateName, S, V, M> = {
                generatorList.map { parse(it.differentialValue) }
            }
            return FreeDGAlgebra.invoke(matrixSpace, degreeGroup, indeterminateList, getDifferentialValueList)
        }

        public operator fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            generatorList: List<GeneratorOfFreeDGA<IntDegree>>
        ): FreeDGAlgebra<IntDegree, StringIndeterminateName, S, V, M> {
            return FreeDGAlgebra.invoke(matrixSpace, IntDegreeGroup, generatorList)
        }
    }

    public fun <B : BasisName> getDGAlgebraMap(
        target: DGAlgebra<D, B, S, V, M>,
        valueList: List<GVectorOrZero<D, B, S, V>>,
    ): DGAlgebraMap<D, Monomial<D, I>, B, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getGAlgebraMap(target.gAlgebra, valueList)
        for (v in this.gAlgebra.generatorList) {
            val fdv = gAlgebraMap(this.differential(v))
            val dfv = target.differential(gAlgebraMap(v))
            if (fdv != dfv) {
                throw IllegalArgumentException(
                    "The given algebra map does not commute with the differential on the generator $v:\n" +
                        "f(d($v)) = $fdv, d(f($v)) = $dfv"
                )
            }
        }
        return DGAlgebraMap(this, target, gAlgebraMap)
    }

    public fun getDGDerivation(
        valueList: List<GVectorOrZero<D, Monomial<D, I>, S, V>>,
        derivationDegree: D,
    ): DGDerivation<D, Monomial<D, I>, S, V, M> {
        val derivation = this.gAlgebra.getDerivation(valueList, derivationDegree)
        this.context.run {
            for (v in this@FreeDGAlgebra.gAlgebra.generatorList) {
                val fdv = derivation(d(v))
                val dfv = d(derivation(v))
                val sign: Sign = Sign.fromParity(derivationDegree.isEven())
                if (fdv != sign * dfv) {
                    throw IllegalArgumentException(
                        "The given derivation does not commute with the differential on the generator $v:\n" +
                            "f(d($v)) = $fdv, d(f($v)) = $dfv"
                    )
                }
            }
        }
        return DGDerivation(this, derivation)
    }

    public fun getDGDerivation(
        valueList: List<GVectorOrZero<D, Monomial<D, I>, S, V>>,
        derivationDegree: IntAsDegree,
    ): DGDerivation<D, Monomial<D, I>, S, V, M> {
        return this.getDGDerivation(valueList, this.degreeGroup.fromInt(derivationDegree))
    }

    public fun <BS : BasisName, BT : BasisName> findLift(
        underlyingMap: DGAlgebraMap<D, Monomial<D, I>, BT, S, V, M>,
        surjectiveQuasiIsomorphism: DGAlgebraMap<D, BS, BT, S, V, M>,
    ): DGAlgebraMap<D, Monomial<D, I>, BS, S, V, M> {
        require(underlyingMap.source == this) { "Invalid diagram: ${underlyingMap.source} != $this" }
        require(underlyingMap.target == surjectiveQuasiIsomorphism.target) {
            "Invalid diagram: ${underlyingMap.target} != ${surjectiveQuasiIsomorphism.target}"
        }
        val n = this.gAlgebra.generatorList.size
        val liftTarget = surjectiveQuasiIsomorphism.source
        val zeroGVector = liftTarget.context.run { zeroGVector }
        val liftValueList: MutableList<GVectorOrZero<D, BS, S, V>> = MutableList(n) { zeroGVector }
        for (i in 0 until n) {
            val currentLift = this.gAlgebra.getGAlgebraMap(liftTarget.gAlgebra, liftValueList)
            // ↑ This should be getGAlgebraMap, NOT getDGAlgebraMap
            // since currentLift does NOT commute with differentials for i < n - 1
            val vi = this.gAlgebra.generatorList[i]
            val cochainLift = surjectiveQuasiIsomorphism.findLift(
                targetCochain = underlyingMap(vi),
                sourceCocycle = currentLift(this.differential(vi))
            )
            liftValueList[i] = cochainLift
        }
        return this.getDGAlgebraMap(liftTarget, liftValueList)
    }

    public fun <B : BasisName> findSection(
        surjectiveQuasiIsomorphism: DGAlgebraMap<D, B, Monomial<D, I>, S, V, M>,
    ): DGAlgebraMap<D, Monomial<D, I>, B, S, V, M> {
        return this.findLift(
            underlyingMap = this.getId(),
            surjectiveQuasiIsomorphism = surjectiveQuasiIsomorphism
        )
    }

    public data class LiftWithHomotopy<D : Degree, I : IndeterminateName, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
        val lift: DGAlgebraMap<D, Monomial<D, I>, BS, S, V, M>,
        val homotopy: DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, BT, S, V, M>,
        val freePathSpace: FreePathSpace<D, I, S, V, M>,
    )

    public fun <BS : BasisName, BT : BasisName> findLiftUpToHomotopy(
        underlyingMap: DGAlgebraMap<D, Monomial<D, I>, BT, S, V, M>,
        quasiIsomorphism: DGAlgebraMap<D, BS, BT, S, V, M>,
        freePathSpace: FreePathSpace<D, I, S, V, M>? = null,
    ): LiftWithHomotopy<D, I, BS, BT, S, V, M> {
        require(underlyingMap.source == this) { "Invalid diagram: ${underlyingMap.source} != $this" }
        require(underlyingMap.target == quasiIsomorphism.target) {
            "Invalid diagram: ${underlyingMap.target} != ${quasiIsomorphism.target}"
        }
        val freePathSpaceNonNull = freePathSpace ?: FreePathSpace(this)
        val n = this.gAlgebra.generatorList.size
        val liftTarget = quasiIsomorphism.source
        val homotopyTarget = quasiIsomorphism.target
        val liftValueList: MutableList<GVectorOrZero<D, BS, S, V>> = MutableList(n) {
            liftTarget.context.run { zeroGVector }
        }
        val homotopyValueList: MutableList<GVectorOrZero<D, BT, S, V>> = MutableList(3 * n) { index ->
            when {
                index < n -> underlyingMap(this.gAlgebra.generatorList[index])
                else -> homotopyTarget.context.run { zeroGVector }
            }
        }
        for (i in 0 until n) {
            val currentLift = this.gAlgebra.getGAlgebraMap(liftTarget.gAlgebra, liftValueList)
            val currentHomotopy = freePathSpaceNonNull.gAlgebra.getGAlgebraMap(homotopyTarget.gAlgebra, homotopyValueList)
            // ↑ This should be getGAlgebraMap, NOT getDGAlgebraMap
            // since currentLift and currentHomotopy does NOT commute with differentials for i < n - 1
            val vi = this.gAlgebra.generatorList[i]
            val targetCochain = homotopyTarget.context.run {
                val vi1 = freePathSpaceNonNull.gAlgebra.generatorList[i]
                val vi2 = freePathSpaceNonNull.gAlgebra.generatorList[n + i]
                val svi = freePathSpaceNonNull.gAlgebra.generatorList[2 * n + i]
                val x = freePathSpaceNonNull.context.run {
                    vi2 - vi1 - d(svi)
                }
                underlyingMap(vi) + currentHomotopy(x)
            }
            val sourceCocycle = currentLift(this.differential(vi))
            val liftWithBoundingCochain = quasiIsomorphism.findLiftUpToHomotopy(
                targetCochain = targetCochain,
                sourceCocycle = sourceCocycle,
            )
            liftValueList[i] = liftWithBoundingCochain.lift
            homotopyValueList[n + i] = quasiIsomorphism(liftWithBoundingCochain.lift)
            homotopyValueList[2 * n + i] = liftWithBoundingCochain.boundingCochain
        }
        val lift = this.getDGAlgebraMap(liftTarget, liftValueList)
        val homotopy = freePathSpaceNonNull.getDGAlgebraMap(homotopyTarget, homotopyValueList)
        return LiftWithHomotopy(lift = lift, homotopy = homotopy, freePathSpace = freePathSpaceNonNull)
    }

    public fun <B : BasisName> findSectionUpToHomotopy(
        quasiIsomorphism: DGAlgebraMap<D, B, Monomial<D, I>, S, V, M>,
        freePathSpace: FreePathSpace<D, I, S, V, M>? = null,
    ): LiftWithHomotopy<D, I, B, Monomial<D, I>, S, V, M> {
        return this.findLiftUpToHomotopy(
            underlyingMap = this.getId(),
            quasiIsomorphism = quasiIsomorphism,
            freePathSpace = freePathSpace,
        )
    }

    public fun <D_ : Degree> convertDegree(
        degreeMorphism: AugmentedDegreeMorphism<D, D_>
    ): Pair<FreeDGAlgebra<D_, I, S, V, M>, GLinearMapWithDegreeChange<D, Monomial<D, I>, D_, Monomial<D_, I>, S, V, M>> {
        val (newFreeGAlgebra, changeDegree) = this.gAlgebra.convertDegree(degreeMorphism)
        val differentialValueList = this.gAlgebra.generatorList.map { v ->
            val dv = this.context.run { d(v) }
            changeDegree(dv)
        }
        val differential = newFreeGAlgebra.getDerivation(differentialValueList, 1)
        val matrixSpace = this.matrixSpace
        val newFreeDGAlgebra = FreeDGAlgebra(newFreeGAlgebra, differential, matrixSpace)
        return Pair(newFreeDGAlgebra, changeDegree)
    }

    public fun toIntDegree(): Pair<FreeDGAlgebra<IntDegree, I, S, V, M>, GLinearMapWithDegreeChange<D, Monomial<D, I>, IntDegree, Monomial<IntDegree, I>, S, V, M>> {
        val degreeMorphism = AugmentationDegreeMorphism(this.degreeGroup)
        return this.convertDegree(degreeMorphism)
    }

    override fun toString(printConfig: PrintConfig): String {
        val gAlgebraString = this.gAlgebra.toString(printConfig)
        return "($gAlgebraString, d)"
    }
}
