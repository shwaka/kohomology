package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

fun <I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> freePathSpace(
    freeDGAlgebra: FreeDGAlgebra<I, S, V, M>,
): FreeDGAlgebra<CopiedName<I>, S, V, M> {
    val n = freeDGAlgebra.gAlgebra.indeterminateList.size
    val matrixSpace = freeDGAlgebra.matrixSpace
    val pathSpaceGAlgebra = run {
        val pathSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.copy(shift = 0, index = 1) } +
                list.map { it.copy(shift = 0, index = 2) } +
                list.map { it.copy(shift = 1) }
        }
        FreeGAlgebra(matrixSpace, pathSpaceIndeterminateList)
    }
    val pathSpaceGeneratorList = pathSpaceGAlgebra.generatorList
    val suspension = run {
        val suspensionValueList = pathSpaceGeneratorList.takeLast(n) +
            pathSpaceGeneratorList.takeLast(n) +
            List(n) {
                pathSpaceGAlgebra.context.run { zeroGVector }
            }
        pathSpaceGAlgebra.getDerivation(suspensionValueList, -1)
    }
    val inclusion1 = freeDGAlgebra.gAlgebra.getAlgebraMap(
        pathSpaceGAlgebra,
        pathSpaceGeneratorList.take(n)
    )
    val inclusion2 = freeDGAlgebra.gAlgebra.getAlgebraMap(
        pathSpaceGAlgebra,
        pathSpaceGeneratorList.slice(n until 2 * n)
    )
    var differentialValueList = run {
        val baseSpaceGeneratorList = freeDGAlgebra.gAlgebra.generatorList
        val valueList1 = baseSpaceGeneratorList.map { v ->
            freeDGAlgebra.context.run { inclusion1(d(v)) }
        }
        val valueList2 = baseSpaceGeneratorList.map { v ->
            freeDGAlgebra.context.run { inclusion2(d(v)) }
        }
        val valueList3 = List(n) { pathSpaceGAlgebra.zeroGVector }
        valueList1 + valueList2 + valueList3
    }
    for (index in 0 until n)
        differentialValueList = getNextValueList(pathSpaceGAlgebra, suspension, differentialValueList, index)
    val differential = pathSpaceGAlgebra.getDerivation(differentialValueList, 1)
    return FreeDGAlgebra(pathSpaceGAlgebra, differential, matrixSpace)
}

private fun <I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getNextValueList(
    pathSpaceGAlgebra: FreeGAlgebra<CopiedName<I>, S, V, M>,
    suspension: GLinearMap<Monomial<CopiedName<I>>, Monomial<CopiedName<I>>, S, V, M>,
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
