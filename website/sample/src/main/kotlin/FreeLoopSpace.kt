package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // \begin{sphere}
    // Define the Sullivan model of the 4-sphere.
    val sphereDim = 4
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val sphere = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (x, y) ->
        mapOf(y to x.pow(2)) // dx = 0, dy = x^2
    }
    // \end{sphere}

    // \begin{freeLoopSpace}
    // Define the Sullivan model of the free loop space.
    val freeLoopSpace = FreeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.generatorList
    // \end{freeLoopSpace}

    // \begin{computation}
    // Assert that d(sy) and -2*x*sx are the same.
    freeLoopSpace.context.run {
        println("dsy = ${d(sy)} = ${-2 * x * sx}")
    }

    // Compute cohomology of the free loop space.
    for (degree in 0 until 25) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        println("H^$degree(LS^$sphereDim) = Q$basis")
    }
    // \end{computation}

    // \begin{freeLoopSpaceWithMultiDegree}
    val freeLoopSpaceWithMultiDegree = FreeLoopSpace.withShiftDegree(sphere)
    for (degree in 0 until 25) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        println("H^$degree(LS^$sphereDim) = Q$basis")
    }
    // \end{freeLoopSpaceWithMultiDegree}
}
