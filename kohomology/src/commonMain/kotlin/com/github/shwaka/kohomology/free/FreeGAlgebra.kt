package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GAlgebraOperations
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOperations
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.IntDegree
import com.github.shwaka.kohomology.dg.IntDegreeMonoid
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.vectsp.BasisName

interface FreeGAlgebraOperations<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun parse(text: String): GVectorOrZero<Monomial<I>, IntDegree, S, V>
}

class FreeGAlgebraContext<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<Monomial<I>, IntDegree, S, V>,
    gAlgebraOperations: GAlgebraOperations<Monomial<I>, IntDegree, S, V, M>,
    freeGAlgebraOperations: FreeGAlgebraOperations<I, S, V, M>
) : GAlgebraContext<Monomial<I>, IntDegree, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations),
    FreeGAlgebraOperations<I, S, V, M> by freeGAlgebraOperations

class FreeGAlgebra<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    val indeterminateList: List<Indeterminate<I>>
) : MonoidGAlgebra<IntDegree, Monomial<I>, FreeMonoid<I>, S, V, M>(matrixSpace, IntDegreeMonoid, FreeMonoid(indeterminateList), FreeGAlgebra.getName(indeterminateList)),
    FreeGAlgebraOperations<I, S, V, M> {
    override val context: FreeGAlgebraContext<I, S, V, M> by lazy {
        FreeGAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this, this)
    }
    val generatorList: List<GVector<Monomial<I>, IntDegree, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val monomial = Monomial.fromIndeterminate(this.indeterminateList, indeterminate)
            this.fromBasisName(monomial, indeterminate.degree)
        }

    fun getDerivation(valueList: List<GVectorOrZero<Monomial<I>, IntDegree, S, V>>, derivationDegree: IntAsDegree): Derivation<Monomial<I>, IntDegree, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of a derivation")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                if (value.degree.toInt() != indeterminate.degree + derivationDegree)
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
        return Derivation.fromGVectors(this, IntDegree(derivationDegree), this.matrixSpace, name) { k ->
            val sourceVectorSpace = this[k]
            // val targetVectorSpace = this[k + derivationDegree]
            sourceVectorSpace.basisNames.map { monomial: Monomial<I> ->
                this.getDerivationValue(gVectorValueList, monomial, k.toInt() + derivationDegree)
            }
        }
    }

    private fun getDerivationValue(
        valueList: List<GVector<Monomial<I>, IntDegree, S, V>>,
        monomial: Monomial<I>,
        valueDegree: IntAsDegree
    ): GVector<Monomial<I>, IntDegree, S, V> {
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
        target: GAlgebra<B, IntDegree, S, V, M>,
        valueList: List<GVectorOrZero<B, IntDegree, S, V>>,
    ): GAlgebraMap<Monomial<I>, B, IntDegree, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of an algebra map")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                if (value.degree.toInt() != indeterminate.degree)
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
        target: GAlgebra<B, IntDegree, S, V, M>,
        valueList: List<GVector<B, IntDegree, S, V>>,
        monomial: Monomial<I>
    ): GVector<B, IntDegree, S, V> {
        return target.context.run {
            monomial.exponentList.mapIndexed { index, exponent ->
                valueList[index].pow(exponent)
            }.fold(this.unit) { acc, gVector ->
                acc * gVector
            }
        }
    }

    fun containsIndeterminate(indeterminateIndex: Int, element: GVector<Monomial<I>, IntDegree, S, V>): Boolean {
        return element.vector.toBasisMap().any { (monomial, _) ->
            monomial.containsIndeterminate(indeterminateIndex)
        }
    }

    override fun parse(text: String): GVectorOrZero<Monomial<I>, IntDegree, S, V> {
        val generators = this.indeterminateList.zip(this.generatorList).map { (indeterminate, generator) ->
            Pair(indeterminate.name.toString(), generator)
        }
        return this.parse(generators, text)
    }

    companion object {
        private fun <I : IndeterminateName> getName(indeterminateList: List<Indeterminate<I>>): String {
            val indeterminateString = indeterminateList.joinToString(", ") { it.toString() }
            return "Λ($indeterminateString)"
        }
    }
}
