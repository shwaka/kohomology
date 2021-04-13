package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

open class MultipleVectorContext<S : Scalar, V : NumVector<S>>(
    numVectorSpace: NumVectorSpace<S, V>,
    private val vectorSpaceList: List<VectorSpace<*, S, V>>
) : NumVectorContext<S, V>(numVectorSpace.field, numVectorSpace) {

    @Suppress("UNCHECKED_CAST")
    operator fun <B : BasisName> Vector<B, S, V>.plus(other: Vector<B, S, V>): Vector<B, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw IllegalContextException("Cannot add vectors in different vector spaces")
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<B, S, V>
                return vectorSpace.add(this, other)
            }
        }
        throw IllegalContextException("Does not match any of the vector spaces")
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <B : BasisName> Vector<B, S, V>.minus(other: Vector<B, S, V>): Vector<B, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw IllegalContextException("Cannot subtract vectors in different vector spaces")
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<B, S, V>
                return vectorSpace.subtract(this, other)
            }
        }
        throw IllegalContextException("Does not match any of the vector spaces")
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <B : BasisName> Vector<B, S, V>.times(scalar: S): Vector<B, S, V> {
        for (vectorSpace in this@MultipleVectorContext.vectorSpaceList) {
            if (vectorSpace == this.vectorSpace) {
                vectorSpace as VectorSpace<B, S, V>
                return vectorSpace.multiply(scalar, this)
            }
        }
        throw IllegalContextException("Does not match any of the vector spaces")
    }

    operator fun <B : BasisName> S.times(vector: Vector<B, S, V>): Vector<B, S, V> = vector * this
    operator fun <B : BasisName> Vector<B, S, V>.times(scalar: Int): Vector<B, S, V> = this * scalar.toScalar()
    operator fun <B : BasisName> Int.times(vector: Vector<B, S, V>): Vector<B, S, V> = vector * this.toScalar()
    operator fun <B : BasisName> Vector<B, S, V>.unaryMinus(): Vector<B, S, V> = this * (-1)
}
