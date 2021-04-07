package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

class DGLinearMap<BS, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val source: DGVectorSpace<BS, S, V, M>,
    val target: DGVectorSpace<BT, S, V, M>,
    val gLinearMap: GLinearMap<BS, BT, S, V, M>,
) {
    val degree = this.gLinearMap.degree
    val matrixSpace = this.source.matrixSpace
    init {
        if (this.source.gVectorSpace != gLinearMap.source)
            throw IllegalArgumentException("The source DGVectorSpace does not match to the source GVectorSpace of GLinearMap")
        if (this.target.gVectorSpace != gLinearMap.target)
            throw IllegalArgumentException("The target DGVectorSpace does not match to the target GVectorSpace of GLinearMap")
    }

    operator fun invoke(gVector: GVector<BS, S, V>): GVector<BT, S, V> {
        return this.gLinearMap(gVector)
    }

    fun inducedMapOnCohomology(): GLinearMap<SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        val getGVectors: (Degree) -> List<GVector<SubQuotBasis<BT, S, V>, S, V>> = { k ->
            this.source.cohomology.getBasis(k).map { cohomologyClass ->
                val cocycle = this.source.cocycleRepresentativeOf(cohomologyClass)
                this.target.cohomologyClassOf(this.gLinearMap(cocycle))
            }
        }
        return GLinearMap.fromGVectors(
            this.source.cohomology,
            this.target.cohomology,
            this.degree,
            this.matrixSpace,
            getGVectors
        )
    }

    companion object {
        operator fun <BS, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGVectorSpace<BS, S, V, M>,
            target: DGVectorSpace<BT, S, V, M>,
            degree: Degree,
            getLinearMap: (Degree) -> LinearMap<BS, BT, S, V, M>
        ): DGLinearMap<BS, BT, S, V, M> {
            val gLinearMap = GLinearMap(source.gVectorSpace, target.gVectorSpace, degree, getLinearMap)
            return DGLinearMap(source, target, gLinearMap)
        }

        fun <BS, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGVectorSpace<BS, S, V, M>,
            target: DGVectorSpace<BT, S, V, M>,
            degree: Degree,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (Degree) -> List<GVector<BT, S, V>>
        ): DGLinearMap<BS, BT, S, V, M> {
            val gLinearMap = GLinearMap.fromGVectors(source.gVectorSpace, target.gVectorSpace, degree, matrixSpace, getGVectors)
            return DGLinearMap(source, target, gLinearMap)
        }
    }
}
