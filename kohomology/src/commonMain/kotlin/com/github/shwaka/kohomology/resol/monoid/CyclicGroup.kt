package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public data class CyclicGroupElement(val value: Int, val order: Int) : FiniteMonoidElement {
    init {
        require((0 <= this.value) && (this.value < this.order)) {
            "Invalid argument: CyclicGroupElement(value=$value, order=$order) is not allowed. " +
                "The argument value must satisfy 0 <= value < order"
        }
    }

    override fun toString(printConfig: PrintConfig): String {
        val t = "t"
        return when (printConfig.printType) {
            PrintType.TEX -> "$t^{${this.value}}"
            else -> "$t^${this.value}"
        }
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
}

public class CyclicGroup(public val order: Int) : FiniteGroup<CyclicGroupElement> {
    init {
        require(this.order > 0)
    }

    override val context: FiniteGroupContext<CyclicGroupElement> = FiniteGroupContext(this)
    override val unit: CyclicGroupElement = CyclicGroupElement(0, order)
    override val elements: List<CyclicGroupElement> by lazy {
        (0 until this.order).map { i -> CyclicGroupElement(i, this.order) }
    }
    override val isCommutative: Boolean = true

    override fun multiply(monoidElement1: CyclicGroupElement, monoidElement2: CyclicGroupElement): CyclicGroupElement {
        require(monoidElement1.order == monoidElement2.order)
        val value = (monoidElement1.value + monoidElement2.value).mod(this.order)
        return CyclicGroupElement(value, this.order)
    }

    override val multiplicationTable: List<List<CyclicGroupElement>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun invert(monoidElement: CyclicGroupElement): CyclicGroupElement {
        return if (monoidElement.value == 0) {
            CyclicGroupElement(0, this.order)
        } else {
            val value = (this.order - monoidElement.value)
            CyclicGroupElement(value, this.order)
        }
    }

    public fun <E : FiniteMonoidElement> getMonoidMap(
        target: FiniteMonoid<E>,
        valueOfGenerator: E,
    ): FiniteMonoidMap<CyclicGroupElement, E> {
        val powerOfValueOfGenerator = target.context.run {
            valueOfGenerator.pow(this@CyclicGroup.order)
        }
        require(powerOfValueOfGenerator == target.unit) {
            "Cannot define monoid map from CyclicGroup(${this.order}): " +
                "$valueOfGenerator.pow(${this.order}) must be unit, but was $powerOfValueOfGenerator"
        }
        val values = (0 until this.order).map { i ->
            target.context.run {
                valueOfGenerator.pow(i)
            }
        }
        return FiniteMonoidMap(
            source = this,
            target = target,
            values = values,
        )
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }

    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.TEX -> "{\\mathbb Z}/${this.order}"
            else -> "Z/${this.order}"
        }
    }
}
