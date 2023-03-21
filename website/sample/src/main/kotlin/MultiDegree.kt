package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    // \begin{degree}
    val sphereDim = 2
    val degreeGroup = MultiDegreeGroup(
        listOf(
            DegreeIndeterminate("n", sphereDim / 2),
            DegreeIndeterminate("m", sphereDim / 2),
        )
    )
    val (n, m) = degreeGroup.generatorList
    // \end{degree}

    // \begin{model}
    val indeterminateList = degreeGroup.context.run {
        listOf(
            Indeterminate("x", 2 * n),
            Indeterminate("y", 4 * n - 1),
            Indeterminate("a", 2 * m),
            Indeterminate("b", 4 * m - 1),
        )
    }
    val matrixSpace = SparseMatrixSpaceOverRational
    val sphere = FreeDGAlgebra.fromList(matrixSpace, degreeGroup, indeterminateList) { (x, y, a, b) ->
        listOf(zeroGVector, x.pow(2), zeroGVector, a.pow(2))
    }
    // \end{model}

    // \begin{cohomology}
    degreeGroup.context.run {
        println(sphere.cohomology.getBasis(0))
        println(sphere.cohomology.getBasis(2 * n))
        println(sphere.cohomology.getBasis(2 * m))
        println(sphere.cohomology.getBasisForAugmentedDegree(sphereDim))
    }
    // \end{cohomology}
}
