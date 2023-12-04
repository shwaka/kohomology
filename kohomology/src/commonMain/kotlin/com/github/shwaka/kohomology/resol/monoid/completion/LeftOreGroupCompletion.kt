package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.FiniteGroupContext
import com.github.shwaka.kohomology.resol.monoid.FiniteGroupContextImpl
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.util.FrozenGenericUnionFind
import com.github.shwaka.kohomology.util.GenericUnionFind
import com.github.shwaka.kohomology.util.directProductOf

internal class LeftOreGroupCompletion<E : FiniteMonoidElement>(
    private val oreFinder: LeftOreFinder<E>,
) : GroupCompletion<E> {
    init {
        require(oreFinder.isOre()) {
            "LeftOreGroupCompletion can be applied only to a monoid satisfying left Ore condition, " +
                "but ${oreFinder.monoid} does not satisfy it"
        }
    }

    override val monoid: FiniteMonoid<E> = oreFinder.monoid
    override val context: FiniteGroupContext<Division<E>> = FiniteGroupContextImpl(this)

    private val unionFind: FrozenGenericUnionFind<Division<E>> = monoid.elements.let { elements ->
        val divisions = directProductOf(elements, elements).map { Division.fromPair(it) }
        val unionFind = GenericUnionFind(divisions)
        for (division in divisions) {
            for (element in elements) {
                val divisionToBeIdentified = Division(
                    numerator = this.monoid.multiply(element, division.numerator),
                    denominator = this.monoid.multiply(element, division.denominator),
                )
                unionFind.unite(division, divisionToBeIdentified)
            }
        }
        unionFind
    }

    override val elements: List<Division<E>> = unionFind.representatives()
    override val unit: Division<E> = unionFind.rootOf(Division(monoid.unit, monoid.unit))
    override val isCommutative: Boolean by lazy {
        FiniteMonoid.isCommutative(this.elements, this::multiply)
    }

    override fun multiply(monoidElement1: Division<E>, monoidElement2: Division<E>): Division<E> {
        // d1\n1 * d2\n2 = pd1\pn1 * qd2\qn2 = pd1\qn2
        // where p*n1 = q*d2
        val (p, q) = this.oreFinder.findOrePair(monoidElement1.numerator, monoidElement2.denominator)
        val division = this.monoid.context.run {
            Division(
                denominator = p * monoidElement1.denominator,
                numerator = q * monoidElement2.numerator,
            )
        }
        return this.unionFind.rootOf(division)
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

    override val canonicalMap: FiniteMonoidMap<E, Division<E>> by lazy {
        val values = this.monoid.elements.map {
            this.unionFind.rootOf(Division(it, this.monoid.unit))
        }
        FiniteMonoidMap(
            source = this.monoid,
            target = this,
            values = values,
        )
    }
}
