package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.PrintConfig

public data class EndElement<E : FiniteMonoidElement>(
    public val baseMonoid: FiniteMonoid<E>,
    public val asMap: FiniteMonoidMap<E, E>,
) : FiniteMonoidElement {
    override fun toString(printConfig: PrintConfig): String {
        return this.asMap.toString(printConfig)
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EndElement<*>

        if (asMap != other.asMap) return false

        return true
    }

    override fun hashCode(): Int {
        return asMap.hashCode()
    }

    public companion object {
        public fun <E : FiniteMonoidElement> getAll(baseMonoid: FiniteMonoid<E>): List<EndElement<E>> {
            val id = FiniteMonoidMap.id(baseMonoid)
            val maps = FiniteMonoidMap.listAllMaps(baseMonoid, baseMonoid)
            val mapsSorted = listOf(id) + maps.filter { it != id }
            return mapsSorted.map { EndElement(baseMonoid, it) }
        }
    }
}

public class FiniteMonoidEnd<E : FiniteMonoidElement>(
    public val baseMonoid: FiniteMonoid<E>,
) : FiniteMonoid<EndElement<E>> {
    override val context: FiniteMonoidContext<EndElement<E>> = FiniteMonoidContext(this)
    override val unit: EndElement<E> = EndElement(baseMonoid, FiniteMonoidMap.id(baseMonoid))
    override val elements: List<EndElement<E>> = EndElement.getAll(baseMonoid)
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

    override fun toString(printConfig: PrintConfig): String {
        return "End(${this.baseMonoid.toString(printConfig)})"
    }

    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }
}
