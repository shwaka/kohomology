package com.github.shwaka.kohomology.bar

public data class SimpleFiniteMonoidElement<T>(val name: T) : FiniteMonoidElement {
    override fun toString(): String {
        return this.name.toString()
    }
}

public class FiniteMonoidFromList<T>(
    override val elements: List<SimpleFiniteMonoidElement<T>>,
    public val multiplicationTable: List<List<SimpleFiniteMonoidElement<T>>>,
) : FiniteMonoid<SimpleFiniteMonoidElement<T>> {
    override val unit: SimpleFiniteMonoidElement<T> = this.elements[0]

    override fun multiply(
        monoidElement1: SimpleFiniteMonoidElement<T>,
        monoidElement2: SimpleFiniteMonoidElement<T>
    ): SimpleFiniteMonoidElement<T> {
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

    override val isCommutative: Boolean by lazy {
        for (i in 0 until this.size) {
            for (j in (i + 1) until this.size) {
                val x = this.elements[i]
                val y = this.elements[j]
                if (this.multiply(x, y) != this.multiply(y, x)) {
                    return@lazy false
                }
            }
        }
        return@lazy true
    }
}
