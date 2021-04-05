package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

open class MultipleVectorContext<S : Scalar, V : NumVector<S>>(
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
        throw IllegalArgumentException("Does not match any of the vector spaces")
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <X> Vector<X, S, V>.minus(other: Vector<X, S, V>): Vector<X, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw IllegalArgumentException("Cannot subtract vectors in different vector spaces")
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<X, S, V>
                return vectorSpace.subtract(this, other)
            }
        }
        throw IllegalArgumentException("Does not match any of the vector spaces")
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <X> Vector<X, S, V>.times(scalar: S): Vector<X, S, V> {
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<X, S, V>
                return vectorSpace.multiply(scalar, this)
            }
        }
        throw IllegalArgumentException("Does not match any of the vector spaces")
    }

    operator fun <X> S.times(vector: Vector<X, S, V>): Vector<X, S, V> = vector * this
    operator fun <X> Vector<X, S, V>.times(scalar: Int): Vector<X, S, V> = this * scalar.toScalar()
    operator fun <X> Int.times(vector: Vector<X, S, V>): Vector<X, S, V> = vector * this.toScalar()
    operator fun <X> Vector<X, S, V>.unaryMinus(): Vector<X, S, V> = this * (-1)
}
