package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public class PermutedFiniteMonoid<E : FiniteMonoidElement>(
    public val originalMonoid: FiniteMonoid<E>,
    permutedElements: List<E>,
) : FiniteMonoid<E> {
    init {
        require(permutedElements.size == originalMonoid.size) {
            "permutedElements must have the same size as the original monoid"
        }
        require(permutedElements.first() == originalMonoid.unit) {
            "The first element of permutedElements must be the unit, but was ${permutedElements.first()}"
        }
        require(permutedElements.all { originalMonoid.elements.contains(it) }) {
            val notContained = permutedElements.filterNot { originalMonoid.elements.contains(it) }
            "All elements of permutedElements must be contained in the original monoid, " +
                "but the following elements are not contained: $notContained"
        }
    }

    override val context: FiniteMonoidContext<E> = FiniteMonoidContextImpl(this)
    override val unit: E = originalMonoid.unit
    override val elements: List<E> = permutedElements
    override val isCommutative: Boolean = originalMonoid.isCommutative
    override fun multiply(monoidElement1: E, monoidElement2: E): E {
        return this.originalMonoid.multiply(monoidElement1, monoidElement2)
    }
    override val multiplicationTable: List<List<E>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    public val mapFromOriginalMonoid: FiniteMonoidMap<E, E> by lazy {
        FiniteMonoidMap(
            source = this.originalMonoid,
            target = this,
            values = this.originalMonoid.elements,
        )
    }

    public val mapToOriginalMonoid: FiniteMonoidMap<E, E> by lazy {
        FiniteMonoidMap(
            source = this,
            target = this.originalMonoid,
            values = this.elements,
        )
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.TEX -> "{${this.originalMonoid.toString(printConfig)}}_{\\mathrm{perm}}"
            else -> "Perm(${this.originalMonoid.toString(printConfig)})"
        }
    }
}
