package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public class OppositeFiniteMonoid<E : FiniteMonoidElement>(
    public val originalMonoid: FiniteMonoid<E>,
) : FiniteMonoid<E> {
    override val context: FiniteMonoidContext<E> = FiniteMonoidContext(this)
    override val unit: E = originalMonoid.unit
    override val elements: List<E> = originalMonoid.elements
    override val isCommutative: Boolean
        get() = originalMonoid.isCommutative

    override fun multiply(monoidElement1: E, monoidElement2: E): E {
        return this.originalMonoid.multiply(monoidElement2, monoidElement1)
    }

    override val multiplicationTable: List<List<E>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun toString(printConfig: PrintConfig): String {
        val originalString = this.originalMonoid.toString(printConfig)
        return when (printConfig.printType) {
            PrintType.TEX -> "{$originalString}^{\\mathrm{op}}"
            else -> "$originalString^op"
        }
    }
}
