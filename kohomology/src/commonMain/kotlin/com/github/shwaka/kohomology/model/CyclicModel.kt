package com.github.shwaka.kohomology.model

import com.github.shwaka.kohomology.dg.DGAlgebraMap
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GAlgebraMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.FreeDGAlgebra
import com.github.shwaka.kohomology.free.FreeGAlgebra
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.free.monoid.StringIndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class CyclicModelFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
    val periodicity: I,
) {
    init {
        if (freeDGAlgebra.gAlgebra.indeterminateList.any { it.name == periodicity }) {
            throw IllegalArgumentException(
                "The free DGA $freeDGAlgebra contains an indeterminate '$periodicity', " +
                    "which is given as the generator for periodicity"
            )
        }
    }

    val matrixSpace = freeDGAlgebra.matrixSpace
    val cyclicGAlgebra: FreeGAlgebra<D, CopiedName<D, I>, S, V, M> = run {
        val degreeGroup = this.freeDGAlgebra.gAlgebra.degreeGroup
        val loopSpaceIndeterminateList = listOf(
            Indeterminate(periodicity, degreeGroup.fromInt(2)).copy(degreeGroup, degreeGroup.zero)
        ) + freeDGAlgebra.gAlgebra.indeterminateList.map {
            listOf(
                it.copy(degreeGroup, degreeGroup.fromInt(1)),
                it.copy(degreeGroup, degreeGroup.zero),
            )
        }.flatten()
        FreeGAlgebra(this.matrixSpace, degreeGroup, loopSpaceIndeterminateList)
    }
    val differential: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M>
    val gAlgebraInclusion: GAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M>
    init {
        val n = freeDGAlgebra.gAlgebra.indeterminateList.size
        val cyclicGeneratorList = cyclicGAlgebra.generatorList
        this.suspension = run {
            val suspensionValueList = listOf(cyclicGAlgebra.zeroGVector) +
                (0 until n).map { i ->
                    listOf(
                        cyclicGAlgebra.zeroGVector,
                        cyclicGeneratorList[2 * i + 1],
                    )
                }.flatten()
            cyclicGAlgebra.getDerivation(suspensionValueList, -1)
        }
        this.gAlgebraInclusion = freeDGAlgebra.gAlgebra.getGAlgebraMap(
            cyclicGAlgebra,
            (0 until n).map { i ->
                cyclicGeneratorList[2 * i + 2]
            }
        )
        val differentialValueList = run {
            val u = cyclicGeneratorList[0]
            val baseSpaceGeneratorList = freeDGAlgebra.gAlgebra.generatorList
            listOf(cyclicGAlgebra.zeroGVector) +
                baseSpaceGeneratorList.map { v ->
                    val dv = freeDGAlgebra.context.run { d(v) }
                    cyclicGAlgebra.context.run {
                        listOf(
                            -suspension(gAlgebraInclusion(dv)),
                            gAlgebraInclusion(dv) + u * suspension(gAlgebraInclusion(v))
                        )
                    }
                }.flatten()
        }
        this.differential = cyclicGAlgebra.getDerivation(differentialValueList, 1)
    }
}

public class CyclicModel<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: CyclicModelFactory<D, I, S, V, M>
) : FreeDGAlgebra<D, CopiedName<D, I>, S, V, M>(factory.cyclicGAlgebra, factory.differential, factory.matrixSpace) {
    public val suspension: Derivation<D, Monomial<D, CopiedName<D, I>>, S, V, M> =
        this.factory.suspension
    public val inclusion: DGAlgebraMap<D, Monomial<D, I>, Monomial<D, CopiedName<D, I>>, S, V, M> by lazy {
        DGAlgebraMap(
            source = this.factory.freeDGAlgebra,
            target = this,
            gLinearMap = this.factory.gAlgebraInclusion
        )
    }

    public companion object {
        public operator fun <D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            freeDGAlgebra: FreeDGAlgebra<D, StringIndeterminateName, S, V, M>
        ): CyclicModel<D, StringIndeterminateName, S, V, M> {
            return CyclicModel(CyclicModelFactory(freeDGAlgebra, StringIndeterminateName("u")))
        }

        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
            periodicity: I,
        ): CyclicModel<D, I, S, V, M> {
            return CyclicModel(CyclicModelFactory(freeDGAlgebra, periodicity))
        }
    }
}