package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun sample1() {
    // start
    val sphereDim = 4
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2
    }

    for (degree in 0 until 10) {
        val basis = sphere.cohomology[degree].getBasis()
        println("H^$degree(S^$sphereDim) = Q$basis")
    }

    val freeLoopSpace = FreeLoopSpace(sphere)
    val (x, y, sx, sy) = freeLoopSpace.gAlgebra.generatorList

    freeLoopSpace.context.run {
        // Operations in a DGA can be applied within 'context.run'
        println("dsy = ${d(sy)} = ${-2 * x * sx}")
    }

    for (degree in 0 until 25) {
        val basis = freeLoopSpace.cohomology[degree].getBasis()
        println("H^$degree(LS^$sphereDim) = Q$basis")
    }
    // end
}
