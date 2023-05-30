package com.github.shwaka.kohomology.util

public fun <A, B> directProductOf(
    collection1: Collection<A>,
    collection2: Collection<B>,
): List<Pair<A, B>> {
    return collection1.flatMap { elm1 ->
        collection2.map { elm2 -> Pair(elm1, elm2) }
    }
}

public fun <A : Comparable<A>, B : Comparable<B>> comparableDirectProductOf(
    collection1: Collection<A>,
    collection2: Collection<B>,
): List<ComparablePair<A, B>> {
    return collection1.flatMap { elm1 ->
        collection2.map { elm2 -> ComparablePair(elm1, elm2) }
    }
}
