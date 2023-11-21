package com.github.shwaka.kohomology.resol.monoid

public interface FiniteMonoidElement

public interface FiniteMonoid<E : FiniteMonoidElement> {
    public val unit: E
    public val elements: List<E>
    public val isCommutative: Boolean
    public fun multiply(monoidElement1: E, monoidElement2: E): E
    public val multiplicationTable: List<List<E>>

    public val size: Int
        get() = elements.size

    public companion object {
        public fun <E : FiniteMonoidElement> getMultiplicationTable(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): List<List<E>> {
            return elements.map { monoidElement1 ->
                elements.map { monoidElement2 ->
                    multiply(monoidElement1, monoidElement2)
                }
            }
        }
    }
}

public interface FiniteGroup<E : FiniteMonoidElement> : FiniteMonoid<E> {
    public fun invert(monoidElement: E): E
}
