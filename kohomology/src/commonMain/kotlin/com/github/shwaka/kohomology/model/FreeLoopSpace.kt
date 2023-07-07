package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.DGDerivation
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.InclusionFromIntDegreeToMultiDegree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeMorphism
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeDGAlgebraContext
import com.github.shwaka.kohomology.free.FreeDGAlgebraContextImpl
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.monoid.FreeMonoid
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType

private class FreeLoopSpaceFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    shiftDegree: D?,
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val shiftDegree = shiftDegree ?: freeDGAlgebra.degreeGroup.fromInt(1)
    val loopSpaceGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = run {
        val degreeGroup = this.freeDGAlgebra.degreeGroup
        val shift = this.shiftDegree
        val loopSpaceIndeterminateList = freeDGAlgebra.indeterminateList.let { list ->
            list.map { it.copy(degreeGroup, degreeGroup.zero) } +
                list.map { it.copy(degreeGroup, shift, showShiftExponentInIdentifier = false) }
        }
        FreeGAlgebra(this.matrixSpace, degreeGroup, loopSpaceIndeterminateList, CopiedName.Companion::getInternalPrintConfig)
    }
    val differential: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraInclusion: GAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M>
    init {
        val n = freeDGAlgebra.indeterminateList.size
        val loopSpaceGeneratorList = loopSpaceGAlgebra.generatorList
        this.suspension = run {
            val suspensionDegree = this.freeDGAlgebra.degreeGroup.context.run {
                -this@FreeLoopSpaceFactory.shiftDegree
            }
            val suspensionValueList = loopSpaceGeneratorList.takeLast(n) + List(n) {
                loopSpaceGAlgebra.zeroGVector
            }
            loopSpaceGAlgebra.getDerivation(suspensionValueList, suspensionDegree)
        }
        this.gAlgebraInclusion = freeDGAlgebra.getGAlgebraMap(
            loopSpaceGAlgebra,
            loopSpaceGeneratorList.take(n)
        )
        val differentialValueList = run {
            val baseSpaceGeneratorList = freeDGAlgebra.generatorList
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

public class FreeLoopSpace<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreeLoopSpaceFactory<D, I, S, V, M>
) : FreeDGAlgebra<D, CopiedName<D, I>, S, V, M>,
    DGAlgebra<D, Monomial<D, CopiedName<D, I>>, S, V, M> by DGAlgebra(factory.loopSpaceGAlgebra, factory.differential) {
    public constructor(
        freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
        shiftDegree: D? = null,
    ) : this(FreeLoopSpaceFactory(freeDGAlgebra, shiftDegree))

    override val context: FreeDGAlgebraContext<D, CopiedName<D, I>, S, V, M> = FreeDGAlgebraContextImpl(this)
    override val degreeGroup: AugmentedDegreeGroup<D> = factory.freeDGAlgebra.degreeGroup
    override val underlyingGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = factory.loopSpaceGAlgebra
    override val indeterminateList: List<Indeterminate<D, CopiedName<D, I>>> =
        factory.loopSpaceGAlgebra.indeterminateList
    override val monoid: FreeMonoid<D, CopiedName<D, I>> = factory.loopSpaceGAlgebra.monoid

    public val suspension: DGDerivation<D, Monomial<D, CopiedName<D, I>>, S, V, M> =
        DGDerivation(this, this.factory.suspension)
    public val inclusion: DGAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion
        )
    }
    public val shiftDegree: D = factory.shiftDegree
    public val baseSpace: FreeDGAlgebra<D, I, S, V, M> = factory.freeDGAlgebra

    public fun getDegree(totalDegree: Int, shiftLength: Int): D {
        val shiftDegree = this.shiftDegree
        return this.degreeGroup.context.run {
            totalDegree - shiftLength * (shiftDegree - 1)
        }
    }

    override fun getIdentity(): DGAlgebraMap<D, Monomial<D, CopiedName<D, I>>, Monomial<D, CopiedName<D, I>>, S, V, M> {
        // getIdentity() is implemented in DGAlgebraImpl,
        // but 'this' in it is DGAlgebra, not FreeLoopSpace
        val gAlgebraMap = this.underlyingGAlgebra.getIdentity()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }

    override fun toString(): String {
        return this.toString(PrintConfig(PrintType.PLAIN))
    }

    override fun toString(printConfig: PrintConfig): String {
        return "(${this.underlyingGAlgebra.toString(printConfig)}, d)"
    }

    public companion object {
        private const val degreeIndeterminateName: String = "S"
        public fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withShiftDegree(
            freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
        ): FreeLoopSpace<MultiDegree, I, S, V, M> {
            @Suppress("UNCHECKED_CAST")
            return when (freeDGAlgebra.degreeGroup) {
                is IntDegreeGroup -> this.withShiftDegreeForIntDegree(freeDGAlgebra as FreeDGAlgebra<IntDegree, I, S, V, M>)
                is MultiDegreeGroup -> this.withShiftDegreeForMultiDegree(freeDGAlgebra as FreeDGAlgebra<MultiDegree, I, S, V, M>)
                else -> throw UnsupportedOperationException("withShiftDegree is supported only for IntDegree and MultiDegree")
            }
        }

        private fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withShiftDegreeForMultiDegree(
            freeDGAlgebra: FreeDGAlgebra<MultiDegree, I, S, V, M>,
        ): FreeLoopSpace<MultiDegree, I, S, V, M> {
            val degreeGroup = freeDGAlgebra.degreeGroup
            if (degreeGroup !is MultiDegreeGroup)
                throw NotImplementedError("not supported!")
            if (degreeGroup.indeterminateList.any { it.name == this.degreeIndeterminateName })
                throw IllegalArgumentException("indeterminateList cannot contain an indeterminate of name \"${this.degreeIndeterminateName}\"")
            val newDegreeGroup = MultiDegreeGroup(
                degreeGroup.indeterminateList + listOf(DegreeIndeterminate(this.degreeIndeterminateName, 0))
            )
            val shiftDegree = newDegreeGroup.context.run {
                val s = newDegreeGroup.generatorList.last()
                -2 * s + 1
            }
            val degreeMorphism = MultiDegreeMorphism(
                degreeGroup,
                newDegreeGroup,
                newDegreeGroup.generatorList.dropLast(1),
            )
            val (newFreeDGAlgebra, _) = freeDGAlgebra.convertDegree(degreeMorphism)
            return FreeLoopSpace(newFreeDGAlgebra, shiftDegree)
        }

        private fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withShiftDegreeForIntDegree(
            freeDGAlgebra: FreeDGAlgebra<IntDegree, I, S, V, M>,
        ): FreeLoopSpace<MultiDegree, I, S, V, M> {
            val degreeGroup = freeDGAlgebra.degreeGroup
            if (degreeGroup !is IntDegreeGroup)
                throw NotImplementedError("not supported!")
            val newDegreeGroup = MultiDegreeGroup(
                listOf(DegreeIndeterminate(this.degreeIndeterminateName, 0))
            )
            val shiftDegree = newDegreeGroup.context.run {
                val s = newDegreeGroup.generatorList.last()
                -2 * s + 1
            }
            val degreeMorphism = InclusionFromIntDegreeToMultiDegree(newDegreeGroup)
            val (newFreeDGAlgebra, _) = freeDGAlgebra.convertDegree(degreeMorphism)
            return FreeLoopSpace(newFreeDGAlgebra, shiftDegree)
        }
    }
}
