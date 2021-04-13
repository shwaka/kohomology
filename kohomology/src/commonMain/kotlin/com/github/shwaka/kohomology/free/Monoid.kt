package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName

interface MonoidElement : BasisName {
    val degree: Degree
}

sealed class MaybeZero<T>
class Zero<T> : MaybeZero<T>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}
data class NonZero<T>(val value: T) : MaybeZero<T>()

interface Monoid<E : MonoidElement> {
    val unit: E
    fun multiply(monoidElement1: E, monoidElement2: E): MaybeZero<Pair<E, Sign>>
    fun listAll(degree: Degree): List<E>
}

data class SimpleMonoidElement<T>(val name: T, override val degree: Degree) : MonoidElement {
    override fun toString(): String {
        return this.name.toString()
    }
}

class MonoidFromList<T>(
    val elements: List<SimpleMonoidElement<T>>,
    val multiplicationTable: List<List<MaybeZero<Pair<SimpleMonoidElement<T>, Sign>>>>
) : Monoid<SimpleMonoidElement<T>> {
    init {
        if (this.elements.isEmpty())
            throw IllegalArgumentException("'elements' must be non-empty list")
        if (this.elements[0].degree != 0)
            throw IllegalArgumentException("The first element of the list 'elements' should be the unit (degree 0)")
    }

    override val unit = this.elements[0]

    override fun multiply(
        monoidElement1: SimpleMonoidElement<T>,
        monoidElement2: SimpleMonoidElement<T>
    ): MaybeZero<Pair<SimpleMonoidElement<T>, Sign>> {
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

    override fun listAll(degree: Degree): List<SimpleMonoidElement<T>> {
        return this.elements.filter { it.degree == degree }
    }
}
