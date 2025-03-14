package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree

internal interface MonomialListGenerator<D : Degree, I : IndeterminateName> {
    val degreeGroup: AugmentedDegreeGroup<D>
    val indeterminateList: IndeterminateList<D, I>
    fun listMonomials(degree: D): List<Monomial<D, I>>
}

internal class MonomialListGeneratorBasic<D : Degree, I : IndeterminateName>(
    override val degreeGroup: AugmentedDegreeGroup<D>,
    override val indeterminateList: IndeterminateList<D, I>,
    // val unit: Monomial<D, I>,
) : MonomialListGenerator<D, I> {
    // (degree: D, index: Int) -> List<Monomial<D, I>>
    private val cache: MutableMap<Pair<D, Int>, List<Monomial<D, I>>> = mutableMapOf()

    private val unit: Monomial<D, I> = Monomial.unit(this.degreeGroup, this.indeterminateList)

    override fun listMonomials(degree: D): List<Monomial<D, I>> {
        if (!this.indeterminateList.isAllowedDegree(degree))
            return emptyList()
        return this.listMonomialsInternal(degree, 0)
    }

    private fun listMonomialsInternal(degree: D, index: Int): List<Monomial<D, I>> {
        if (index < 0 || index > this.indeterminateList.size)
            throw Exception("This can't happen! (illegal index: $index)")
        if (index == this.indeterminateList.size) {
            return if (degree.isZero())
                listOf(this.unit)
            else
                emptyList()
        }
        val cacheKey = Pair(degree, index)
        return this.cache.getOrPut(cacheKey) {
            // Since 0 <= index < this.indeterminateList.size,
            // we have 0 < this.indeterminateList.size
            val newDegree = this.degreeGroup.context.run { degree - this@MonomialListGeneratorBasic.indeterminateList[index].degree }
            val listWithNonZeroAtIndex = if (this.indeterminateList.isAllowedDegree(newDegree)) {
                this.listMonomialsInternal(newDegree, index)
                    .mapNotNull { monomial -> monomial.increaseExponentAtIndex(index) }
            } else emptyList()
            val listWithZeroAtIndex = this.listMonomialsInternal(degree, index + 1)
            listWithNonZeroAtIndex + listWithZeroAtIndex
        }
    }
}
