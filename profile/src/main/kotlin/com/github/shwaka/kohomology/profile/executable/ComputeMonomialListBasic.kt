@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.github.shwaka.kohomology.profile.executable
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateList
import com.github.shwaka.kohomology.free.monoid.MonomialListGenerator
import com.github.shwaka.kohomology.free.monoid.MonomialListGeneratorAugmented
import com.github.shwaka.kohomology.free.monoid.MonomialListGeneratorBasic
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName

class ComputeMonomialListBasic(val n: Int) : Executable() {
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
        MonomialListGeneratorBasic(
            this.degreeGroup,
            IndeterminateList.from(this.degreeGroup, this.indeterminateList),
        )

    override fun mainFun(): String {
        val degree = this.degreeGroup.fromList(listOf(this.n, 2 * this.n, 2 * this.n))
        val monomials = this.monomialListGenerator.listMonomials(degree)
        return monomials.toString()
    }
}

// TODO: duplicated code
class ComputeMonomialListAugmented(val n: Int) : Executable() {
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
        MonomialListGeneratorAugmented(
            this.degreeGroup,
            IndeterminateList.from(this.degreeGroup, this.indeterminateList),
        )

    override fun mainFun(): String {
        val degree = this.degreeGroup.fromList(listOf(this.n, 2 * this.n, 2 * this.n))
        val monomials = this.monomialListGenerator.listMonomials(degree)
        return monomials.toString()
    }
}
