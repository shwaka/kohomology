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

private class DividedFreeLoopSpaceFactory<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<I, D, S, V, M>
) {
    // Λ(V⊕V⊕sV⊕sV)
    val matrixSpace = freeDGAlgebra.matrixSpace
    val dividedLoopSpaceGAlgebra: FreeGAlgebra<CopiedName<I, D>, D, S, V, M> = run {
        val degreeMonoid = this.freeDGAlgebra.gAlgebra.degreeGroup
        val dividedLoopSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            val zero = degreeMonoid.zero
            val one = degreeMonoid.fromInt(1)
            list.map { it.copy(degreeMonoid, shift = zero, index = 1) } + list.map { it.copy(degreeMonoid, shift = zero, index = 2) } +
                list.map { it.copy(degreeMonoid, shift = one, index = 1) } + list.map { it.copy(degreeMonoid, shift = one, index = 2) }
        }
        FreeGAlgebra(this.matrixSpace, degreeMonoid, dividedLoopSpaceIndeterminateList)
    }
    val pathSpaceDGAlgebra: FreeDGAlgebra<CopiedName<I, D>, D, S, V, M> = FreePathSpace(freeDGAlgebra)
    val differential: Derivation<Monomial<CopiedName<I, D>, D>, D, S, V, M>
    val pathGAlgebraInclusion1: GAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M>
    val pathGAlgebraInclusion2: GAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M>

    val loopSpaceDGAlgebra: FreeDGAlgebra<CopiedName<I, D>, D, S, V, M> by lazy { FreeLoopSpace(freeDGAlgebra) }
    val gAlgebraProjection1: GAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M> by lazy {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = this.loopSpaceDGAlgebra.gAlgebra.generatorList
        val zeroGVector = this.loopSpaceDGAlgebra.context.run { zeroGVector }
        this.dividedLoopSpaceGAlgebra.getGAlgebraMap(
            this.loopSpaceDGAlgebra.gAlgebra,
            loopSpaceGeneratorList.take(n) + loopSpaceGeneratorList.take(n) +
                loopSpaceGeneratorList.takeLast(n) + List(n) { zeroGVector }
        )
    }
    val gAlgebraProjection2: GAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M> by lazy {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = this.loopSpaceDGAlgebra.gAlgebra.generatorList
        val zeroGVector = this.loopSpaceDGAlgebra.context.run { zeroGVector }
        this.dividedLoopSpaceGAlgebra.getGAlgebraMap(
            this.loopSpaceDGAlgebra.gAlgebra,
            loopSpaceGeneratorList.take(n) + loopSpaceGeneratorList.take(n) +
                List(n) { zeroGVector } + loopSpaceGeneratorList.takeLast(n)
        )
    }

    init {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val pathSpaceDGAlgebra = this.pathSpaceDGAlgebra
        val dividedLoopSpaceGeneratorList = this.dividedLoopSpaceGAlgebra.generatorList
        this.pathGAlgebraInclusion1 = pathSpaceDGAlgebra.gAlgebra.getGAlgebraMap(
            this.dividedLoopSpaceGAlgebra,
            dividedLoopSpaceGeneratorList.take(3 * n)
        )
        this.pathGAlgebraInclusion2 = pathSpaceDGAlgebra.gAlgebra.getGAlgebraMap(
            this.dividedLoopSpaceGAlgebra,
            dividedLoopSpaceGeneratorList.take(2 * n) + dividedLoopSpaceGeneratorList.takeLast(n)
        )
        val differentialValueList = pathSpaceDGAlgebra.context.run {
            val valueList1 = pathSpaceDGAlgebra.gAlgebra.generatorList.map { v ->
                this@DividedFreeLoopSpaceFactory.pathGAlgebraInclusion1(d(v))
            }
            val valueList2 = pathSpaceDGAlgebra.gAlgebra.generatorList.takeLast(n).map { v ->
                this@DividedFreeLoopSpaceFactory.pathGAlgebraInclusion2(d(v))
            }
            valueList1 + valueList2
        }
        this.differential = this.dividedLoopSpaceGAlgebra.getDerivation(differentialValueList, 1)
    }
}

class DividedFreeLoopSpace<I : IndeterminateName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DividedFreeLoopSpaceFactory<I, D, S, V, M>
) : FreeDGAlgebra<CopiedName<I, D>, D, S, V, M>(factory.dividedLoopSpaceGAlgebra, factory.differential, factory.matrixSpace) {
    constructor(freeDGAlgebra: FreeDGAlgebra<I, D, S, V, M>) : this(DividedFreeLoopSpaceFactory(freeDGAlgebra))
    val freeLoopSpace = this.factory.loopSpaceDGAlgebra
    val projection1: DGAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M> by lazy {
        DGAlgebraMap(
            source = this,
            target = this.factory.loopSpaceDGAlgebra,
            gLinearMap = this.factory.gAlgebraProjection1
        )
    }
    val projection2: DGAlgebraMap<Monomial<CopiedName<I, D>, D>, Monomial<CopiedName<I, D>, D>, D, S, V, M> by lazy {
        DGAlgebraMap(
            source = this,
            target = this.factory.loopSpaceDGAlgebra,
            gLinearMap = this.factory.gAlgebraProjection2
        )
    }
}
