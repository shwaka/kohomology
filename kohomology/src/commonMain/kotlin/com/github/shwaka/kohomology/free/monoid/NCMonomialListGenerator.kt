package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.util.shuffles

internal class NCMonomialListGenerator<D : Degree, I : IndeterminateName>(
    val degreeGroup: AugmentedDegreeGroup<D>,
    val indeterminateList: IndeterminateList<D, I>,
) {
    private val cache: MutableMap<D, List<NCMonomial<D, I>>> = mutableMapOf()
    private val monomialListGenerator: MonomialListGenerator<D, I> =
        MonomialListGenerator(degreeGroup, indeterminateList)

    fun listNCMonomials(degree: D): List<NCMonomial<D, I>> {
        return this.cache.getOrPut(degree) {
            val monomials = this.monomialListGenerator.listMonomials(degree)
            monomials.flatMap { monomial ->
                shuffles(monomial.exponentList.toList()).map { shuffle: List<Int> ->
                    val word = shuffle.map { i -> this.indeterminateList[i] }
                    NCMonomial(this.degreeGroup, this.indeterminateList, word)
                }
            }
        }
    }
}
