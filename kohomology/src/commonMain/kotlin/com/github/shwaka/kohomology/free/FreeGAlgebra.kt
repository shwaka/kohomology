package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GAlgebraOperations
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOperations
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeMonoid
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeMonoid
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.vectsp.BasisName

interface FreeGAlgebraOperations<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun parse(text: String): GVectorOrZero<GeneralizedMonomial<I, D>, D, S, V>
}

class FreeGAlgebraContext<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<GeneralizedMonomial<I, D>, D, S, V>,
    gAlgebraOperations: GAlgebraOperations<GeneralizedMonomial<I, D>, D, S, V, M>,
    freeGAlgebraOperations: FreeGAlgebraOperations<I, D, S, V, M>
) : GAlgebraContext<GeneralizedMonomial<I, D>, D, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations),
    FreeGAlgebraOperations<I, D, S, V, M> by freeGAlgebraOperations

class FreeGAlgebra<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    degreeMonoid: DegreeMonoid<D>,
    val indeterminateList: List<GeneralizedIndeterminate<I, D>>
) : MonoidGAlgebra<D, GeneralizedMonomial<I, D>, GeneralizedFreeMonoid<I, D>, S, V, M>(matrixSpace, degreeMonoid, GeneralizedFreeMonoid(degreeMonoid, indeterminateList), FreeGAlgebra.getName(indeterminateList)),
    FreeGAlgebraOperations<I, D, S, V, M> {
    override val context: FreeGAlgebraContext<I, D, S, V, M> by lazy {
        FreeGAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this, this)
    }
    val generatorList: List<GVector<GeneralizedMonomial<I, D>, D, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val monomial = GeneralizedMonomial.fromIndeterminate(this.degreeMonoid, this.indeterminateList, indeterminate)
            this.fromBasisName(monomial, indeterminate.degree)
        }

    fun getDerivation(valueList: List<GVectorOrZero<GeneralizedMonomial<I, D>, D, S, V>>, derivationDegree: D): Derivation<GeneralizedMonomial<I, D>, D, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of a derivation")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                val expectedValueDegree = this.degreeMonoid.context.run {
                    indeterminate.degree + derivationDegree
                }
                if (value.degree != expectedValueDegree)
                    throw IllegalArgumentException(
                        "Illegal degree: the degree of the value of $indeterminate must be " +
                            "${indeterminate.degree} + $derivationDegree = $expectedValueDegree, " +
                            "but ${value.degree} was given"
                    )
            }
        }
        val gVectorValueList = valueList.mapIndexed { index, gVectorOrZero ->
            val valueDegree = this.degreeMonoid.context.run {
                this@FreeGAlgebra.indeterminateList[index].degree + derivationDegree
            }
            this.convertToGVector(gVectorOrZero, valueDegree)
        }
        val name = "Derivation(${valueList.joinToString(", ") { it.toString() }})"
        return Derivation.fromGVectors(this, derivationDegree, this.matrixSpace, name) { k ->
            val sourceVectorSpace = this[k]
            // val targetVectorSpace = this[k + derivationDegree]
            val targetDegree = this.degreeMonoid.context.run {
                k + derivationDegree
            }
            sourceVectorSpace.basisNames.map { monomial: GeneralizedMonomial<I, D> ->
                this.getDerivationValue(gVectorValueList, monomial, targetDegree)
            }
        }
    }

    fun getDerivation(valueList: List<GVectorOrZero<GeneralizedMonomial<I, D>, D, S, V>>, derivationDegree: IntAsDegree): Derivation<GeneralizedMonomial<I, D>, D, S, V, M> {
        return this.getDerivation(valueList, this.degreeMonoid.fromInt(derivationDegree))
    }

    private fun getDerivationValue(
        valueList: List<GVector<GeneralizedMonomial<I, D>, D, S, V>>,
        monomial: GeneralizedMonomial<I, D>,
        valueDegree: D
    ): GVector<GeneralizedMonomial<I, D>, D, S, V> {
        return this.monoid.allSeparations(monomial).map { separation ->
            val derivedSeparatedExponentList = this.indeterminateList.indices.map { i ->
                if (i == separation.index)
                    separation.separatedExponent - 1
                else
                    0
            }
            val derivedSeparatedMonomial = GeneralizedMonomial(this.degreeMonoid, this.indeterminateList, derivedSeparatedExponentList)
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
        target: GAlgebra<B, D, S, V, M>,
        valueList: List<GVectorOrZero<B, D, S, V>>,
    ): GAlgebraMap<GeneralizedMonomial<I, D>, B, D, S, V, M> {
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
            sourceVectorSpace.basisNames.map { monomial: GeneralizedMonomial<I, D> ->
                val gVectorValue = this.getAlgebraMapValue(target, gVectorValueList, monomial)
                gVectorValue
            }
        }
    }

    private fun <B : BasisName> getAlgebraMapValue(
        target: GAlgebra<B, D, S, V, M>,
        valueList: List<GVector<B, D, S, V>>,
        monomial: GeneralizedMonomial<I, D>
    ): GVector<B, D, S, V> {
        return target.context.run {
            monomial.exponentList.mapIndexed { index, exponent ->
                valueList[index].pow(exponent)
            }.fold(this.unit) { acc, gVector ->
                acc * gVector
            }
        }
    }

    fun containsIndeterminate(indeterminateIndex: Int, element: GVector<GeneralizedMonomial<I, D>, D, S, V>): Boolean {
        return element.vector.toBasisMap().any { (monomial, _) ->
            monomial.containsIndeterminate(indeterminateIndex)
        }
    }

    override fun parse(text: String): GVectorOrZero<GeneralizedMonomial<I, D>, D, S, V> {
        val generators = this.indeterminateList.zip(this.generatorList).map { (indeterminate, generator) ->
            Pair(indeterminate.name.toString(), generator)
        }
        return this.parse(generators, text)
    }

    companion object {
        private fun <I : IndeterminateName, D : Degree> getName(indeterminateList: List<GeneralizedIndeterminate<I, D>>): String {
            val indeterminateString = indeterminateList.joinToString(", ") { it.toString() }
            return "Λ($indeterminateString)"
        }

        operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<GeneralizedIndeterminate<I, IntDegree>>
        ): FreeGAlgebra<I, IntDegree, S, V, M> {
            return FreeGAlgebra(matrixSpace, IntDegreeMonoid, indeterminateList)
        }
    }
}
