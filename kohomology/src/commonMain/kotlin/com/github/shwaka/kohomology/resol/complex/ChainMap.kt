package com.github.shwaka.kohomology.resol.complex

import com.github.shwaka.kohomology.dg.DGLinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.ModuleMap
import com.github.shwaka.kohomology.vectsp.BasisName

public interface ChainMap<
    D : Degree,
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>> {

    public val name: String
    public val source: Complex<D, BA, BS, S, V, M>
    public val target: Complex<D, BA, BT, S, V, M>

    public fun getModuleMap(degree: D): ModuleMap<BA, BS, BT, S, V, M>

    public val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M>

    public operator fun invoke(gVector: GVector<D, BS, S, V>): GVector<D, BT, S, V> {
        return this.underlyingDGLinearMap(gVector)
    }

    public fun checkChainMapAxioms(degreeList: List<D>) {
        val failedDegreeList = mutableListOf<D>()
        for (degree in degreeList) {
            val nextDegree = this.source.degreeGroup.context.run { degree + 1 }
            val sourceDiff = this.source.underlyingDGVectorSpace.differential[degree]
            val targetDiff = this.target.underlyingDGVectorSpace.differential[degree]
            val f = this.underlyingDGLinearMap[degree]
            val g = this.underlyingDGLinearMap[nextDegree]
            if (g * sourceDiff != targetDiff * f) {
                failedDegreeList.add(degree)
            }
        }
        if (failedDegreeList.isNotEmpty()) {
            throw IllegalStateException(
                "$this does not commute with the differential at degree(s) $failedDegreeList"
            )
        }
    }

    public fun checkChainMapAxioms(minDegree: Int, maxDegree: Int) {
        val degreeList = (minDegree..maxDegree).map { this.source.degreeGroup.fromInt(it) }
        this.checkChainMapAxioms(degreeList)
    }

    public companion object {
        public operator fun <
            D : Degree,
            BA : BasisName,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: Complex<D, BA, BS, S, V, M>,
            target: Complex<D, BA, BT, S, V, M>,
            name: String,
            getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M>,
        ): ChainMap<D, BA, BS, BT, S, V, M> {
            return ChainMapImpl(source, target, name, getModuleMap)
        }
    }
}

private class ChainMapImpl<
    D : Degree,
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: Complex<D, BA, BS, S, V, M>,
    override val target: Complex<D, BA, BT, S, V, M>,
    override val name: String,
    getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M>,
) : ChainMap<D, BA, BS, BT, S, V, M> {
    private val _getModuleMap: (degree: D) -> ModuleMap<BA, BS, BT, S, V, M> = getModuleMap

    override fun getModuleMap(degree: D): ModuleMap<BA, BS, BT, S, V, M> {
        return this._getModuleMap(degree)
    }

    override val underlyingDGLinearMap: DGLinearMap<D, BS, BT, S, V, M> by lazy {
        DGLinearMap(
            source = this.source.underlyingDGVectorSpace,
            target = this.target.underlyingDGVectorSpace,
            degree = this.source.degreeGroup.zero,
            matrixSpace = this.source.matrixSpace,
            name = this.name,
        ) { degree ->
            this.getModuleMap(degree).underlyingLinearMap
        }
    }
}
