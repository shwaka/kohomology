package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import com.github.shwaka.kohomology.vectsp.BasisName

public interface FiniteMonoidElement : BasisName

public interface FiniteMonoidContext<E : FiniteMonoidElement> {
    public val finiteMonoid: FiniteMonoid<E>

    public operator fun E.times(other: E): E {
        return this@FiniteMonoidContext.finiteMonoid.multiply(this, other)
    }

    public fun E.pow(exponent: Int): E {
        val unit = this@FiniteMonoidContext.finiteMonoid.unit
        return when {
            exponent == 0 -> unit
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else unit
                half * half * rem
            }
            else -> throw Exception("Negative exponent is not allowed in FiniteMonoid")
        }
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

    public fun checkMonoidAxioms() {
        val isMonoid: BooleanWithCause = FiniteMonoid.isMonoid(this.elements, this::multiply)
        if (isMonoid is BooleanWithCause.False) {
            throw IllegalStateException(
                "Monoid axioms are not satisfied:\n" + isMonoid.cause.joinToString("\n")
            )
        }
    }

    public companion object {
        internal fun <E : FiniteMonoidElement> getMultiplicationTable(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): List<List<E>> {
            return elements.map { monoidElement1 ->
                elements.map { monoidElement2 ->
                    multiply(monoidElement1, monoidElement2)
                }
            }
        }

        internal fun <E : FiniteMonoidElement> isCommutative(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): Boolean {
            for (i in elements.indices) {
                for (j in (i + 1) until elements.size) {
                    val x = elements[i]
                    val y = elements[j]
                    if (multiply(x, y) != multiply(y, x)) {
                        return false
                    }
                }
            }
            return true
        }

        public fun <E : FiniteMonoidElement> isUnital(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): BooleanWithCause {
            val cause = mutableListOf<String>()
            val unit = elements[0]
            for (element in elements) {
                multiply(element, unit).let {
                    if (it != element) {
                        cause.add("Non-unital: $element * $unit must be $element, but was $it")
                    }
                }
                multiply(unit, element).let {
                    if (it != element) {
                        cause.add("Non-unital: $unit * $element must be $element, but was $it")
                    }
                }
            }
            return BooleanWithCause.fromCause(cause)
        }

        public fun <E : FiniteMonoidElement> isAssociative(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): BooleanWithCause {
            val cause = mutableListOf<String>()
            for (a in elements) {
                for (b in elements) {
                    for (c in elements) {
                        val leftAssoc = multiply(multiply(a, b), c)
                        val rightAssoc = multiply(a, multiply(b, c))
                        if (leftAssoc != rightAssoc) {
                            cause.add(
                                "Non-associative: " +
                                    "($a * $b) * $c = $leftAssoc and " +
                                    "$a * ($b * $c) = $rightAssoc; " +
                                    "these must be same, but was different"
                            )
                        }
                    }
                }
            }
            return BooleanWithCause.fromCause(cause)
        }

        public fun <E : FiniteMonoidElement> isMonoid(
            elements: List<E>,
            multiply: (monoidElement1: E, monoidElement2: E) -> E,
        ): BooleanWithCause {
            val isUnital: BooleanWithCause = FiniteMonoid.isUnital(elements, multiply)
            val isAssociative: BooleanWithCause = FiniteMonoid.isAssociative(elements, multiply)
            return isUnital * isAssociative
        }
    }
}
