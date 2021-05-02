package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.IndeterminateName
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class FreeLoopSpaceFactory<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<I, D, S, V, M>
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val loopSpaceGAlgebra: FreeGAlgebra<CopiedName<I, D>, D, S, V, M> = run {
        val degreeMonoid = this.freeDGAlgebra.gAlgebra.degreeGroup
        val loopSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.copy(degreeMonoid, degreeMonoid.zero) } + list.map { it.copy(degreeMonoid, degreeMonoid.fromInt(1)) }
        }
        FreeGAlgebra(this.matrixSpace, degreeMonoid, loopSpaceIndeterminateList)
    }
    val differential: Derivation<Monomial<CopiedName<I, D>, D>, D, S, V, M>
    val suspension: Derivation<Monomial<CopiedName<I, D>, D>, D, S, V, M>
    val gAlgebraInclusion: GAlgebraMap<Monomial<I, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M>
    init {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = loopSpaceGAlgebra.generatorList
        this.suspension = run {
            val suspensionValueList = loopSpaceGeneratorList.takeLast(n) + List(n) {
                loopSpaceGAlgebra.zeroGVector
            }
            loopSpaceGAlgebra.getDerivation(suspensionValueList, -1)
        }
        this.gAlgebraInclusion = freeDGAlgebra.gAlgebra.getGAlgebraMap(
            loopSpaceGAlgebra,
            loopSpaceGeneratorList.take(n)
        )
        val differentialValueList = run {
            val baseSpaceGeneratorList = freeDGAlgebra.gAlgebra.generatorList
            val valueList1 = baseSpaceGeneratorList.map { v ->
                freeDGAlgebra.context.run { gAlgebraInclusion(d(v)) }
            }
            val valueList2 = baseSpaceGeneratorList.map { v ->
                val dv = freeDGAlgebra.context.run { d(v) }
                loopSpaceGAlgebra.context.run { -suspension(gAlgebraInclusion(dv)) }
            }
            valueList1 + valueList2
        }
        this.differential = loopSpaceGAlgebra.getDerivation(differentialValueList, 1)
    }
}

class FreeLoopSpace<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreeLoopSpaceFactory<I, D, S, V, M>
) : FreeDGAlgebra<CopiedName<I, D>, D, S, V, M>(factory.loopSpaceGAlgebra, factory.differential, factory.matrixSpace) {
    constructor(freeDGAlgebra: FreeDGAlgebra<I, D, S, V, M>) : this(FreeLoopSpaceFactory(freeDGAlgebra))
    val suspension: Derivation<Monomial<CopiedName<I, D>, D>, D, S, V, M> =
        this.factory.suspension
    val inclusion: DGAlgebraMap<Monomial<I, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion
        )
    }
}
