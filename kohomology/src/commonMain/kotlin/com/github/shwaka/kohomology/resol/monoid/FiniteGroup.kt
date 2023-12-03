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

    public fun checkInverse() {
        val elements = this.elements
        val unit = this.unit
        this.context.run {
            for (element in elements) {
                val inverse = element.inv()
                check(element * inverse == unit) {
                    "Not inverse: $element * $inverse must be $unit, but was ${element * inverse}"
                }
                check(inverse * element == unit) {
                    "Not inverse: $inverse * $element must be $unit, but was ${inverse * element}"
                }
            }
        }
    }

    public fun checkGroupAxioms() {
        this.checkMonoidAxioms()
        this.checkInverse()
    }
}
