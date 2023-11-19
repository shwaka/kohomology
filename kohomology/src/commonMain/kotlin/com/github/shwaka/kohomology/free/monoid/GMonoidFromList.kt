package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.Boundedness
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree

public data class SimpleGMonoidElement<T, D : Degree>(val name: T, override val degree: D) : GMonoidElement<D> {
    override fun toString(): String {
        return this.name.toString()
    }
    public companion object {
        public operator fun <T> invoke(name: T, degree: Int): SimpleGMonoidElement<T, IntDegree> {
            return SimpleGMonoidElement(name, IntDegree(degree))
        }
    }
}

public class GMonoidFromList<T, D : Degree>(
    public val elements: List<SimpleGMonoidElement<T, D>>,
    override val degreeGroup: DegreeGroup<D>,
    public val multiplicationTable: List<List<SignedOrZero<SimpleGMonoidElement<T, D>>>>,
    override val isCommutative: Boolean,
) : GMonoid<D, SimpleGMonoidElement<T, D>> {
    init {
        if (this.elements.isEmpty())
            throw IllegalArgumentException("'elements' must be non-empty list")
        if (this.elements[0].degree.isNotZero())
            throw IllegalArgumentException("The first element of the list 'elements' should be the unit (degree 0)")
    }

    override val unit: SimpleGMonoidElement<T, D> = this.elements[0]
    override val boundedness: Boundedness by lazy {
        Boundedness(
            upperBound = GMonoidFromList.getUpperBound(this.elements, this.degreeGroup),
            lowerBound = GMonoidFromList.getLowerBound(this.elements, this.degreeGroup),
        )
    }

    override fun multiply(
        monoidElement1: SimpleGMonoidElement<T, D>,
        monoidElement2: SimpleGMonoidElement<T, D>
    ): SignedOrZero<SimpleGMonoidElement<T, D>> {
        val index1: Int = this.elements.indexOf(monoidElement1).also {
            if (it == -1)
                throw NoSuchElementException("$monoidElement1 is not found in the list 'elements'")
        }
        val index2: Int = this.elements.indexOf(monoidElement2).also {
            if (it == -1)
                throw NoSuchElementException("$monoidElement2 is not found in the list 'elements'")
        }
        return this.multiplicationTable[index1][index2]
    }

    override fun listElements(degree: D): List<SimpleGMonoidElement<T, D>> {
        return this.elements.filter { it.degree == degree }
    }

    private companion object {
        private const val boundForEmpty = 0
        private fun <T, D : Degree> getBound(
            elements: List<SimpleGMonoidElement<T, D>>,
            degreeGroup: DegreeGroup<D>,
            maxOrMin: (degrees: List<Int>) -> Int,
        ): Int? {
            if (degreeGroup !is AugmentedDegreeGroup<D>) {
                return null
            }
            if (elements.isEmpty()) {
                return this.boundForEmpty
            }
            val degrees: List<Int> = elements.map { element ->
                degreeGroup.augmentation(element.degree)
            }
            return maxOrMin(degrees)
        }
        private fun <T, D : Degree> getUpperBound(
            elements: List<SimpleGMonoidElement<T, D>>,
            degreeGroup: DegreeGroup<D>,
        ): Int? {
            return this.getBound(elements, degreeGroup) { degrees -> degrees.max() }
        }
        private fun <T, D : Degree> getLowerBound(
            elements: List<SimpleGMonoidElement<T, D>>,
            degreeGroup: DegreeGroup<D>,
        ): Int? {
            return this.getBound(elements, degreeGroup) { degrees -> degrees.min() }
        }
    }
}
