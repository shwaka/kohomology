package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.util.Sign

public class NCFreeGMonoid<D : Degree, I : IndeterminateName>(
    override val degreeGroup: AugmentedDegreeGroup<D>,
    indeterminateList: List<Indeterminate<D, I>>,
) : GMonoid<D, NCMonomial<D, I>> {
    private val indeterminateListInternal = IndeterminateList.from(degreeGroup, indeterminateList)
    public val indeterminateList: List<Indeterminate<D, I>> by lazy {
        this.indeterminateListInternal.toList()
    }

    public companion object {
        public operator fun <I : IndeterminateName> invoke(
            indeterminateList: List<Indeterminate<IntDegree, I>>
        ): NCFreeGMonoid<IntDegree, I> {
            return NCFreeGMonoid(IntDegreeGroup, indeterminateList)
        }
    }

    override val unit: NCMonomial<D, I> = NCMonomial.unit(this.degreeGroup, this.indeterminateListInternal)
    override val isCommutative: Boolean
        get() = this.indeterminateListInternal.isEmpty()
    override val boundedness: Boundedness
        get() = if (this.indeterminateListInternal.isEmpty()) {
            Boundedness(upperBound = 0, lowerBound = 0)
        } else {
            Boundedness(upperBound = null, lowerBound = null)
        }

    override fun multiply(
        monoidElement1: NCMonomial<D, I>,
        monoidElement2: NCMonomial<D, I>
    ): SignedOrZero<NCMonomial<D, I>> {
        val word = monoidElement1.word + monoidElement2.word
        val ncMonomial = NCMonomial(this.degreeGroup, this.indeterminateListInternal, word)
        return Signed(ncMonomial, Sign.PLUS)
    }

    override fun listElements(degree: D): List<NCMonomial<D, I>> {
        TODO("Not yet implemented")
    }
}
