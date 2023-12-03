package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.FiniteGroup
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement

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
            if (monoid.isCommutative) {
                return CommutativeGroupCompletion(monoid)
            }
            val leftOreFinder = LeftOreFinder(monoid)
            if (leftOreFinder.isOre()) {
                return LeftOreGroupCompletion(leftOreFinder)
            }
            throw Exception("RightOreGroupCompletion is not yet implemented")
        }
    }
}
