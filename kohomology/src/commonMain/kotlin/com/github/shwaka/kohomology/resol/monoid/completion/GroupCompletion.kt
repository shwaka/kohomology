package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.FiniteGroup
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap

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
    public val canonicalMap: FiniteMonoidMap<E, Division<E>>

    public companion object {
        public operator fun <E : FiniteMonoidElement> invoke(
            monoid: FiniteMonoid<E>
        ): GroupCompletion<E> {
            if (monoid.isCommutative) {
                return CommutativeGroupCompletion(monoid)
            }
            LeftOreFinder(monoid).let {
                if (it.isOre()) {
                    return LeftOreGroupCompletion(it)
                }
            }
            RightOreFinder(monoid).let {
                if (it.isOre()) {
                    return RightOreGroupCompletion(it)
                }
            }
            throw IllegalArgumentException(
                "GroupCompletion supports only commutative, left Ore or right Ore monoids"
            )
        }
    }
}
