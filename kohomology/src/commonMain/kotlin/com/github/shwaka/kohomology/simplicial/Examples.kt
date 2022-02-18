package com.github.shwaka.kohomology.simplicial

private fun <T> List<T>.subsets(size: Int): List<List<T>> {
    when {
        (size > this.size) -> return emptyList()
        (size == 0) -> return listOf(emptyList())
        (size < 0) -> throw Exception("size must be non-negative")
        this.isEmpty() -> throw Exception("This can't happen! (contained in previous cases)")
    }
    val dropped = this.dropLast(1)
    val last = this.last()
    val subsetsWithoutLast = dropped.subsets(size)
    val subsetsWithLast = dropped.subsets(size - 1).map { it + listOf(last) }
    return subsetsWithoutLast + subsetsWithLast
}

public fun delta(
    dim: Int
): SimplicialComplex<Int> {
    return SimplicialComplex { i ->
        when {
            i < 0 -> emptyList()
            else -> (0..dim).toList().subsets(i + 1).map { Simplex((it)) }
        }
    }
}

public fun boundaryDelta(
    dim: Int
): SimplicialComplex<Int> {
    return SimplicialComplex { i ->
        when {
            (i < 0 || i == dim) -> emptyList()
            else -> (0..dim).toList().subsets(i + 1).map { Simplex((it)) }
        }
    }
}

public fun projectivePlane(): SimplicialComplex<Int> {
    //  0-1-2-9
    //  |/|/|/|
    //  3-4-5-6
    //  |/|/|/|
    //  6-7-8-3
    //  |/|/|\|
    //  9-2-1-0
    // Note:
    // The square 8310 (at the lower right corner) is triangulated in a different way
    // in order to avoid that the triangle 013 duplicates.
    val vertices = (0..9).map { Simplex(listOf(it)) }
    val edges = listOf(
        // horizontal edges
        listOf(0, 1),
        listOf(1, 2),
        listOf(2, 9),
        listOf(3, 4),
        listOf(4, 5),
        listOf(5, 6),
        listOf(6, 7),
        listOf(7, 8),
        listOf(8, 3),
        // vertical edges
        listOf(0, 3),
        listOf(3, 6),
        listOf(6, 9),
        listOf(1, 4),
        listOf(4, 7),
        listOf(7, 2),
        listOf(2, 5),
        listOf(5, 8),
        listOf(8, 1),
        // others
        listOf(1, 3),
        listOf(2, 4),
        listOf(9, 5),
        listOf(4, 6),
        listOf(5, 7),
        listOf(6, 8),
        listOf(7, 9),
        listOf(8, 2),
        listOf(8, 0),
    ).map { Simplex(it) }
    val faces = listOf(
        // first row
        listOf(0, 1, 3),
        listOf(1, 3, 4),
        listOf(1, 2, 4),
        listOf(2, 4, 5),
        listOf(2, 9, 5),
        listOf(9, 5, 6),
        // second row
        listOf(3, 4, 6),
        listOf(4, 6, 7),
        listOf(4, 5, 7),
        listOf(5, 7, 8),
        listOf(5, 6, 8),
        listOf(6, 8, 3),
        // third row
        listOf(6, 7, 9),
        listOf(7, 9, 2),
        listOf(7, 8, 2),
        listOf(8, 2, 1),
        listOf(8, 3, 0),
        listOf(8, 1, 0),
    ).map { Simplex(it) }
    return SimplicialComplex { i ->
        when (i) {
            0 -> vertices
            1 -> edges
            2 -> faces
            else -> emptyList()
        }
    }
}
