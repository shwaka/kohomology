package com.github.shwaka.kohomology.util

public data class ComparablePair<A : Comparable<A>, B : Comparable<B>>(
    val first: A,
    val second: B,
) : Comparable<ComparablePair<A, B>> {
    override fun compareTo(other: ComparablePair<A, B>): Int {
        return when {
            this.first < other.first -> -1
            this.first > other.first -> 1
            else -> when {
                this.second < other.second -> -1
                this.second > other.second -> 1
                else -> 0
            }
        }
    }
}

public fun <A : Comparable<A>, B : Comparable<B>> directProductOf(
    collection1: Collection<A>,
    collection2: Collection<B>,
): List<ComparablePair<A, B>> {
    return collection1.flatMap { elm1 ->
        collection2.map { elm2 -> ComparablePair(elm1, elm2) }
    }
}
