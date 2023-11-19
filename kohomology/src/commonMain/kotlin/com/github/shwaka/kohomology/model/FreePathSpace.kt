package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebraContext
import com.github.shwaka.kohomology.free.FreeDGAlgebraContextImpl
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.monoid.FreeGMonoid
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

private class FreePathSpaceFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val pathSpaceGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = run {
        val n = freeDGAlgebra.indeterminateList.size
        val degreeGroup = this.freeDGAlgebra.degreeGroup
        val zero = degreeGroup.zero
        val one = degreeGroup.fromInt(1)
        val pathSpaceIndeterminateList = freeDGAlgebra.indeterminateList.let { list ->
            list.map { it.copy(degreeGroup, shift = zero, index = 1) } +
                list.map { it.copy(degreeGroup, shift = zero, index = 2) } +
                list.map { it.copy(degreeGroup, shift = one, showShiftExponentInIdentifier = false) }
        }
        val basisComparator: Comparator<Monomial<D, CopiedName<D, I>>> by lazy {
            // compare by the total length in shifted generators
            var comparator: Comparator<Monomial<D, CopiedName<D, I>>> =
                compareBy { monomial -> (0 until n).map { monomial.exponentList[it + 2 * n] }.sum() }
            // compare by the exponents in shifted generators
            for (i in 0 until n)
                comparator = comparator.thenBy { monomial -> monomial.exponentList[i + 2 * n] }
            // compare by the exponents in the generators in first component
            for (i in 0 until n)
                comparator = comparator.thenBy { monomial -> monomial.exponentList[i] }
            // compare by the exponents in the generators in second component
            for (i in 0 until n)
                comparator = comparator.thenBy { monomial -> monomial.exponentList[i + n] }
            comparator
        }
        FreeGAlgebra(this.matrixSpace, degreeGroup, pathSpaceIndeterminateList) { printType ->
            CopiedName.getInternalPrintConfig<D, I, S>(printType).copy(basisComparator = basisComparator)
        }
    }
    val differential: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraInclusion1: GAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraInclusion2: GAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraProjection: GAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, I>, S, V, M>
    init {
        val n = freeDGAlgebra.indeterminateList.size
        val pathSpaceGeneratorList = this.pathSpaceGAlgebra.generatorList
        this.suspension = run {
            val suspensionValueList = pathSpaceGeneratorList.takeLast(n) +
                pathSpaceGeneratorList.takeLast(n) +
                List(n) {
                    this.pathSpaceGAlgebra.zeroGVector
                }
            this.pathSpaceGAlgebra.getDerivation(suspensionValueList, -1)
        }
        this.gAlgebraInclusion1 = freeDGAlgebra.getGAlgebraMap(
            this.pathSpaceGAlgebra,
            pathSpaceGeneratorList.take(n)
        )
        this.gAlgebraInclusion2 = freeDGAlgebra.getGAlgebraMap(
            this.pathSpaceGAlgebra,
            pathSpaceGeneratorList.slice(n until 2 * n)
        )
        this.gAlgebraProjection = run {
            val gAlgebraGeneratorList = freeDGAlgebra.generatorList.take(n)
            val zeroGVector = freeDGAlgebra.zeroGVector
            pathSpaceGAlgebra.getGAlgebraMap(
                freeDGAlgebra,
                gAlgebraGeneratorList + gAlgebraGeneratorList + List(n) { zeroGVector }
            )
        }
        var differentialValueList = run {
            val baseSpaceGeneratorList = freeDGAlgebra.generatorList
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
        currentValueList: List<GVectorOrZero<D, Monomial<D, CopiedName<D, I>>, S, V>>,
        index: Int
    ): List<GVectorOrZero<D, Monomial<D, CopiedName<D, I>>, S, V>> {
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

public class FreePathSpace<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreePathSpaceFactory<D, I, S, V, M>
) : FreeDGAlgebra<D, CopiedName<D, I>, S, V, M>,
    DGAlgebra<D, Monomial<D, CopiedName<D, I>>, S, V, M> by DGAlgebra(factory.pathSpaceGAlgebra, factory.differential) {
    public constructor(freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>) : this(FreePathSpaceFactory(freeDGAlgebra))

    override val context: FreeDGAlgebraContext<D, CopiedName<D, I>, S, V, M> = FreeDGAlgebraContextImpl(this)
    override val degreeGroup: AugmentedDegreeGroup<D> = factory.freeDGAlgebra.degreeGroup
    override val underlyingGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = factory.pathSpaceGAlgebra
    override val indeterminateList: List<Indeterminate<D, CopiedName<D, I>>> =
        factory.pathSpaceGAlgebra.indeterminateList
    override val monoid: FreeGMonoid<D, CopiedName<D, I>> = factory.pathSpaceGAlgebra.monoid

    public val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M> =
        this.factory.suspension
    public val inclusion1: DGAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion1
        )
    }
    public val inclusion2: DGAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion2
        )
    }
    public val projection: DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, I>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this,
            target = this.factory.freeDGAlgebra,
            gLinearMap = this.factory.gAlgebraProjection
        )
    }

    override fun getIdentity(): DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> {
        // getIdentity() is implemented in DGAlgebraImpl,
        // but 'this' in it is DGAlgebra, not FreePathSpace
        val gAlgebraMap = this.underlyingGAlgebra.getIdentity()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return "(${this.underlyingGAlgebra.toString(printConfig)}, d)"
    }

    // private val n: Int by lazy {
    //     this.factory.freeDGAlgebra.generatorList.size
    // }
}
