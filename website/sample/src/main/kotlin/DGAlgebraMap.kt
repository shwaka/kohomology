package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // \begin{model}
    val n = 1
    val matrixSpace = SparseMatrixSpaceOverRational

    // define a Sullivan model of the 4n-sphere
    val sphereIndeterminateList = listOf(
        Indeterminate("x", 4 * n),
        Indeterminate("y", 8 * n - 1),
    )
    val sphere = FreeDGAlgebra.fromMap(matrixSpace, sphereIndeterminateList) { (x, y) ->
        mapOf(y to x.pow(2))
    }

    // define a Sullivan model of the product of two 2n-spheres
    val sphereProductIndeterminateList = listOf(
        Indeterminate("a1", 2 * n),
        Indeterminate("b1", 4 * n - 1),
        Indeterminate("a2", 2 * n),
        Indeterminate("b2", 4 * n - 1),
    )
    val sphereProduct = FreeDGAlgebra.fromMap(matrixSpace, sphereProductIndeterminateList) { (a1, b1, a2, b2) ->
        mapOf(b1 to a1.pow(2), b2 to a2.pow(2))
    }
    // \end{model}

    // \begin{dgaMap}
    val (x, y) = sphere.generatorList
    val (a1, b1, a2, b2) = sphereProduct.generatorList
    val valueList = sphereProduct.context.run {
        listOf(a1 * a2, a1.pow(2) * b2)
    }
    val f = sphere.getDGAlgebraMap(sphereProduct, valueList)
    sphere.context.run {
        // This 'context' is necessary for pow(2) and cohomologyClass()
        println(f(x)) // a1a2
        println(f(x.pow(2))) // a1^2a2^2
        println(f.inducedMapOnCohomology(x.cohomologyClass())) // [a1a2]
        println(f.inducedMapOnCohomology(x.pow(2).cohomologyClass())) // 0
    }
    // \end{dgaMap}
}
