package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // start
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3)
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->
        val da = zeroGVector // da = 0
        val db = zeroGVector // db = 0
        val dx = a.pow(2) // dx = a^2
        val dy = a * b // dy = ab
        val dz = b.pow(2) // dz = b^2
        listOf(da, db, dx, dy, dz)
    }
    for (degree in 0 until 10) {
        val basis = sphere.cohomology[degree].getBasis()
        println("H^$degree = Q$basis")
    }
    // end
}
