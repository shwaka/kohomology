package com.github.shwaka.kohomology.bar

import com.github.shwaka.kohomology.util.GenericUnionFind
import com.github.shwaka.kohomology.util.directProductOf

public data class Division<E : FiniteMonoidElement>(val left: E, val right: E) : FiniteMonoidElement {
    override fun toString(): String {
        return "(${this.left}/${this.right})"
    }

    public companion object {
        public fun <E : FiniteMonoidElement> fromPair(pair: Pair<E, E>): Division<E> {
            return Division(pair.first, pair.second)
        }
    }
}

public class GroupCompletion<E : FiniteMonoidElement>(
    public val monoid: FiniteMonoid<E>
) : FiniteGroup<Division<E>> {
    private val unionFind = monoid.elements.let { elements ->
        val divisions = directProductOf(elements, elements).map { Division.fromPair(it) }
        val unionFind = GenericUnionFind(divisions)
        for (division in divisions) {
            for (element in elements) {
                val divisionToBeIdentified = Division(
                    left = this.monoid.multiply(division.left, element),
                    right = this.monoid.multiply(division.right, element),
                )
                unionFind.unite(division, divisionToBeIdentified)
            }
        }
        unionFind
    }

    override val elements: List<Division<E>> = unionFind.groups().map { group -> group[0] }
    override val unit: Division<E> = unionFind.rootOf(Division(monoid.unit, monoid.unit))

    override fun multiply(monoidElement1: Division<E>, monoidElement2: Division<E>): Division<E> {
        return this.unionFind.rootOf(
            Division(
                left = this.monoid.multiply(monoidElement1.left, monoidElement2.left),
                right = this.monoid.multiply(monoidElement1.right, monoidElement2.right),
            )
        )
    }

    override fun invert(monoidElement: Division<E>): Division<E> {
        return this.unionFind.rootOf(
            Division(
                left = monoidElement.right,
                right = monoidElement.left,
            )
        )
    }
}
