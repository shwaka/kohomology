package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // \begin
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3)
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (a, b, x, y, z) ->
        mapOf(
            // da = 0
            // db = 0
            x to a.pow(2), // dx = a^2
            y to a * b,    // dy = ab
            z to b.pow(2), // dz = b^2
        )
    }
    for (degree in 0 until 10) {
        val basis = freeDGAlgebra.cohomology.getBasis(degree)
        println("H^$degree = Q$basis")
    }
    // \end
}
