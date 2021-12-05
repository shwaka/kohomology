package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    // start
    val sphereDim = 4
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2
    }

    for (degree in 0 until 10) {
        val basis = sphere.cohomology[degree].getBasis()
        println("H^$degree(S^$sphereDim) = Q$basis")
    }
    // end

    // start context
    val (x, y) = sphere.gAlgebra.generatorList

    sphere.context.run {
        // Operations in a DGA can be applied within 'context.run'
        println("d(x * y) = ${d(x * y)}")
        println(d(x).isZero())
        println(x.cohomologyClass())
        println(x.pow(2).cohomologyClass())
    }
    // end context
}
