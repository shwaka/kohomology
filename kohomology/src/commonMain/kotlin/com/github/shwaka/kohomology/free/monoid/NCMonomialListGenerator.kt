package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree

internal class NCMonomialListGenerator<D : Degree, I : IndeterminateName>(
    val degreeGroup: AugmentedDegreeGroup<D>,
    val indeterminateList: IndeterminateList<D, I>,
) {
    private val cache: MutableMap<Pair<D, Int>, List<NCMonomial<D, I>>> = mutableMapOf()

    private val unit: NCMonomial<D, I> = NCMonomial.unit(this.degreeGroup, this.indeterminateList)
}
