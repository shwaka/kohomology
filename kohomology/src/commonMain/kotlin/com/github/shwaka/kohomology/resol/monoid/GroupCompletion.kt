package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.GenericUnionFind
import com.github.shwaka.kohomology.util.directProductOf

public data class Division<E : FiniteMonoidElement>(val numerator: E, val denominator: E) : FiniteMonoidElement {
    override fun toString(): String {
        return "(${this.numerator}/${this.denominator})"
    }

    public companion object {
        public fun <E : FiniteMonoidElement> fromPair(pair: Pair<E, E>): Division<E> {
            return Division(pair.first, pair.second)
        }
    }
}

public interface GroupCompletion<E : FiniteMonoidElement> : FiniteGroup<Division<E>> {
    public val monoid: FiniteMonoid<E>

    public companion object {
        public operator fun <E : FiniteMonoidElement> invoke(
            monoid: FiniteMonoid<E>
        ): GroupCompletion<E> {
            return CommutativeGroupCompletion(monoid)
        }
    }
}

private class CommutativeGroupCompletion<E : FiniteMonoidElement>(
    override val monoid: FiniteMonoid<E>
) : GroupCompletion<E> {
    init {
        require(monoid.isCommutative) {
            "GroupCompletion can be applied only to commutative monoid, but $monoid is not commutative"
        }
    }

    override val context: FiniteGroupContext<Division<E>> = FiniteGroupContextImpl(this)

    private val unionFind = monoid.elements.let { elements ->
        val divisions = directProductOf(elements, elements).map { Division.fromPair(it) }
        val unionFind = GenericUnionFind(divisions)
        for (division in divisions) {
            for (element in elements) {
                val divisionToBeIdentified = Division(
                    numerator = this.monoid.multiply(division.numerator, element),
                    denominator = this.monoid.multiply(division.denominator, element),
                )
                unionFind.unite(division, divisionToBeIdentified)
            }
        }
        unionFind
    }

    override val elements: List<Division<E>> = unionFind.groups().map { group ->
        unionFind.rootOf(group[0])
    }
    override val unit: Division<E> = unionFind.rootOf(Division(monoid.unit, monoid.unit))
    override val isCommutative: Boolean = true

    override fun multiply(monoidElement1: Division<E>, monoidElement2: Division<E>): Division<E> {
        return this.unionFind.rootOf(
            Division(
                numerator = this.monoid.multiply(monoidElement1.numerator, monoidElement2.numerator),
                denominator = this.monoid.multiply(monoidElement1.denominator, monoidElement2.denominator),
            )
        )
    }

    override val multiplicationTable: List<List<Division<E>>> by lazy {
        FiniteMonoid.getMultiplicationTable(this.elements, this::multiply)
    }

    override fun invert(monoidElement: Division<E>): Division<E> {
        return this.unionFind.rootOf(
            Division(
                numerator = monoidElement.denominator,
                denominator = monoidElement.numerator,
            )
        )
    }
}
