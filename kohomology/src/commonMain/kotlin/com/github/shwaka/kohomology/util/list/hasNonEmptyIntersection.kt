package com.github.shwaka.kohomology.util.list

/**
 * Returns true if the intersection of two *sorted* lists is empty.
 *
 * Two lists [this] and [other] are assumed to be already sorted in increasing order.
 * Otherwise returns incorrect result.
 */
internal fun <T : Comparable<T>> List<T>.hasEmptyIntersection(other: List<T>): Boolean {
    var i = 0
    var j = 0
    while (i < this.size && j < other.size) {
        when {
            (this[i] == other[j]) -> return false
            (this[i] > other[j]) -> j++
            (this[i] < other[j]) -> i++
            else -> throw Exception("This can't happen!")
        }
    }
    return true
}

/**
 * Returns true if the intersection of two *sorted* lists is non-empty.
 *
 * Two lists [this] and [other] are assumed to be already sorted in increasing order.
 * Otherwise returns incorrect result.
 */
internal fun <T : Comparable<T>> List<T>.hasNonEmptyIntersection(other: List<T>): Boolean {
    return !this.hasEmptyIntersection(other)
}
