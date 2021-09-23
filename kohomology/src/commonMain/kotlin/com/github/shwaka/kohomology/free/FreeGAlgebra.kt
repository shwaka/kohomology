package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GAlgebraOperations
import com.github.shwaka.kohomology.dg.GLinearMapWithDegreeChange
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOperations
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.degree.AugmentationDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.free.monoid.FreeMonoid
import com.github.shwaka.kohomology.free.monoid.FreeMonoidMorphismByDegreeChange
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.IntAsDegree
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.PrintConfig

public interface FreeGAlgebraOperations<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public fun parse(text: String): GVectorOrZero<D, Monomial<D, I>, S, V>
}

public class FreeGAlgebraContext<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, Monomial<D, I>, S, V>,
    gAlgebraOperations: GAlgebraOperations<D, Monomial<D, I>, S, V, M>,
    freeGAlgebraOperations: FreeGAlgebraOperations<D, I, S, V, M>
) : GAlgebraContext<D, Monomial<D, I>, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations),
    FreeGAlgebraOperations<D, I, S, V, M> by freeGAlgebraOperations

public class FreeGAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    override val degreeGroup: AugmentedDegreeGroup<D>,
    public val indeterminateList: List<Indeterminate<D, I>>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<Monomial<D, I>, S> = InternalPrintConfig.Companion::default,
) : MonoidGAlgebra<D, Monomial<D, I>, FreeMonoid<D, I>, S, V, M>(
    matrixSpace,
    degreeGroup,
    FreeMonoid(degreeGroup, indeterminateList),
    FreeGAlgebra.getName(indeterminateList),
    getInternalPrintConfig,
),
    FreeGAlgebraOperations<D, I, S, V, M> {
    override val context: FreeGAlgebraContext<D, I, S, V, M> by lazy {
        FreeGAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this, this)
    }
    public val generatorList: List<GVector<D, Monomial<D, I>, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val monomial = Monomial.fromIndeterminate(this.degreeGroup, this.indeterminateList, indeterminate)
            this.fromBasisName(monomial, indeterminate.degree)
        }

    public fun getDerivation(valueList: List<GVectorOrZero<D, Monomial<D, I>, S, V>>, derivationDegree: D): Derivation<D, Monomial<D, I>, S, V, M> {
        if (valueList.size != this.indeterminateList.size)
            throw InvalidSizeException("Invalid size of the list of values of a derivation")
        for ((indeterminate, value) in this.indeterminateList.zip(valueList)) {
            if (value is GVector) {
                val expectedValueDegree = this.degreeGroup.context.run {
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
            val valueDegree = this.degreeGroup.context.run {
                this@FreeGAlgebra.indeterminateList[index].degree + derivationDegree
            }
            this.convertToGVector(gVectorOrZero, valueDegree)
        }
        val name = "Derivation(${valueList.joinToString(", ") { it.toString() }})"
        return Derivation.fromGVectors(this, derivationDegree, this.matrixSpace, name) { k ->
            val sourceVectorSpace = this[k]
            // val targetVectorSpace = this[k + derivationDegree]
            val targetDegree = this.degreeGroup.context.run {
                k + derivationDegree
            }
            sourceVectorSpace.basisNames.map { monomial: Monomial<D, I> ->
                this.getDerivationValue(gVectorValueList, monomial, targetDegree)
            }
        }
    }

    public fun getDerivation(valueList: List<GVectorOrZero<D, Monomial<D, I>, S, V>>, derivationDegree: IntAsDegree): Derivation<D, Monomial<D, I>, S, V, M> {
        return this.getDerivation(valueList, this.degreeGroup.fromInt(derivationDegree))
    }

    private fun getDerivationValue(
        valueList: List<GVector<D, Monomial<D, I>, S, V>>,
        monomial: Monomial<D, I>,
        valueDegree: D
    ): GVector<D, Monomial<D, I>, S, V> {
        val terms: List<GVector<D, Monomial<D, I>, S, V>> =
            this.monoid.allSeparations(monomial).map { separation ->
                val derivedSeparatedExponentList = this.indeterminateList.indices.map { i ->
                    if (i == separation.index)
                        separation.separatedExponent - 1
                    else
                        0
                }
                val derivedSeparatedMonomial = Monomial(this.degreeGroup, this.indeterminateList, derivedSeparatedExponentList)
                val derivedSeparatedGVector = this.context.run {
                    separation.separatedExponent *
                        this@FreeGAlgebra.fromBasisName(derivedSeparatedMonomial, derivedSeparatedMonomial.degree) *
                        valueList[separation.index]
                }
                val remainingGVector = this.fromBasisName(separation.remainingMonomial, separation.remainingMonomial.degree)
                this.context.run {
                    derivedSeparatedGVector * remainingGVector * separation.sign
                }
            }
        return this.context.run {
            terms.sum(valueDegree)
        }
    }

    public fun <B : BasisName> getGAlgebraMap(
        target: GAlgebra<D, B, S, V, M>,
        valueList: List<GVectorOrZero<D, B, S, V>>,
    ): GAlgebraMap<D, Monomial<D, I>, B, S, V, M> {
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
            sourceVectorSpace.basisNames.map { monomial: Monomial<D, I> ->
                val gVectorValue = this.getAlgebraMapValue(target, gVectorValueList, monomial)
                gVectorValue
            }
        }
    }

    private fun <B : BasisName> getAlgebraMapValue(
        target: GAlgebra<D, B, S, V, M>,
        valueList: List<GVector<D, B, S, V>>,
        monomial: Monomial<D, I>
    ): GVector<D, B, S, V> {
        return target.context.run {
            monomial.exponentList.mapIndexed { index, exponent ->
                valueList[index].pow(exponent)
            }.fold(this.unit) { acc, gVector ->
                acc * gVector
            }
        }
    }

    public fun containsIndeterminate(indeterminateIndex: Int, element: GVector<D, Monomial<D, I>, S, V>): Boolean {
        return element.vector.toBasisMap().any { (monomial, _) ->
            monomial.containsIndeterminate(indeterminateIndex)
        }
    }

    override fun parse(text: String): GVectorOrZero<D, Monomial<D, I>, S, V> {
        val generators = this.indeterminateList.zip(this.generatorList).map { (indeterminate, generator) ->
            Pair(indeterminate.name.toString(), generator)
        }
        return this.parse(generators, text)
    }

    public fun <D_ : Degree> convertDegree(
        degreeMorphism: AugmentedDegreeMorphism<D, D_>
    ): Pair<FreeGAlgebra<D_, I, S, V, M>, GLinearMapWithDegreeChange<D, Monomial<D, I>, D_, Monomial<D_, I>, S, V, M>> {
        val newIndeterminateList = this.indeterminateList.map { indeterminate ->
            indeterminate.convertDegree(degreeMorphism)
        }
        val newFreeGAlgebra = FreeGAlgebra(this.matrixSpace, degreeMorphism.target, newIndeterminateList)
        val freeMonoidMorphism = FreeMonoidMorphismByDegreeChange(this.monoid, degreeMorphism)
        val gLinearMapWithDegreeChange = GLinearMapWithDegreeChange(
            this,
            newFreeGAlgebra,
            degreeMorphism,
            this.matrixSpace,
            "${this.name} (degree changed)"
        ) { monomial -> freeMonoidMorphism(monomial) }
        return Pair(newFreeGAlgebra, gLinearMapWithDegreeChange)
    }

    public fun toIntDegree(): Pair<FreeGAlgebra<IntDegree, I, S, V, M>, GLinearMapWithDegreeChange<D, Monomial<D, I>, IntDegree, Monomial<IntDegree, I>, S, V, M>> {
        val degreeMorphism = AugmentationDegreeMorphism(this.degreeGroup)
        return this.convertDegree(degreeMorphism)
    }

    public companion object {
        private fun <D : Degree, I : IndeterminateName> getName(indeterminateList: List<Indeterminate<D, I>>): String {
            val indeterminateString = indeterminateList.joinToString(", ") { it.toString() }
            return "Λ($indeterminateString)"
        }

        public operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<IntDegree, I>>
        ): FreeGAlgebra<IntDegree, I, S, V, M> {
            return FreeGAlgebra(matrixSpace, IntDegreeGroup, indeterminateList)
        }
    }
}
