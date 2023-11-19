package com.github.shwaka.kohomology.bar

public interface FiniteMonoidElement

public interface FiniteMonoid<E : FiniteMonoidElement> {
    public val unit: E
    public val elements: List<E>
    public fun multiply(monoidElement1: E, monoidElement2: E): E

    public val size: Int
        get() = elements.size
}

public interface FiniteGroup<E : FiniteMonoidElement> : FiniteMonoid<E> {
    public fun invert(monoidElement: E): E
}
