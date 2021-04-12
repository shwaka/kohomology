package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.IndeterminateName
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class FreePathSpaceFactory<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<I, S, V, M>
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val pathSpaceGAlgebra: FreeGAlgebra<CopiedName<I>, S, V, M> = run {
        val pathSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.copy(shift = 0, index = 1) } +
                list.map { it.copy(shift = 0, index = 2) } +
                list.map { it.copy(shift = 1) }
        }
        FreeGAlgebra(this.matrixSpace, pathSpaceIndeterminateList)
    }
    val differential: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M>
    val suspension: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M>
    val gAlgebraInclusion1: GLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M>
    val gAlgebraInclusion2: GLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M>
    init {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val pathSpaceGeneratorList = this.pathSpaceGAlgebra.generatorList
        this.suspension = run {
            val suspensionValueList = pathSpaceGeneratorList.takeLast(n) +
                pathSpaceGeneratorList.takeLast(n) +
                List(n) {
                    this.pathSpaceGAlgebra.zeroGVector
                }
            this.pathSpaceGAlgebra.getDerivation(suspensionValueList, -1)
        }
        this.gAlgebraInclusion1 = freeDGAlgebra.gAlgebra.getAlgebraMap(
            this.pathSpaceGAlgebra,
            pathSpaceGeneratorList.take(n)
        )
        this.gAlgebraInclusion2 = freeDGAlgebra.gAlgebra.getAlgebraMap(
            this.pathSpaceGAlgebra,
            pathSpaceGeneratorList.slice(n until 2 * n)
        )
        var differentialValueList = run {
            val baseSpaceGeneratorList = freeDGAlgebra.gAlgebra.generatorList
            val valueList1 = baseSpaceGeneratorList.map { v ->
                freeDGAlgebra.context.run { this@FreePathSpaceFactory.gAlgebraInclusion1(d(v)) }
            }
            val valueList2 = baseSpaceGeneratorList.map { v ->
                freeDGAlgebra.context.run { this@FreePathSpaceFactory.gAlgebraInclusion2(d(v)) }
            }
            val valueList3 = List(n) { this.pathSpaceGAlgebra.zeroGVector }
            valueList1 + valueList2 + valueList3
        }
        for (index in 0 until n)
            differentialValueList = this.getNextValueList(differentialValueList, index)
        this.differential = pathSpaceGAlgebra.getDerivation(differentialValueList, 1)
    }

    private fun getNextValueList(
        currentValueList: List<GVectorOrZero<Monomial<CopiedName<I>>, S, V>>,
        index: Int
    ): List<GVectorOrZero<Monomial<CopiedName<I>>, S, V>> {
        val n = pathSpaceGAlgebra.indeterminateList.size / 3
        if (index < 0 || index >= n)
            throw Exception("This can't happen! (illegal index)")
        if (!currentValueList[2 * n + index].isZero())
            throw Exception("This can't happen! (computing twice)")
        val differential = pathSpaceGAlgebra.getDerivation(currentValueList, 1)
        val generatorList = pathSpaceGAlgebra.generatorList
        val value = pathSpaceGAlgebra.context.run {
            val v1 = generatorList[index]
            val v2 = generatorList[n + index]
            var tempValue = v2 - v1
            var sdv1 = v1 // (sd)v1 (本当は (sd)^k (v1) / k! だけど…)
            var k = 1
            while (!sdv1.isZero()) {
                sdv1 = suspension(differential(sdv1)) * k.toScalar().inv()
                k += 1
                tempValue -= sdv1
            }
            tempValue
        }
        val zeroGVector = pathSpaceGAlgebra.zeroGVector
        return currentValueList.take(2 * n + index) + listOf(value) + List(n - index - 1) { zeroGVector }
    }
}

class FreePathSpace<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreePathSpaceFactory<I, S, V, M>
) : FreeDGAlgebra<CopiedName<I>, S, V, M>(factory.pathSpaceGAlgebra, factory.differential, factory.matrixSpace) {
    constructor(freeDGAlgebra: FreeDGAlgebra<I, S, V, M>) : this(FreePathSpaceFactory(freeDGAlgebra))
    val suspension: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M> =
        this.factory.suspension
    val inclusion1: DGLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M> by lazy {
        DGLinearMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion1
        )
    }
    val inclusion2: DGLinearMap<Monomial<I>, Monomial<CopiedName<I>>, S, V, M> by lazy {
        DGLinearMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion2
        )
    }
}
