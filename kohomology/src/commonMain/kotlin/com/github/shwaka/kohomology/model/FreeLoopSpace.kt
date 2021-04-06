package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.Indeterminate
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree

data class ShiftedName<I>(val name: I, val shift: Degree) {
    override fun toString(): String {
        return when (this.shift) {
            0 -> this.name.toString()
            1 -> "s${this.name}"
            else -> "s^{${this.shift}}${this.name}"
        }
    }
}

fun <I> Indeterminate<I>.shift(
    shift: Degree
): Indeterminate<ShiftedName<I>> {
    return Indeterminate(ShiftedName(this.name, shift), this.degree - shift)
}

fun <I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freeLoopSpace(
    freeDGAlgebra: FreeDGAlgebra<I, S, V, M>,
): FreeDGAlgebra<ShiftedName<I>, S, V, M> {
    val n = freeDGAlgebra.gAlgebra.indeterminateList.size
    val matrixSpace = freeDGAlgebra.matrixSpace
    val loopSpaceGAlgebra = run {
        val loopSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.shift(0) } + list.map { it.shift(1) }
        }
        FreeGAlgebra(matrixSpace, loopSpaceIndeterminateList)
    }
    val loopSpaceGeneratorList = loopSpaceGAlgebra.generatorList
    val suspension = run {
        val suspensionValueList = loopSpaceGeneratorList.takeLast(n) + List(n) {
            loopSpaceGAlgebra.context.run { zeroGVector }
        }
        loopSpaceGAlgebra.getDerivation(suspensionValueList, -1)
    }
    val inclusion = freeDGAlgebra.gAlgebra.getAlgebraMap(
        loopSpaceGAlgebra,
        loopSpaceGeneratorList.take(n)
    )
    val differentialValueList = run {
        val baseSpaceGeneratorList = freeDGAlgebra.gAlgebra.generatorList
        val valueList1 = baseSpaceGeneratorList.map { v ->
            freeDGAlgebra.context.run { inclusion(d(v)) }
        }
        val valueList2 = baseSpaceGeneratorList.map { v ->
            val dv = freeDGAlgebra.context.run { d(v) }
            loopSpaceGAlgebra.context.run { -suspension(inclusion(dv)) }
        }
        valueList1 + valueList2
    }
    val differential = loopSpaceGAlgebra.getDerivation(differentialValueList, 1)
    return FreeDGAlgebra(loopSpaceGAlgebra, differential, matrixSpace)
}
