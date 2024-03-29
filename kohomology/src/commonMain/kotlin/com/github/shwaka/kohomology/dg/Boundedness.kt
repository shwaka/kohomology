package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.util.directProductOf

public data class Boundedness(
    public val upperBound: Int? = null,
    public val lowerBound: Int? = null,
) {
    public companion object {
        public fun <D : Degree> listDegreePairsOfSum(
            degreeGroup: AugmentedDegreeGroup<D>,
            degreeSum: D,
            boundedness1: Boundedness,
            boundedness2: Boundedness,
        ): List<Pair<D, D>> {
            val intPairList = this.listIntPairsOfSum(
                degreeGroup.augmentation(degreeSum),
                boundedness1, boundedness2,
            )
            return intPairList.map { (int1, int2) ->
                directProductOf(
                    degreeGroup.listAllDegrees(int1),
                    degreeGroup.listAllDegrees(int2),
                ).filter { (degree1, degree2) ->
                    degreeGroup.context.run {
                        degree1 + degree2 == degreeSum
                    }
                }
            }.flatten()
        }

        private fun listIntPairsOfSum(
            sum: Int,
            boundedness1: Boundedness,
            boundedness2: Boundedness,
        ): List<Pair<Int, Int>> {
            return when {
                (boundedness1.lowerBound != null) && (boundedness2.lowerBound != null) -> {
                    (boundedness1.lowerBound..(sum - boundedness2.lowerBound)).map { p ->
                        Pair(p, sum - p)
                    }
                }
                (boundedness1.upperBound != null) && (boundedness2.upperBound != null) -> {
                    ((sum - boundedness2.upperBound)..boundedness1.upperBound).map { p ->
                        Pair(p, sum - p)
                    }
                }
                else -> {
                    throw Exception("Incompatible Boundedness: $boundedness1, $boundedness2")
                }
            }
        }

        public fun <D : Degree> fromDegreeList(
            degreeGroup: AugmentedDegreeGroup<D>,
            degreeList: List<D>,
        ): Boundedness {
            if (degreeList.isEmpty()) {
                // These can be any int, but we need to specify some int.
                return Boundedness(upperBound = 0, lowerBound = 0)
            }
            val upperBound: Int = degreeList.maxOf { degreeGroup.augmentation(it) }
            val lowerBound: Int = degreeList.minOf { degreeGroup.augmentation(it) }
            return Boundedness(upperBound = upperBound, lowerBound = lowerBound)
        }
    }
}
