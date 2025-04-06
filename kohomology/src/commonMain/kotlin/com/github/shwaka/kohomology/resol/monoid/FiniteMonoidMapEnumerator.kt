package com.github.shwaka.kohomology.resol.monoid

import com.github.shwaka.kohomology.util.BooleanWithCause
import com.github.shwaka.kohomology.util.pow

internal interface FiniteMonoidMapEnumerator {
    fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> listAllMaps(
        source: FiniteMonoid<ES>,
        target: FiniteMonoid<ET>,
    ): List<FiniteMonoidMap<ES, ET>>

    object Naive : FiniteMonoidMapEnumerator {
        // This is very naive implementation and should be used in test.
        override fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> listAllMaps(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>
        ): List<FiniteMonoidMap<ES, ET>> {
            return target.elements.pow(source.elements.size).mapNotNull { values ->
                when (FiniteMonoidMap.isFiniteMonoidMap(source, target, values, earlyReturn = true)) {
                    is BooleanWithCause.True -> FiniteMonoidMap(source, target, values)
                    is BooleanWithCause.False -> null
                }
            }
        }
    }

    object UnitPreserving : FiniteMonoidMapEnumerator {
        override fun <ES : FiniteMonoidElement, ET : FiniteMonoidElement> listAllMaps(
            source: FiniteMonoid<ES>,
            target: FiniteMonoid<ET>
        ): List<FiniteMonoidMap<ES, ET>> {
            return target.elements.pow(source.elements.size - 1).mapNotNull { valuesForNonUnit ->
                val values = listOf(target.unit) + valuesForNonUnit
                when (FiniteMonoidMap.isFiniteMonoidMap(source, target, values, earlyReturn = true)) {
                    is BooleanWithCause.True -> FiniteMonoidMap(source, target, values)
                    is BooleanWithCause.False -> null
                }
            }
        }
    }
}
