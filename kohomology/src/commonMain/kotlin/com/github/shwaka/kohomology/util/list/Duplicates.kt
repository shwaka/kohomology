package com.github.shwaka.kohomology.util.list

internal fun <T> List<T>.duplicates(): List<T> {
    val counts: Map<T, Int> = this.groupingBy { it }.eachCount()
    return counts.filter { it.value > 1 }.map { it.key }
}
