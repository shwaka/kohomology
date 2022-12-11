package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational

fun main() {
    val sphereDim = 2
    val indeterminateList = listOf(
        Indeterminate("x", sphereDim),
        Indeterminate("y", sphereDim * 2 - 1)
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, _) ->
        listOf(zeroGVector, x.pow(2))
    }

    val freeLoopSpace = FreeLoopSpace(sphere)

    val degree: Int = System.getProperty("degree").toInt()
    var totalDim: Int = 0
    for (n in 0 until degree) {
        totalDim += freeLoopSpace.cohomology[n].dim
    }
    println("Total dim until $degree is $totalDim")
}
