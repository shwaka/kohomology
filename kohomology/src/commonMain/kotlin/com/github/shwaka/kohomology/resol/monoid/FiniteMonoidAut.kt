package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig

public class FiniteMonoidAut<E : FiniteMonoidElement>(
    public val baseMonoid: FiniteMonoid<E>,
) : FiniteGroup<EndElement<E>> {
    override val context: FiniteGroupContext<EndElement<E>> = FiniteGroupContext(this)
    override val unit: EndElement<E> = EndElement(baseMonoid, FiniteMonoidMap.id(baseMonoid))
    override val elements: List<EndElement<E>> = EndElement.getAll(baseMonoid).filter { it.asMap.isBijective() }
    override val isCommutative: Boolean by lazy {
        FiniteMonoid.isCommutative(elements, ::multiply)
    }

    override fun multiply(monoidElement1: EndElement<E>, monoidElement2: EndElement<E>): EndElement<E> {
        require(monoidElement1.baseMonoid == this.baseMonoid) {
            "$monoidElement1 is not an endomorphism on ${this.baseMonoid}"
        }
        require(monoidElement2.baseMonoid == this.baseMonoid) {
            "$monoidElement2 is not an endomorphism on ${this.baseMonoid}"
        }
        return EndElement(
            baseMonoid = this.baseMonoid,
            asMap = monoidElement1.asMap * monoidElement2.asMap,
        )
    }

    override val multiplicationTable: List<List<EndElement<E>>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun invert(monoidElement: EndElement<E>): EndElement<E> {
        TODO("Not yet implemented")
    }

    public val end: FiniteMonoidEnd<E> by lazy { FiniteMonoidEnd(this.baseMonoid) }
    public val inclusionToEnd: FiniteMonoidMap<EndElement<E>, EndElement<E>> by lazy {
        FiniteMonoidMap(
            source = this,
            target = this.end,
            values = this.elements,
        )
    }

    public fun asAction(): FiniteMonoidAction<EndElement<E>, E> {
        return FiniteMonoidAction(
            source = this,
            target = this.baseMonoid,
            targetEnd = this.end,
            actionMap = this.inclusionToEnd,
        )
    }

    override fun toString(printConfig: PrintConfig): String {
        return "End(${this.baseMonoid.toString(printConfig)})"
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
}
