package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift

fun main() {
    // start def
    val n = 1
    val indeterminateList = listOf(
        Indeterminate("x", 2 * n),
        Indeterminate("y", 4 * n - 1),
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val sphere = FreeDGAlgebra(matrixSpace, indeterminateList) { (x, y) ->
        listOf(zeroGVector, x.pow(2))
    }
    val freeLoopSpace = FreeLoopSpace(sphere)
    // end def

    println("----- plain output -----")
    // start plain
    for (degree in 0..10) {
        val basis = freeLoopSpace.cohomology[degree].getBasis()
        println("H^$degree(LS^${2 * n}) = Q$basis")
    }
    // end plain

    println("----- tex output -----")
    // start tex
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
    for (degree in 0..10) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        freeLoopSpace.context.run {
            println("H^{$degree}(LS^${2 * n}) &= \\Q${basis.map { v -> p(v) }} \\\\")
        }
    }
    // end tex
}
