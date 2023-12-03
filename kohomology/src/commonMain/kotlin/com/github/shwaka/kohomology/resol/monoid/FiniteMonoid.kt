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

    public fun checkUnitality() {
        val elements = this.elements
        val unit = this.unit
        this.context.run {
            for (element in elements) {
                check(element * unit == element) {
                    "Non-unital: $element * $unit must be $element, but was ${element * unit}"
                }
                check(unit * element == element) {
                    "Non-unital: $unit * $element must be $element, but was ${unit * element}"
                }
            }
        }
    }

    public fun checkAssociativity() {
        val elements = this.elements
        this.context.run {
            for (a in elements) {
                for (b in elements) {
                    for (c in elements) {
                        check((a * b) * c == a * (b * c)) {
                            "Non-associative: " +
                                "($a * $b) * $c = ${(a * b) * c} and " +
                                "$a * ($b * $c) = ${a * (b * c)}; " +
                                "these must be same, but was different"
                        }
                    }
                }
            }
        }
    }

    public fun checkMonoidAxioms() {
        this.checkUnitality()
        this.checkAssociativity()
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
    }
}
