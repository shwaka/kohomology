package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class FreeLoopSpaceFactory<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    freeDGAlgebra: FreeDGAlgebra<I, S, V, M>
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val loopSpaceGAlgebra: FreeGAlgebra<CopiedName<I>, S, V, M> = run {
        val loopSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.copy(0) } + list.map { it.copy(1) }
        }
        FreeGAlgebra(this.matrixSpace, loopSpaceIndeterminateList)
    }
    val differential: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M>
    val suspension: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M>
    val inclusion: GLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M>
    init {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = loopSpaceGAlgebra.generatorList
        this.suspension = run {
            val suspensionValueList = loopSpaceGeneratorList.takeLast(n) + List(n) {
                loopSpaceGAlgebra.zeroGVector
            }
            loopSpaceGAlgebra.getDerivation(suspensionValueList, -1)
        }
        this.inclusion = freeDGAlgebra.gAlgebra.getAlgebraMap(
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
        this.differential = loopSpaceGAlgebra.getDerivation(differentialValueList, 1)
    }
}

class FreeLoopSpace<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreeLoopSpaceFactory<I, S, V, M>
): FreeDGAlgebra<CopiedName<I>, S, V, M>(factory.loopSpaceGAlgebra, factory.differential, factory.matrixSpace) {
    constructor(freeDGAlgebra: FreeDGAlgebra<I, S, V, M>) : this(FreeLoopSpaceFactory(freeDGAlgebra))
    val suspension: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M> =
        this.factory.suspension
    val inclusion: GLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M> =
        this.factory.inclusion
}
