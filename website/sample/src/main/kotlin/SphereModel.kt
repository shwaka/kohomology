package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // \begin{def}
    val n = 2
    // Declare an indeterminate (generator) for the free commutative graded algebra Λ(x,y)
    val indeterminateList = listOf(
        Indeterminate("x", 2 * n),
        Indeterminate("y", 4 * n - 1),
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    // Sullivan algebra can be defined by using the function FreeDGAlgebra.fromMap.
    // The last argument is a function
    // which receives the list of generators and returns the map representing the differential.
    val sphere = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (x, y) ->
        mapOf(
            y to x.pow(2),    // x.pow(2) represents x^2
        )
        // If you want, you can write dx = 0 explicitly in the code
        // by using zeroGVector, a special element that represents zero in any degree.
        // mapOf(
        //     x to zeroGVector,
        //     y to x.pow(2),
        // )
    }
    // \end{def}

    // \begin{cohomology}
    for (degree in 0 until 10) {
        val basis = sphere.cohomology.getBasis(degree)
        println("H^$degree(S^${2 * n}) = Q$basis")
    }
    // \end{cohomology}

    // \begin{context}
    val (x, y) = sphere.generatorList

    // You can't write DGA operations here.

    sphere.context.run {
        // You can write DGA operations in "context.run"
        println("d(x * y) = ${d(x * y)}")
        println(d(x).isZero())
        println(x.cohomologyClass())
        println(x.pow(2).cohomologyClass())
    }
    // \end{context}
}
