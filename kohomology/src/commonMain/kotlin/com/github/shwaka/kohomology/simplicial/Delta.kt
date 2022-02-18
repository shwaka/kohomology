package com.github.shwaka.kohomology.simplicial

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

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
