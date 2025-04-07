package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

public class FiniteMonoidAut<E : FiniteMonoidElement>(
    public val baseMonoid: FiniteMonoid<E>,
) : FiniteGroup<EndElement<E>> {
    override val context: FiniteGroupContext<EndElement<E>> = FiniteGroupContext(this)
    override val unit: EndElement<E> =
        EndElement(baseMonoid, FiniteMonoidMap.id(baseMonoid), this::getIndex)
    override val elements: List<EndElement<E>> =
        EndElement.getAll(baseMonoid, this::getIndex).filter { it.asMap.isBijective() }
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
            getIndex = this::getIndex,
        )
    }

    override val multiplicationTable: List<List<EndElement<E>>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun invert(monoidElement: EndElement<E>): EndElement<E> {
        return EndElement(this.baseMonoid, monoidElement.asMap.inv(), this::getIndex)
    }

    public val end: FiniteMonoidEnd<E> by lazy { FiniteMonoidEnd(this.baseMonoid) }
    public val inclusionToEnd: FiniteMonoidMap<EndElement<E>, EndElement<E>> by lazy {
        FiniteMonoidMap(
            source = this,
            target = this.end,
            values = this.elements.map { autElement ->
                // this.getIndex and this.end.getIndex are different
                autElement.copy(getIndex = this.end::getIndex)
            },
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

    public fun getIndex(endElement: EndElement<E>): Int {
        val index = this.elements.indexOf(endElement)
        if (index == -1) {
            throw NoSuchElementException("$endElement is not found in $this")
        }
        return index
    }

    override fun toString(printConfig: PrintConfig): String {
        val className = "Aut"
        val classNameFormatted = when (printConfig.printType) {
            PrintType.TEX -> "\\mathrm{$className}"
            else -> className
        }
        return "$classNameFormatted(${this.baseMonoid.toString(printConfig)})"
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
}
