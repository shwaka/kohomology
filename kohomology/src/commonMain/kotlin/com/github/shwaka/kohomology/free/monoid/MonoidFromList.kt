package com.github.shwaka.kohomology.free.monoid

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree

public data class SimpleMonoidElement<T, D : Degree>(val name: T, override val degree: D) : MonoidElement<D> {
    override fun toString(): String {
        return this.name.toString()
    }
    public companion object {
        public operator fun <T> invoke(name: T, degree: Int): SimpleMonoidElement<T, IntDegree> {
            return SimpleMonoidElement(name, IntDegree(degree))
        }
    }
}

public class MonoidFromList<T, D : Degree>(
    public val elements: List<SimpleMonoidElement<T, D>>,
    override val degreeGroup: DegreeGroup<D>,
    public val multiplicationTable: List<List<SignedOrZero<SimpleMonoidElement<T, D>>>>,
    override val isCommutative: Boolean,
) : Monoid<D, SimpleMonoidElement<T, D>> {
    init {
        if (this.elements.isEmpty())
            throw IllegalArgumentException("'elements' must be non-empty list")
        if (this.elements[0].degree.isNotZero())
            throw IllegalArgumentException("The first element of the list 'elements' should be the unit (degree 0)")
    }

    override val unit: SimpleMonoidElement<T, D> = this.elements[0]

    override fun multiply(
        monoidElement1: SimpleMonoidElement<T, D>,
        monoidElement2: SimpleMonoidElement<T, D>
    ): SignedOrZero<SimpleMonoidElement<T, D>> {
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

    override fun listElements(degree: D): List<SimpleMonoidElement<T, D>> {
        return this.elements.filter { it.degree == degree }
    }
}
