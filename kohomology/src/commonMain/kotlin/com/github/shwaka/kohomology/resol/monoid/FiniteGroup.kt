package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause

public interface FiniteGroupContext<E : FiniteMonoidElement> : FiniteMonoidContext<E> {
    override val finiteMonoid: FiniteGroup<E>

    public fun E.inv(): E {
        return this@FiniteGroupContext.finiteMonoid.invert(this)
    }

    override fun E.pow(exponent: Int): E {
        val unit = this@FiniteGroupContext.finiteMonoid.unit
        return when {
            exponent == 0 -> unit
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else unit
                half * half * rem
            }
            exponent < 0 -> unit * this.pow(-exponent).inv()
            else -> throw Exception("This can't happen!")
        }
    }

    public companion object {
        public operator fun <E : FiniteMonoidElement> invoke(
            finiteMonoid: FiniteGroup<E>,
        ): FiniteGroupContext<E> {
            return FiniteGroupContextImpl(finiteMonoid)
        }
    }
}

private class FiniteGroupContextImpl<E : FiniteMonoidElement>(
    override val finiteMonoid: FiniteGroup<E>,
) : FiniteGroupContext<E>

public interface FiniteGroup<E : FiniteMonoidElement> : FiniteMonoid<E> {
    override val context: FiniteGroupContext<E>
    public fun invert(monoidElement: E): E

    public fun checkGroupAxioms() {
        val isGroup: BooleanWithCause = FiniteGroup.isGroup(this.elements, this::multiply, this::invert)
        if (isGroup is BooleanWithCause.False) {
            throw IllegalStateException(
                "Group axioms are not satisfied:\n" + isGroup.cause.joinToString("\n")
            )
        }
    }

    public companion object {
        public fun <E : FiniteMonoidElement> isInvertible(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
            invert: (monoidElement: E) -> E,
        ): BooleanWithCause {
            val cause = mutableListOf<String>()
            val unit = elements[0]
            for (element in elements) {
                val inverse = invert(element)
                multiply(element, inverse).let {
                    if (it != unit) {
                        cause.add("Not inverse: $element * $inverse must be $unit, but was $it")
                    }
                }
                if (inverse != element) {
                    multiply(inverse, element).let {
                        if (it != unit) {
                            cause.add("Not inverse: $inverse * $element must be $unit, but was $it")
                        }
                    }
                }
            }
            return BooleanWithCause.fromCause(cause)
        }

        public fun <E : FiniteMonoidElement> isGroup(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
            invert: (monoidElement: E) -> E,
        ): BooleanWithCause {
            val isMonoid: BooleanWithCause = FiniteMonoid.isMonoid(elements, multiply)
            val isInvertible: BooleanWithCause = FiniteGroup.isInvertible(elements, multiply, invert)
            return isMonoid * isInvertible
        }
    }
}
