package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebraContext
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

private class DividedFreeLoopSpaceFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>
) {
    // Λ(V⊕V⊕sV⊕sV)
    val matrixSpace = freeDGAlgebra.matrixSpace
    val dividedLoopSpaceGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = run {
        val degreeGroup = this.freeDGAlgebra.degreeGroup
        val dividedLoopSpaceIndeterminateList = freeDGAlgebra.indeterminateList.let { list ->
            val zero = degreeGroup.zero
            val one = degreeGroup.fromInt(1)
            list.map { it.copy(degreeGroup, shift = zero, index = 1) } +
                list.map { it.copy(degreeGroup, shift = zero, index = 2) } +
                list.map { it.copy(degreeGroup, shift = one, index = 1, showShiftExponentInIdentifier = true) } +
                list.map { it.copy(degreeGroup, shift = one, index = 2, showShiftExponentInIdentifier = true) }
        }
        FreeGAlgebra(this.matrixSpace, degreeGroup, dividedLoopSpaceIndeterminateList, CopiedName.Companion::getInternalPrintConfig)
    }
    val pathSpaceDGAlgebra: FreePathSpace<D, I, S, V, M> = FreePathSpace(freeDGAlgebra)
    val differential: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val pathGAlgebraInclusion1: GAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M>
    val pathGAlgebraInclusion2: GAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M>

    val loopSpaceDGAlgebra: FreeLoopSpace<D, I, S, V, M> by lazy { FreeLoopSpace(freeDGAlgebra) }
    val gAlgebraProjection1: GAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        val n = freeDGAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = this.loopSpaceDGAlgebra.generatorList
        val zeroGVector = this.loopSpaceDGAlgebra.context.run { zeroGVector }
        this.dividedLoopSpaceGAlgebra.getGAlgebraMap(
            this.loopSpaceDGAlgebra,
            loopSpaceGeneratorList.take(n) + loopSpaceGeneratorList.take(n) +
                loopSpaceGeneratorList.takeLast(n) + List(n) { zeroGVector }
        )
    }
    val gAlgebraProjection2: GAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        val n = freeDGAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = this.loopSpaceDGAlgebra.generatorList
        val zeroGVector = this.loopSpaceDGAlgebra.context.run { zeroGVector }
        this.dividedLoopSpaceGAlgebra.getGAlgebraMap(
            this.loopSpaceDGAlgebra,
            loopSpaceGeneratorList.take(n) + loopSpaceGeneratorList.take(n) +
                List(n) { zeroGVector } + loopSpaceGeneratorList.takeLast(n)
        )
    }

    init {
        val n = freeDGAlgebra.indeterminateList.size
        val pathSpaceDGAlgebra = this.pathSpaceDGAlgebra
        val dividedLoopSpaceGeneratorList = this.dividedLoopSpaceGAlgebra.generatorList
        this.pathGAlgebraInclusion1 = pathSpaceDGAlgebra.getGAlgebraMap(
            this.dividedLoopSpaceGAlgebra,
            dividedLoopSpaceGeneratorList.take(3 * n)
        )
        this.pathGAlgebraInclusion2 = pathSpaceDGAlgebra.getGAlgebraMap(
            this.dividedLoopSpaceGAlgebra,
            dividedLoopSpaceGeneratorList.take(2 * n) + dividedLoopSpaceGeneratorList.takeLast(n)
        )
        val differentialValueList = pathSpaceDGAlgebra.context.run {
            val valueList1 = pathSpaceDGAlgebra.generatorList.map { v ->
                this@DividedFreeLoopSpaceFactory.pathGAlgebraInclusion1(d(v))
            }
            val valueList2 = pathSpaceDGAlgebra.generatorList.takeLast(n).map { v ->
                this@DividedFreeLoopSpaceFactory.pathGAlgebraInclusion2(d(v))
            }
            valueList1 + valueList2
        }
        this.differential = this.dividedLoopSpaceGAlgebra.getDerivation(differentialValueList, 1)
    }
}

public class DividedFreeLoopSpace<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DividedFreeLoopSpaceFactory<D, I, S, V, M>
) : FreeDGAlgebra<D, CopiedName<D, I>, S, V, M>,
    DGAlgebra<D, Monomial<D, CopiedName<D, I>>, S, V, M> by DGAlgebra(factory.dividedLoopSpaceGAlgebra, factory.differential) {
    public constructor(freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>) : this(DividedFreeLoopSpaceFactory(freeDGAlgebra))

    override val context: FreeDGAlgebraContext<D, CopiedName<D, I>, S, V, M> = FreeDGAlgebraContext(this)
    override val degreeGroup: AugmentedDegreeGroup<D> = factory.freeDGAlgebra.degreeGroup
    override val underlyingGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = factory.dividedLoopSpaceGAlgebra
    override val indeterminateList: List<Indeterminate<D, CopiedName<D, I>>> =
        factory.dividedLoopSpaceGAlgebra.indeterminateList
    override val monoid: FreeGMonoid<D, CopiedName<D, I>> = factory.dividedLoopSpaceGAlgebra.monoid

    public val freeLoopSpace: FreeLoopSpace<D, I, S, V, M> = this.factory.loopSpaceDGAlgebra
    public val projection1: DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this,
            target = this.factory.loopSpaceDGAlgebra,
            gLinearMap = this.factory.gAlgebraProjection1
        )
    }
    public val projection2: DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this,
            target = this.factory.loopSpaceDGAlgebra,
            gLinearMap = this.factory.gAlgebraProjection2
        )
    }

    override fun getIdentity(): DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> {
        // getIdentity() is implemented in DGAlgebraImpl,
        // but 'this' in it is DGAlgebra, not DividedFreeLoopSpace
        val gAlgebraMap = this.underlyingGAlgebra.getIdentity()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return "(${this.underlyingGAlgebra.toString(printConfig)}, d)"
    }
}
