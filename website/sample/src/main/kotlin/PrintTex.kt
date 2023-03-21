package com.github.shwaka.kohomology.sample

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.model.FreeLoopSpace
import com.github.shwaka.kohomology.specific.SparseMatrixSpaceOverRational
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printer
import com.github.shwaka.kohomology.util.ShowShift

fun main() {
    // \begin{def}
    val indeterminateList = listOf(
        Indeterminate("a", 2),
        Indeterminate("b", 2),
        Indeterminate("x", 3),
        Indeterminate("y", 3),
        Indeterminate("z", 3),
    )
    val matrixSpace = SparseMatrixSpaceOverRational
    val freeDGAlgebra = FreeDGAlgebra.fromMap(matrixSpace, indeterminateList) { (a, b, x, y, z) ->
        mapOf(
            x to a.pow(2),
            y to a * b,
            z to b.pow(2),
        )
    }
    val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
    // \end{def}

    println("----- plain output -----")
    // \begin{plain}
    for (degree in 0..4) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        println("H^$degree(LX) = Q$basis")
    }
    // \end{plain}

    println("----- tex output -----")
    // \begin{tex}
    val p = Printer(printType = PrintType.TEX, showShift = ShowShift.BAR)
    for (degree in 0..4) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        println("H^{$degree}(LX) &= \\Q${basis.map { v -> p(v) }} \\\\")
    }
    // \end{tex}

    println("----- long tex output -----")
    // \begin{long}
    val p2 = Printer(printType = PrintType.TEX, beforeSign = "\n", showShift = ShowShift.BAR)
    for (degree in 0..6) {
        val basis = freeLoopSpace.cohomology.getBasis(degree)
        val basisString = basis.joinToString(",\n") { v -> p2(v) }
        println("\\begin{autobreak}\nH^{$degree}(LX) = \\Q[\n${basisString}]\n\\end{autobreak}\\\\")
    }
    // \end{long}
}
