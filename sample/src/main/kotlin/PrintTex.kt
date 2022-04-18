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
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3)
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val freeDGAlgebra = FreeDGAlgebra(matrixSpace, indeterminateList) { (a, b, x, y, z) ->
        listOf(zeroGVector, zeroGVector, a.pow(2), a * b, b.pow(2))
    }
    val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
    // end def

    println("----- plain output -----")
    // start plain
    for (degree in 0..4) {
        val basis = freeLoopSpace.cohomology[degree].getBasis()
        println("H^$degree(LX) = Q$basis")
    }
    // end plain

    println("----- tex output -----")
    // start tex
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
    for (degree in 0..4) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        freeLoopSpace.context.run {
            println("H^{$degree}(LX) &= \\Q${basis.map { v -> p(v) }} \\\\")
        }
    }
    // end tex
}
