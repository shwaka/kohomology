package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorSpace
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
}
