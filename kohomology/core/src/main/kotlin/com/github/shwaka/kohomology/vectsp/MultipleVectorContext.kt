package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

class MultipleVectorContext<S : Scalar<S>, V : NumVector<S, V>>(
    private val vectorSpaceList: List<VectorSpace<*, S, V>>
) {
    @Suppress("UNCHECKED_CAST")
    fun <X> add(vector1: Vector<X, S, V>, vector2: Vector<X, S, V>): Vector<X, S, V> {
        for (vectorSpace in this.vectorSpaceList) {
            if (vectorSpace == vector1.vectorSpace) {
                vectorSpace as VectorSpace<X, S, V>
                return vectorSpace.add(vector1, vector2)
            }
        }
        throw IllegalArgumentException("does not match any of the vector spaces")
    }
}
