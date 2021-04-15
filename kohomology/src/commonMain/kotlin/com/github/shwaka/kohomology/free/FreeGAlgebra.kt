package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.BasisName

class FreeGAlgebra<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    val indeterminateList: List<Indeterminate<I>>
) : MonoidGAlgebra<Monomial<I>, FreeMonoid<I>, S, V, M>(
    matrixSpace,
    FreeMonoid(indeterminateList),
    FreeGAlgebra.getName(indeterminateList)
) {
    val generatorList: List<GVector<Monomial<I>, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val monomial = Monomial.fromIndeterminate(this.indeterminateList, indeterminate)
            this.fromBasisName(monomial, indeterminate.degree)
        }

    fun getDerivation(valueList: List<GVectorOrZero<Monomial<I>, S, V>>, derivationDegree: Degree): Derivation<Monomial<I>, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of a derivation")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                if (value.degree != indeterminate.degree + derivationDegree)
                    throw IllegalArgumentException(
                        "Illegal degree: the degree of the value of $indeterminate must be " +
                            "${indeterminate.degree} + $derivationDegree = ${indeterminate.degree + derivationDegree}, " +
                            "but ${value.degree} was given"
                    )
            }
        }
        val gVectorValueList = valueList.mapIndexed { index, gVectorOrZero ->
            val valueDegree = this.indeterminateList[index].degree + derivationDegree
            this.convertToGVector(gVectorOrZero, valueDegree)
        }
        val name = "Derivation(${valueList.joinToString(", ") { it.toString() }})"
        return Derivation.fromGVectors(this, derivationDegree, this.matrixSpace, name) { k ->
            val sourceVectorSpace = this[k]
            // val targetVectorSpace = this[k + derivationDegree]
            sourceVectorSpace.basisNames.map { monomial: Monomial<I> ->
                this.getDerivationValue(gVectorValueList, monomial, k + derivationDegree)
            }
        }
    }

    private fun getDerivationValue(
        valueList: List<GVector<Monomial<I>, S, V>>,
        monomial: Monomial<I>,
        valueDegree: Degree
    ): GVector<Monomial<I>, S, V> {
        return this.monoid.allSeparations(monomial).map { separation ->
            val derivedSeparatedExponentList = this.indeterminateList.indices.map { i ->
                if (i == separation.index)
                    separation.separatedExponent - 1
                else
                    0
            }
            val derivedSeparatedMonomial = Monomial(this.indeterminateList, derivedSeparatedExponentList)
            val derivedSeparatedGVector = this.context.run {
                separation.separatedExponent *
                    this@FreeGAlgebra.fromBasisName(derivedSeparatedMonomial, derivedSeparatedMonomial.degree) *
                    valueList[separation.index]
            }
            val remainingGVector = this.fromBasisName(separation.remainingMonomial, separation.remainingMonomial.degree)
            this.context.run {
                derivedSeparatedGVector * remainingGVector * separation.sign
            }
        }.fold(this.getZero(valueDegree)) { acc, gVector ->
            this.context.run { acc + gVector }
        }
    }

    fun <B : BasisName> getGAlgebraMap(
        target: GAlgebra<B, S, V, M>,
        valueList: List<GVectorOrZero<B, S, V>>,
    ): GAlgebraMap<Monomial<I>, B, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of an algebra map")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                if (value.degree != indeterminate.degree)
                    throw IllegalArgumentException(
                        "Illegal degree: the degree of the value of $indeterminate must be ${indeterminate.degree}" +
                            "but ${value.degree} was given"
                    )
            }
        }
        val gVectorValueList = valueList.mapIndexed { index, gVectorOrZero ->
            val valueDegree = this.indeterminateList[index].degree
            target.convertToGVector(gVectorOrZero, valueDegree)
        }
        val name = "AlgebraMap(${valueList.joinToString(", ") { it.toString() }})"
        return GAlgebraMap.fromGVectors(this, target, this.matrixSpace, name) { k ->
            val sourceVectorSpace = this[k]
            // val targetVectorSpace = target[k]
            sourceVectorSpace.basisNames.map { monomial: Monomial<I> ->
                val gVectorValue = this.getAlgebraMapValue(target, gVectorValueList, monomial)
                gVectorValue
            }
        }
    }

    private fun <B : BasisName> getAlgebraMapValue(
        target: GAlgebra<B, S, V, M>,
        valueList: List<GVector<B, S, V>>,
        monomial: Monomial<I>
    ): GVector<B, S, V> {
        return target.context.run {
            monomial.exponentList.mapIndexed { index, exponent ->
                valueList[index].pow(exponent)
            }.fold(this.unit) { acc, gVector ->
                acc * gVector
            }
        }
    }

    fun containsIndeterminate(indeterminateIndex: Int, element: GVector<Monomial<I>, S, V>): Boolean {
        return element.vector.toMap().any { (monomial, _) ->
            monomial.containsIndeterminate(indeterminateIndex)
        }
    }

    companion object {
        private fun <I : IndeterminateName> getName(indeterminateList: List<Indeterminate<I>>): String {
            val indeterminateString = indeterminateList.joinToString(", ") { it.toString() }
            return "Λ($indeterminateString)"
        }
    }
}
