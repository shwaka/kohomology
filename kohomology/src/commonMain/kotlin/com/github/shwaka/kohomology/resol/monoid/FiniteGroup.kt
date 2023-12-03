package com.github.shwaka.kohomology.resol.monoid

public interface FiniteGroupContext<E : FiniteMonoidElement> : FiniteMonoidContext<E> {
    override val finiteMonoid: FiniteGroup<E>

    public fun E.inv(): E {
        return this@FiniteGroupContext.finiteMonoid.invert(this)
    }
}

internal class FiniteGroupContextImpl<E : FiniteMonoidElement>(
    override val finiteMonoid: FiniteGroup<E>,
) : FiniteGroupContext<E>

public interface FiniteGroup<E : FiniteMonoidElement> : FiniteMonoid<E> {
    override val context: FiniteGroupContext<E>
    public fun invert(monoidElement: E): E
}
