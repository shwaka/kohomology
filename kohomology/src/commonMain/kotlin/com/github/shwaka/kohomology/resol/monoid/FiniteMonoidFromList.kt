package com.github.shwaka.kohomology.resol.monoid

public data class SimpleFiniteMonoidElement<T>(val name: T) : FiniteMonoidElement {
    override fun toString(): String {
        return this.name.toString()
    }
}

public class FiniteMonoidFromList<T>(
    override val elements: List<SimpleFiniteMonoidElement<T>>,
    override val multiplicationTable: List<List<SimpleFiniteMonoidElement<T>>>,
) : FiniteMonoid<SimpleFiniteMonoidElement<T>> {
    override val context: FiniteMonoidContext<SimpleFiniteMonoidElement<T>> = FiniteMonoidContextImpl(this)
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
        FiniteMonoid.isCommutative(this.elements, this::multiply)
    }

    public companion object {
        public operator fun invoke(
            elements: List<String>,
            multiplicationTable: List<List<String>>,
        ): FiniteMonoidFromList<String> {
            return FiniteMonoidFromList(
                elements.map { SimpleFiniteMonoidElement(it) },
                multiplicationTable.map { row -> row.map { SimpleFiniteMonoidElement(it) } },
            )
        }
    }
}
