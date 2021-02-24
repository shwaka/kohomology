package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

class GLinearMap<B0, B1, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    val source: GVectorSpace<B0, S, V>,
    val target: GVectorSpace<B1, S, V>,
    val degree: Degree,
    val getLinearMap: (Degree) -> LinearMap<B0, B1, S, V, M>
) {
    operator fun invoke(gVector: GVector<B0, S, V>): GVector<B1, S, V> {
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
