package com.github.shwaka.kohomology.profile.executable
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.MonomialListGenerator
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName

private typealias GetMonomialListGenerator<D, I> =
        (AugmentedDegreeGroup<D>, List<Indeterminate<D, I>>) -> MonomialListGenerator<D, I>

class ComputeMonomialList(
    val n: Int,
    getMonomialListGenerator: GetMonomialListGenerator<MultiDegree, StringIndeterminateName>,
) : Executable() {
    override val description: String = "Compute monomial list"

    private val degreeGroup: MultiDegreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("N", 1),
            DegreeIndeterminate("M", 1),
        )
    )
    private val indeterminateList: List<Indeterminate<MultiDegree, StringIndeterminateName>> = listOf(
        Indeterminate("x", degreeGroup.fromList(listOf(1, 0, 0))),
        Indeterminate("y", degreeGroup.fromList(listOf(0, 2, 0))),
        Indeterminate("z", degreeGroup.fromList(listOf(0, 0, 2))),
    )
    private val monomialListGenerator: MonomialListGenerator<MultiDegree, StringIndeterminateName> =
        getMonomialListGenerator(this.degreeGroup, this.indeterminateList)

    override fun mainFun(): String {
        val degree = this.degreeGroup.fromList(listOf(this.n, 2 * this.n, 2 * this.n))
        val monomials = this.monomialListGenerator.listMonomials(degree)
        return monomials.toString()
    }
}
