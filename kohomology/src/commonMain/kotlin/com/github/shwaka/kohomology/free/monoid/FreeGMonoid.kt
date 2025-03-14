package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeMorphism
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.util.Sign

/**
 * The free object among commutative [GMonoid]s.
 */
public class FreeGMonoid<D : Degree, I : IndeterminateName> (
    override val degreeGroup: AugmentedDegreeGroup<D>,
    indeterminateList: List<Indeterminate<D, I>>
) : GMonoid<D, Monomial<D, I>> {
    // constructor(
    //     indeterminateList: List<Indeterminate<I>>,
    // ) : this(IndeterminateList.from(indeterminateList))
    private val indeterminateListInternal = IndeterminateList.from(degreeGroup, indeterminateList)
    public val indeterminateList: List<Indeterminate<D, I>> by lazy {
        this.indeterminateListInternal.toList()
    }

    public companion object {
        public operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>
        ): FreeGMonoid<IntDegree, I> {
            return FreeGMonoid(IntDegreeGroup, indeterminateList)
        }
    }

    override val unit: Monomial<D, I> = Monomial.unit(this.degreeGroup, this.indeterminateListInternal)
    override val isCommutative: Boolean = true
    override val boundedness: Boundedness by lazy {
        val bound: Int? = if (this.indeterminateListInternal.isOddOnly) {
            this.indeterminateListInternal.degreeSumAsInt
        } else {
            null
        }
        when (this.indeterminateListInternal) {
            is PositiveIndeterminateList -> Boundedness(upperBound = bound, lowerBound = 0)
            is NegativeIndeterminateList -> Boundedness(upperBound = 0, lowerBound = bound)
        }
    }

    // val generatorList: List<Monomial<D, I>> by lazy {
    //     val n = this.indeterminateListInternal.size
    //     List(n) { i ->
    //         IntArray(n) { j -> if (i == j) 1 else 0 }
    //     }.map { exponentList ->
    //         this.fromExponentList(exponentList)
    //     }
    // }

    public fun fromExponentList(exponentList: IntArray): Monomial<D, I> {
        return Monomial(this.degreeGroup, this.indeterminateListInternal, exponentList)
    }

    override fun multiply(
        monoidElement1: Monomial<D, I>,
        monoidElement2: Monomial<D, I>
    ): SignedOrZero<Monomial<D, I>> {
        // if (monoidElement1.indeterminateList != monoidElement2.indeterminateList)
        //     throw IllegalArgumentException("Cannot multiply two monomials of different indeterminate")
        val size = this.indeterminateListInternal.size
        // val exponentList = monoidElement1.exponentList
        //     .zip(monoidElement2.exponentList)
        //     .map { (p, q) -> p + q }
        val exponentList = this.addExponentLists(monoidElement1.exponentList, monoidElement2.exponentList)
        for (i in 0 until size) {
            if ((this.indeterminateListInternal[i].degree.isOdd()) && (exponentList[i] >= 2))
                return Zero
        }
        var sign = Sign.PLUS
        for (i in 0 until size) {
            if ((this.indeterminateListInternal[i].degree.isOdd()) && (monoidElement1.exponentList[i] == 1)) {
                for (j in 0 until i) {
                    if ((this.indeterminateListInternal[j].degree.isOdd()) && (monoidElement2.exponentList[j] == 1)) {
                        sign = -sign
                    }
                }
            }
        }
        val monomial = Monomial(this.degreeGroup, this.indeterminateListInternal, exponentList)
        return Signed(monomial, sign)
    }

    private fun addExponentLists(exponentList1: IntArray, exponentList2: IntArray): IntArray {
        // return exponentList1.zip(exponentList2).map { (p, q) -> p + q }
        // return exponentList1.indices.map { i -> exponentList1[i] + exponentList2[i] }
        // return exponentList1.mapIndexed { index, exponent -> exponent + exponentList2[index] }
        return IntArray(exponentList1.size) { exponentList1[it] + exponentList2[it] }
    }

    // private val monomialListGenerator by lazy {
    //     MonomialListGenerator(this.degreeGroup, this.indeterminateListInternal)
    // }

    override fun listElements(degree: D): List<Monomial<D, I>> {
        return this.monomialListGeneratorAugmented.listMonomials(degree)
    }

    private val monomialListGeneratorAugmented: MonomialListGeneratorAugmented<D, I> by lazy {
        MonomialListGeneratorAugmented(this.degreeGroup, this.indeterminateListInternal)
    }

    override fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        return this.monomialListGeneratorAugmented.listDegreesForAugmentedDegree(augmentedDegree)
    }

    private fun separate(monomial: Monomial<D, I>, index: Int): MonomialSeparation<D, I>? {
        val separatedExponent = monomial.exponentList[index]
        if (separatedExponent == 0)
            return null
        // val remainingExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) 0 else exponent
        // }
        val remainingExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) 0 else monomial.exponentList[it]
        }
        val remainingMonomial = Monomial(this.degreeGroup, this.indeterminateListInternal, remainingExponentList)
        val separatedIndeterminate = this.indeterminateListInternal[index]
        // val separatedExponentList = monomial.exponentList.mapIndexed { i, exponent ->
        //     if (i == index) exponent else 0
        // }
        val separatedExponentList = IntArray(monomial.exponentList.size) {
            if (it == index) monomial.exponentList[it] else 0
        }
        val multipliedMonomialOrZero = this.multiply(
            Monomial(this.degreeGroup, this.indeterminateListInternal, separatedExponentList),
            remainingMonomial
        )
        val sign = when (multipliedMonomialOrZero) {
            is Signed -> multipliedMonomialOrZero.sign
            is Zero -> throw Exception("This can't happen!")
        }
        return MonomialSeparation(remainingMonomial, separatedIndeterminate, separatedExponent, sign, index)
    }

    internal fun allSeparations(monomial: Monomial<D, I>): List<MonomialSeparation<D, I>> {
        // TODO: List じゃなくて Iterator の方が良い？
        return this.indeterminateListInternal.indices.mapNotNull { i -> this.separate(monomial, i) }
    }

    override fun toString(): String {
        val indeterminateListString = this.indeterminateListInternal.joinToString(", ")
        return "FreeGMonoid($indeterminateListString)"
    }
}

internal data class MonomialSeparation<D : Degree, I : IndeterminateName>(
    val remainingMonomial: Monomial<D, I>,
    val separatedIndeterminate: Indeterminate<D, I>,
    val separatedExponent: Int,
    val sign: Sign,
    val index: Int,
) {
    init {
        if (separatedExponent <= 0)
            throw Exception("separatedExponent must be positive")
    }
}

public class FreeGMonoidMorphismByDegreeChange<DS : Degree, DT : Degree, I : IndeterminateName>(
    override val source: FreeGMonoid<DS, I>,
    override val degreeMorphism: AugmentedDegreeMorphism<DS, DT>,
) : GMonoidMorphismWithDegreeChange<DS, Monomial<DS, I>, DT, Monomial<DT, I>> {
    override val target: FreeGMonoid<DT, I> = run {
        val targetDegreeGroup = this.degreeMorphism.target
        val targetIndeterminateList = this.source.indeterminateList.map { indeterminate ->
            indeterminate.convertDegree(degreeMorphism)
        }
        FreeGMonoid(targetDegreeGroup, targetIndeterminateList)
    }

    override fun invoke(monoidElement: Monomial<DS, I>): Monomial<DT, I> {
        return this.target.fromExponentList(monoidElement.exponentList)
    }
}
