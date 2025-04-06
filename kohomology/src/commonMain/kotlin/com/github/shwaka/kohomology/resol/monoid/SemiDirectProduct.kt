package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.directProductOf

public data class SemiDirectProductElement<EA : FiniteMonoidElement, E : FiniteMonoidElement>(
    val sourceElement: EA,
    val targetElement: E,
) : FiniteMonoidElement {
    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
    override fun toString(printConfig: PrintConfig): String {
        val sourceString = this.sourceElement.toString(printConfig)
        val targetString = this.targetElement.toString(printConfig)
        return "($sourceString, $targetString)"
    }

    public companion object {
        public fun <EA : FiniteMonoidElement, E : FiniteMonoidElement> getAll(
            action: FiniteMonoidAction<EA, E>
        ): List<SemiDirectProductElement<EA, E>> {
            return directProductOf(action.source.elements, action.target.elements).map {
                SemiDirectProductElement(it.first, it.second)
            }
        }
    }
}

public class SemiDirectProduct<EA : FiniteMonoidElement, E : FiniteMonoidElement>(
    public val action: FiniteMonoidAction<EA, E>,
) : FiniteMonoid<SemiDirectProductElement<EA, E>> {
    override val context: FiniteMonoidContext<SemiDirectProductElement<EA, E>> = FiniteMonoidContext(this)
    override val unit: SemiDirectProductElement<EA, E> =
        SemiDirectProductElement(action.source.unit, action.target.unit)
    override val elements: List<SemiDirectProductElement<EA, E>> = SemiDirectProductElement.getAll(action)
    override val isCommutative: Boolean by lazy {
        FiniteMonoid.isCommutative(elements, ::multiply)
    }

    override fun multiply(
        monoidElement1: SemiDirectProductElement<EA, E>,
        monoidElement2: SemiDirectProductElement<EA, E>
    ): SemiDirectProductElement<EA, E> {
        TODO("Not yet implemented")
    }

    override val multiplicationTable: List<List<SemiDirectProductElement<EA, E>>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }

    override fun toString(printConfig: PrintConfig): String {
        val sourceString = this.action.source.toString(printConfig)
        val targetString = this.action.target.toString(printConfig)
        val ltimes = when (printConfig.printType) {
            PrintType.TEX -> "\\ltimes"
            else -> "⋉"
        }
        return "$sourceString $ltimes $targetString"
    }
}
