package com.github.shwaka.kohomology.resol.monoid

public interface FiniteGroup<E : FiniteMonoidElement> : FiniteMonoid<E> {
    public fun invert(monoidElement: E): E
}
