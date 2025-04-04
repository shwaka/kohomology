package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import kotlin.math.min

public data class TruncatedAdditionMonoidElement(val value: Int, val maxValue: Int) : FiniteMonoidElement {
    init {
        require(this.maxValue >= 0)
        require(this.value >= 0)
        require(this.value <= this.maxValue)
    }

    override fun toString(): String {
        return this.value.toString()
    }
}

public class TruncatedAdditionMonoid(public val maxValue: Int) : FiniteMonoid<TruncatedAdditionMonoidElement> {
    init {
        require(this.maxValue >= 0)
    }

    override val context: FiniteMonoidContext<TruncatedAdditionMonoidElement> = FiniteMonoidContext(this)
    override val unit: TruncatedAdditionMonoidElement = TruncatedAdditionMonoidElement(0, maxValue = maxValue)
    override val elements: List<TruncatedAdditionMonoidElement> by lazy {
        (0..this.maxValue).map { i -> TruncatedAdditionMonoidElement(i, maxValue = maxValue)}
    }
    override val isCommutative: Boolean = true

    override fun multiply(
        monoidElement1: TruncatedAdditionMonoidElement,
        monoidElement2: TruncatedAdditionMonoidElement
    ): TruncatedAdditionMonoidElement {
        val value = min(monoidElement1.value + monoidElement2.value, this.maxValue)
        return TruncatedAdditionMonoidElement(value, this.maxValue)
    }

    override val multiplicationTable: List<List<TruncatedAdditionMonoidElement>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.TEX -> "TA_{${this.maxValue}}"
            else -> "TA_${this.maxValue}"
        }
    }
}
