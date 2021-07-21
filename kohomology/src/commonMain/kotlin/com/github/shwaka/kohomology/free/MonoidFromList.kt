package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.util.Sign

data class SimpleMonoidElement<T, D : Degree>(val name: T, override val degree: D) : MonoidElement<D> {
    override fun toString(): String {
        return this.name.toString()
    }
    companion object {
        operator fun <T> invoke(name: T, degree: Int): SimpleMonoidElement<T, IntDegree> {
            return SimpleMonoidElement(name, IntDegree(degree))
        }
    }
}

class MonoidFromList<T, D : Degree>(
    val elements: List<SimpleMonoidElement<T, D>>,
    override val degreeGroup: DegreeGroup<D>,
    val multiplicationTable: List<List<MaybeZero<Pair<SimpleMonoidElement<T, D>, Sign>>>>
) : Monoid<D, SimpleMonoidElement<T, D>> {
    init {
        if (this.elements.isEmpty())
            throw IllegalArgumentException("'elements' must be non-empty list")
        if (this.elements[0].degree.isNotZero())
            throw IllegalArgumentException("The first element of the list 'elements' should be the unit (degree 0)")
    }

    override val unit = this.elements[0]

    override fun multiply(
        monoidElement1: SimpleMonoidElement<T, D>,
        monoidElement2: SimpleMonoidElement<T, D>
    ): MaybeZero<Pair<SimpleMonoidElement<T, D>, Sign>> {
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
