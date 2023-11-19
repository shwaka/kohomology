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

/**
 * Returns the direct product of collections in [collections].
 *
 * When [collections] is `[[1, 2, 3], [4, 5]]`,
 * then this function returns `[[1, 4], [1, 5], [2, 4], [2, 5], [3, 4], [3, 5]]`.
 *
 * Note that `directProductOfFamily(listOf(collection1, collection2))`
 * is the same as `directProductOf(collection1, collection2)`.
 */
public fun <T> directProductOfFamily(
    collections: Collection<Collection<T>>
): List<List<T>> {
    var result: List<List<T>> = listOf(emptyList())
    for (collection in collections) {
        result = result.flatMap { partial: List<T> ->
            collection.map { elm -> partial + listOf(elm) }
        }
    }
    return result
}
