package com.github.shwaka.kohomology.resol.monoid

public interface FiniteMonoidMap<ES : FiniteMonoidElement, ET : FiniteMonoidElement> {
    public val source: FiniteMonoid<ES>
    public val target: FiniteMonoid<ET>
    public val values: List<ET>

    public operator fun invoke(monoidElement: ES): ET {
        val index = this.source.elements.indexOf(monoidElement)
        require(index >= 0) {
            "$monoidElement is not an element of $source"
        }
        return this.values[index]
    }

    public companion object {
        public operator fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> invoke(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>,
            values: List<ET>,
        ): FiniteMonoidMap<ES, ET> {
            return FiniteMonoidMapImpl(source, target, values)
        }
    }
}

private class FiniteMonoidMapImpl<ES : FiniteMonoidElement, ET : FiniteMonoidElement>(
    override val source: FiniteMonoid<ES>,
    override val target: FiniteMonoid<ET>,
    override val values: List<ET>,
) : FiniteMonoidMap<ES, ET> {
    init {
        require(values.size == source.elements.size) {
            "$values should have the same size with $source"
        }
    }
}
