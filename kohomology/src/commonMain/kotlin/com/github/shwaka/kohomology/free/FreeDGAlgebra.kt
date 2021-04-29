package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraContext
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.DGVectorOperations
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraOperations
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOperations
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.IntDeg
import com.github.shwaka.kohomology.vectsp.BasisName

class FreeDGAlgebraContext<I : IndeterminateName, D:Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<Monomial<I>, D,S, V>,
    gAlgebraOperations: GAlgebraOperations<Monomial<I>, D,S, V, M>,
    dgVectorOperations: DGVectorOperations<Monomial<I>, D,S, V, M>,
    freeGAlgebraOperations: FreeGAlgebraOperations<I, D,S, V, M>
) : DGAlgebraContext<Monomial<I>, D,S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations, dgVectorOperations),
    FreeGAlgebraOperations<I,D, S, V, M> by freeGAlgebraOperations

data class GeneratorOfFreeDGA(val name: String, val degree: IntDeg, val differentialValue: String)

typealias GetDifferentialValueList<I,D, S, V, M> =
    FreeGAlgebraContext<I, D,S, V, M>.(List<GVector<Monomial<I>, S, V>>) -> List<GVectorOrZero<Monomial<I>, D,S, V>>

open class FreeDGAlgebra<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> (
    override val gAlgebra: FreeGAlgebra<I, D,S, V, M>,
    differential: Derivation<Monomial<I>, D,S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGAlgebra<Monomial<I>, D,S, V, M>(gAlgebra, differential, matrixSpace) {
    override val context by lazy {
        FreeDGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this, this.gAlgebra)
    }

    companion object {
        operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<I>>,
            getDifferentialValueList: GetDifferentialValueList<I, S, V, M>
        ): FreeDGAlgebra<I, S, V, M> {
            val freeGAlgebra: FreeGAlgebra<I, S, V, M> = FreeGAlgebra(matrixSpace, indeterminateList)
            val valueList = freeGAlgebra.context.run {
                getDifferentialValueList(freeGAlgebra.generatorList)
            }
            val differential: Derivation<Monomial<I>, S, V, M> = freeGAlgebra.getDerivation(
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

        operator fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            generatorList: List<GeneratorOfFreeDGA>
        ): FreeDGAlgebra<StringIndeterminateName, S, V, M> {
            val indeterminateList = generatorList.map { Indeterminate(it.name, it.degree) }
            val getDifferentialValueList: GetDifferentialValueList<StringIndeterminateName, S, V, M> = {
                generatorList.map { parse(it.differentialValue) }
            }
            return FreeDGAlgebra.invoke(matrixSpace, indeterminateList, getDifferentialValueList)
        }
    }

    fun <B : BasisName> getDGAlgebraMap(
        target: DGAlgebra<B, S, V, M>,
        valueList: List<GVectorOrZero<B, S, V>>,
    ): DGAlgebraMap<Monomial<I>, B, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getGAlgebraMap(target.gAlgebra, valueList)
        return DGAlgebraMap(this, target, gAlgebraMap)
    }

    fun <BS : BasisName, BT : BasisName> findLift(
        underlyingMap: DGAlgebraMap<Monomial<I>, BT, S, V, M>,
        surjectiveQuasiIsomorphism: DGAlgebraMap<BS, BT, S, V, M>,
    ): DGAlgebraMap<Monomial<I>, BS, S, V, M> {
        if (underlyingMap.source != this)
            throw IllegalArgumentException("Invalid diagram: ${underlyingMap.source} != $this")
        if (underlyingMap.target != surjectiveQuasiIsomorphism.target)
            throw IllegalArgumentException("Invalid diagram: ${underlyingMap.target} != ${surjectiveQuasiIsomorphism.target}")
        val n = this.gAlgebra.generatorList.size
        val liftTarget = surjectiveQuasiIsomorphism.source
        val zeroGVector = liftTarget.context.run { zeroGVector }
        val liftValueList: MutableList<GVectorOrZero<BS, S, V>> = MutableList(n) { zeroGVector }
        for (i in 0 until n) {
            val currentLift = this.getDGAlgebraMap(liftTarget, liftValueList)
            val vi = this.gAlgebra.generatorList[i]
            val cochainLift = surjectiveQuasiIsomorphism.findLift(
                targetCochain = underlyingMap(vi),
                sourceCoboundary = currentLift(this.differential(vi))
            )
            liftValueList[i] = cochainLift
        }
        return this.getDGAlgebraMap(liftTarget, liftValueList)
    }

    fun <B : BasisName> findSection(
        surjectiveQuasiIsomorphism: DGAlgebraMap<B, Monomial<I>, S, V, M>,
    ): DGAlgebraMap<Monomial<I>, B, S, V, M> {
        return this.findLift(
            underlyingMap = this.getId(),
            surjectiveQuasiIsomorphism = surjectiveQuasiIsomorphism
        )
    }
}
