package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.LinearMap

class GLinearMap<BS, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val source: GVectorSpace<BS, S, V>,
    val target: GVectorSpace<BT, S, V>,
    val degree: Degree,
    val getLinearMap: (Degree) -> LinearMap<BS, BT, S, V, M>
) {
    operator fun invoke(gVector: GVector<BS, S, V>): GVector<BT, S, V> {
        if (gVector.gVectorSpace != this.source)
            throw IllegalArgumentException("Invalid graded vector is given as an argument for a graded linear map")
        val linearMap = this.getLinearMap(gVector.degree)
        if (gVector.vector.vectorSpace != linearMap.source)
            throw Exception("Graded linear map contains a bug: getLinearMap returns incorrect linear map")
        val newVector = linearMap(gVector.vector)
        val newDegree = gVector.degree + this.degree
        return this.target.fromVector(newVector, newDegree)
    }

    companion object {
        fun <BS, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GVectorSpace<BS, S, V>,
            target: GVectorSpace<BT, S, V>,
            degree: Degree,
            matrixSpace: MatrixSpace<S, V, M>,
            getGVectors: (Degree) -> List<GVector<BT, S, V>>
        ): GLinearMap<BS, BT, S, V, M> {
            val getLinearMap: (Degree) -> LinearMap<BS, BT, S, V, M> = { k ->
                val sourceVectorSpace = source[k]
                val targetVectorSpace = target[k + degree]
                val gVectorValueList = getGVectors(k)
                if (gVectorValueList.any { it !in target })
                    throw IllegalArgumentException("The value list contains an element not contained in $target")
                if (gVectorValueList.any { it.degree != k + degree })
                    throw IllegalArgumentException("The value list contains an element with wrong degree")
                if (sourceVectorSpace.basisNames.size != gVectorValueList.size)
                    throw IllegalArgumentException("The value list has incompatible size")
                val valueList = gVectorValueList.map { it.vector }
                LinearMap.fromVectors(sourceVectorSpace, targetVectorSpace, matrixSpace, valueList)
            }
            return GLinearMap(source, target, degree, getLinearMap)
        }
    }
}
