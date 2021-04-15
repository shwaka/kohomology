package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

open class FreeDGAlgebra<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> (
    override val gAlgebra: FreeGAlgebra<I, S, V, M>,
    differential: Derivation<Monomial<I>, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGAlgebra<Monomial<I>, S, V, M>(gAlgebra, differential, matrixSpace) {
    companion object {
        operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<I>>,
            getDifferentialValueList: GAlgebraContext<Monomial<I>, S, V, M>.(List<GVector<Monomial<I>, S, V>>) -> List<GVectorOrZero<Monomial<I>, S, V>>
        ): FreeDGAlgebra<I, S, V, M> {
            val freeGAlgebra: FreeGAlgebra<I, S, V, M> = FreeGAlgebra(matrixSpace, indeterminateList)
            val valueList = freeGAlgebra.context.run {
                getDifferentialValueList(freeGAlgebra.generatorList)
            }
            for (i in valueList.indices) {
                val value = valueList[i]
                if (value !is GVector)
                    continue
                if ((i until valueList.size).any { k -> freeGAlgebra.containsIndeterminate(k, value) }) {
                    throw IllegalArgumentException(
                        "The generator list of a FreeDGAlgebra should be sorted along a Sullivan filtration"
                    )
                }
            }
            val differential: Derivation<Monomial<I>, S, V, M> = freeGAlgebra.getDerivation(
                valueList = valueList,
                derivationDegree = 1
            )
            return FreeDGAlgebra(freeGAlgebra, differential, matrixSpace)
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
