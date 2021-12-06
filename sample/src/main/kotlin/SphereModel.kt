package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverBigRational

fun main() {
    // start def
    val n = 2
    val indeterminateList = listOf(
        Indeterminate("x", 2 * n),
        Indeterminate("y", 4 * n - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverBigRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2)) // dx = 0, dy = x^2
    }
    // end def

    // start cohomology
    for (degree in 0 until 10) {
        val basis = sphere.cohomology[degree].getBasis()
        println("H^$degree(S^${2 * n}) = Q$basis")
    }
    // end cohomology

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
