package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup

public interface MonomialListGenerator<D : Degree, I : IndeterminateName> {
    public val degreeGroup: AugmentedDegreeGroup<D>
    public val indeterminateList: List<Indeterminate<D, I>>
    public fun listMonomials(degree: D): List<Monomial<D, I>>
}

public class MonomialListGeneratorBasic<D : Degree, I : IndeterminateName> internal constructor(
    override val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateListInternal: IndeterminateList<D, I>,
    // val unit: Monomial<D, I>,
) : MonomialListGenerator<D, I> {
    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList))

    override val indeterminateList: List<Indeterminate<D, I>>
        get() = this.indeterminateListInternal.toList()

    // (degree: D, index: Int) -> List<Monomial<D, I>>
    private val cache: MutableMap<Pair<D, Int>, List<Monomial<D, I>>> = mutableMapOf()

    private val unit: Monomial<D, I> = Monomial.unit(this.degreeGroup, this.indeterminateListInternal)

    private val calculator: PartitionCalculator<D> =
        PartitionCalculator(
            degreeGroup,
            indeterminateList.map { it.degree },
            indeterminateListInternal::isAllowedDegree,
        )

    override fun listMonomials(degree: D): List<Monomial<D, I>> {
        return this.calculator.getList(degree).map { exponentList ->
            Monomial(this.degreeGroup, this.indeterminateListInternal, exponentList)
        }
    }

    private fun listMonomialsInternal(degree: D, index: Int): List<Monomial<D, I>> {
        if (index < 0 || index > this.indeterminateListInternal.size)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.indeterminateListInternal.size) {
            return if (degree.isZero())
                listOf(this.unit)
            else
                emptyList()
        }
        val cacheKey = Pair(degree, index)
        return this.cache.getOrPut(cacheKey) {
            // Since 0 <= index < this.indeterminateList.size,
            // we have 0 < this.indeterminateList.size
            val newDegree = this.degreeGroup.context.run { degree - this@MonomialListGeneratorBasic.indeterminateListInternal[index].degree }
            val listWithNonZeroAtIndex = if (this.indeterminateListInternal.isAllowedDegree(newDegree)) {
                this.listMonomialsInternal(newDegree, index)
                    .mapNotNull { monomial -> monomial.increaseExponentAtIndex(index) }
            } else emptyList()
            val listWithZeroAtIndex = this.listMonomialsInternal(degree, index + 1)
            listWithNonZeroAtIndex + listWithZeroAtIndex
        }
    }
}

public class MonomialListGeneratorAugmented<D : Degree, I : IndeterminateName> internal constructor(
    override val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateListInternal: IndeterminateList<D, I>,
) : MonomialListGenerator<D, I> {
    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList))

    override val indeterminateList: List<Indeterminate<D, I>>
        get() = this.indeterminateListInternal.toList()

    private val generatorForAugmentedDegree: MonomialListGenerator<IntDegree, I> = run {
        val indeterminateRawList: List<Indeterminate<IntDegree, I>> = indeterminateListInternal.map { indeterminate ->
            Indeterminate(indeterminate.name, this.degreeGroup.augmentation(indeterminate.degree))
        }
        val indeterminateListWithAugDeg = IndeterminateList.from(IntDegreeGroup, indeterminateRawList)
        MonomialListGeneratorBasic(IntDegreeGroup, indeterminateListWithAugDeg)
    }

    override fun listMonomials(degree: D): List<Monomial<D, I>> {
        val augmentedDegree = this.degreeGroup.augmentation(degree)
        return this.listMonomialsForAugmentedDegree(augmentedDegree).filter {
            it.degree == degree
        }
    }

    public fun listDegreesForAugmentedDegree(augmentedDegree: Int): List<D> {
        return this.listMonomialsForAugmentedDegree(augmentedDegree).map { it.degree }.distinct()
    }

    private fun listMonomialsForAugmentedDegree(augmentedDegree: Int): List<Monomial<D, I>> {
        val elementListWithIntDegree: List<Monomial<IntDegree, I>> =
            this.generatorForAugmentedDegree.listMonomials(IntDegree(augmentedDegree))
        return elementListWithIntDegree.map { elementWithAugDeg ->
            Monomial(this.degreeGroup, this.indeterminateListInternal, elementWithAugDeg.exponentList)
        }
    }
}
