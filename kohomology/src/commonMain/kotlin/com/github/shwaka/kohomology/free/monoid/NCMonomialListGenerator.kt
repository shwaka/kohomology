package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.util.shuffles

public interface NCMonomialListGenerator<D : Degree, I : IndeterminateName> {
    public val degreeGroup: AugmentedDegreeGroup<D>
    public val indeterminateList: List<Indeterminate<D, I>>
    public fun listNCMonomials(degree: D): List<NCMonomial<D, I>>
}

public class NCMonomialListGeneratorBasic<D : Degree, I : IndeterminateName> internal constructor(
    override val degreeGroup: AugmentedDegreeGroup<D>,
    private val indeterminateListInternal: IndeterminateList<D, I>,
) : NCMonomialListGenerator<D, I> {
    public constructor(
        degreeGroup: AugmentedDegreeGroup<D>,
        indeterminateList: List<Indeterminate<D, I>>,
    ) : this(degreeGroup, IndeterminateList.from(degreeGroup, indeterminateList))

    override val indeterminateList: List<Indeterminate<D, I>>
        get() = this.indeterminateListInternal.toList()

    private val cache: MutableMap<D, List<NCMonomial<D, I>>> = mutableMapOf()
    private val monomialListGenerator: MonomialListGenerator<D, I> =
        MonomialListGeneratorBasic(degreeGroup, indeterminateListInternal)
    private val calculator: PartitionCalculator<D> =
        PartitionCalculator(
            degreeGroup,
            indeterminateList.map { it.degree },
            indeterminateListInternal::isAllowedDegree,
            allowMultipleOfOdd = true,
        )

    override fun listNCMonomials(degree: D): List<NCMonomial<D, I>> {
        return this.cache.getOrPut(degree) {
            this.calculator.getList(degree).flatMap { exponentList ->
                shuffles(exponentList.toList()).map { shuffle: List<Int> ->
                    val word = shuffle.map { i -> this.indeterminateListInternal[i] }
                    NCMonomial(this.degreeGroup, this.indeterminateListInternal, word)
                }
            }
        }
    }
}
