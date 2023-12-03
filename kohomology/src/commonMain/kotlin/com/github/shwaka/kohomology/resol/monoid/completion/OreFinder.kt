package com.github.shwaka.kohomology.resol.monoid.completion

import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement

internal abstract class OreFinderBase<E : FiniteMonoidElement>(
    protected val monoid: FiniteMonoid<E>,
) {
    abstract fun multiplyMayBeReversed(element1: E, element2: E): E

    private val orePairCache: MutableMap<Pair<E, E>, Pair<E, E>> = mutableMapOf()

    private fun findOrePairOrNull(element1: E, element2: E): Pair<E, E>? {
        val cacheKey = Pair(element1, element2)
        this.orePairCache[cacheKey]?.let { return it }
        for (candidate1 in this.monoid.elements) {
            for (candidate2 in this.monoid.elements) {
                val multiplied1 = this.multiplyMayBeReversed(candidate1, element1)
                val multiplied2 = this.multiplyMayBeReversed(candidate2, element2)
                if (multiplied1 == multiplied2) {
                    return Pair(candidate1, candidate2).also { this.orePairCache[cacheKey] = it }
                }
            }
        }
        return null
    }

    fun findOrePair(element1: E, element2: E): Pair<E, E> {
        return this.findOrePairOrNull(element1, element2) ?: throw IllegalStateException("Ore pair not found")
    }

    fun isOre(): Boolean {
        return this.monoid.elements.all { element1 ->
            this.monoid.elements.all { element2 ->
                this.findOrePairOrNull(element1, element2) != null
            }
        }
    }
}

internal class LeftOreFinder<E : FiniteMonoidElement>(
    monoid: FiniteMonoid<E>,
) : OreFinderBase<E>(monoid) {
    override fun multiplyMayBeReversed(element1: E, element2: E): E {
        return this.monoid.multiply(element1, element2)
    }
}

internal class RightOreFinder<E : FiniteMonoidElement>(
    monoid: FiniteMonoid<E>,
) : OreFinderBase<E>(monoid) {
    override fun multiplyMayBeReversed(element1: E, element2: E): E {
        return this.monoid.multiply(element2, element1)
    }
}
