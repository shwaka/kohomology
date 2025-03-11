package com.github.shwaka.kohomology.util

public fun shuffles(counts: List<Int>): List<List<Int>> {
    if (counts.all { it == 0 }) {
        return listOf(emptyList())
    }
    val n = counts.size
    val result: MutableList<List<Int>> = mutableListOf()
    for (i in 0 until n) {
        if (counts[i] > 0) {
            val newCounts = counts.mapIndexed { j, count ->
                if (j == i) {
                    count - 1
                } else {
                    count
                }
            }
            result.addAll(
                shuffles(newCounts).map { it + listOf(i) }
            )
        }
    }
    return result
}
