package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.DGDerivation
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeIndeterminate
import com.github.shwaka.kohomology.dg.degree.DegreeMorphism
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegree
import com.github.shwaka.kohomology.dg.degree.MultiDegreeGroup
import com.github.shwaka.kohomology.dg.degree.MultiDegreeMorphism
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.monoid.FreeMonoidMorphismByDegreeChange
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class FreeLoopSpaceFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    val shiftDegree: D?,
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val loopSpaceGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = run {
        val degreeGroup = this.freeDGAlgebra.gAlgebra.degreeGroup
        val degreeForShiftNonNull = shiftDegree ?: degreeGroup.fromInt(1)
        val loopSpaceIndeterminateList = freeDGAlgebra.gAlgebra.indeterminateList.let { list ->
            list.map { it.copy(degreeGroup, degreeGroup.zero) } + list.map { it.copy(degreeGroup, degreeForShiftNonNull) }
        }
        FreeGAlgebra(this.matrixSpace, degreeGroup, loopSpaceIndeterminateList, CopiedName.Companion::getInternalPrintConfig)
    }
    val differential: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraInclusion: GAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M>
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

class FreeLoopSpace<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: FreeLoopSpaceFactory<D, I, S, V, M>
) : FreeDGAlgebra<D, CopiedName<D, I>, S, V, M>(factory.loopSpaceGAlgebra, factory.differential, factory.matrixSpace) {
    constructor(
        freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
        shiftDegree: D? = null,
    ) : this(FreeLoopSpaceFactory(freeDGAlgebra, shiftDegree))

    val suspension: DGDerivation<D, Monomial<D, CopiedName<D, I>>, S, V, M> =
        DGDerivation(this, this.factory.suspension)
    val inclusion: DGAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion
        )
    }

    companion object {
        private const val degreeIndeterminateName: String = "S"
        fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> withShiftDegree(
            freeDGAlgebra: FreeDGAlgebra<MultiDegree, I, S, V, M>,
        ): FreeLoopSpace<MultiDegree, I, S, V, M> {
            val degreeGroup = freeDGAlgebra.gAlgebra.degreeGroup
            if (degreeGroup !is MultiDegreeGroup)
                throw NotImplementedError("not supported!")
            if (degreeGroup.indeterminateList.any { it.name == this.degreeIndeterminateName })
                throw IllegalArgumentException("indeterminateList cannot contain an indeterminate of name \"${this.degreeIndeterminateName}\"")
            val newDegreeGroup = MultiDegreeGroup(
                degreeGroup.indeterminateList + listOf(DegreeIndeterminate(this.degreeIndeterminateName, 0))
            )
            val degreeMorphism = MultiDegreeMorphism(
                degreeGroup,
                newDegreeGroup,
                newDegreeGroup.generatorList.dropLast(1),
            )
            val (newFreeGAlgebra, changeDegree) = freeDGAlgebra.gAlgebra.convertDegree(degreeMorphism)
            val differentialValueList = freeDGAlgebra.gAlgebra.generatorList.map { v ->
                val dv = freeDGAlgebra.context.run { d(v) }
                changeDegree(dv)
            }
            val differential = newFreeGAlgebra.getDerivation(differentialValueList, 1)
            val matrixSpace = freeDGAlgebra.matrixSpace
            val newFreeDGAlgebra = FreeDGAlgebra(newFreeGAlgebra, differential, matrixSpace)
            return FreeLoopSpace(newFreeDGAlgebra)
        }
    }
}
