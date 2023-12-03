package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.vectsp.BasisName

public interface FiniteMonoidElement : BasisName

public interface FiniteMonoidContext<E : FiniteMonoidElement> {
    public val finiteMonoid: FiniteMonoid<E>

    public operator fun E.times(other: E): E {
        return this@FiniteMonoidContext.finiteMonoid.multiply(this, other)
    }
}

internal class FiniteMonoidContextImpl<E : FiniteMonoidElement>(
    override val finiteMonoid: FiniteMonoid<E>,
) : FiniteMonoidContext<E>

public interface FiniteMonoid<E : FiniteMonoidElement> {
    public val context: FiniteMonoidContext<E>
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
