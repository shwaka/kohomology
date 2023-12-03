package com.github.shwaka.kohomology.resol.monoid

public data class CyclicGroupElement(val value: Int, val order: Int) : FiniteMonoidElement {
    init {
        require((0 <= this.value) && (this.value < this.order))
    }
    override fun toString(): String {
        return "t^${this.value}"
    }
}

public class CyclicGroup(public val order: Int) : FiniteGroup<CyclicGroupElement> {
    init {
        require(this.order > 0)
    }

    override val context: FiniteGroupContext<CyclicGroupElement> = FiniteGroupContextImpl(this)
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
}
