package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

class MultipleVectorContext<S : Scalar<S>, V : NumVector<S, V>>(
    numVectorSpace: NumVectorSpace<S, V>,
    private val vectorSpaceList: List<VectorSpace<*, S, V>>
) : NumVectorContext<S, V>(numVectorSpace.field, numVectorSpace) {

    @Suppress("UNCHECKED_CAST")
    operator fun <X> Vector<X, S, V>.plus(other: Vector<X, S, V>): Vector<X, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw IllegalArgumentException("Cannot add vectors in different vector spaces")
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<X, S, V>
                return vectorSpace.add(this, other)
            }
        }
        throw IllegalArgumentException("does not match any of the vector spaces")
    }
}
