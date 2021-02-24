package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace

class Vector<B, S : Scalar<S>, V : NumVector<S, V>>(val numVector: V, val vectorSpace: VectorSpace<B, S, V>) {
    init {
        if (numVector.dim != vectorSpace.dim)
            throw IllegalArgumentException("Dimension of the numerical vector does not match the dimension of the vector space")
    }

    operator fun plus(other: Vector<B, S, V>): Vector<B, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw ArithmeticException("Cannot add two vectors in different vector spaces")
        return this.vectorSpace.numVectorSpace.withContext {
            Vector(this@Vector.numVector + other.numVector, this@Vector.vectorSpace)
        }
    }

    operator fun minus(other: Vector<B, S, V>): Vector<B, S, V> {
        if (this.vectorSpace != other.vectorSpace)
            throw ArithmeticException("Cannot subtract two vectors in different vector spaces")
        return this.vectorSpace.numVectorSpace.withContext {
            Vector(this@Vector.numVector - other.numVector, this@Vector.vectorSpace)
        }
    }

    operator fun unaryMinus(): Vector<B, S, V> {
        return this.vectorSpace.numVectorSpace.withContext {
            Vector(-this@Vector.numVector, this@Vector.vectorSpace)
        }
    }

    operator fun times(scalar: S): Vector<B, S, V> {
        return this.vectorSpace.numVectorSpace.withContext {
            Vector(this@Vector.numVector * scalar, this@Vector.vectorSpace)
        }
    }

    operator fun times(scalar: Int): Vector<B, S, V> {
        return this.vectorSpace.numVectorSpace.withContext {
            Vector(this@Vector.numVector * scalar, this@Vector.vectorSpace)
        }
    }

    fun toNumVector(): V {
        return this.numVector
    }

    fun coeffOf(basisName: B): S {
        return this.vectorSpace.numVectorSpace.withContext {
            this@Vector.numVector[this@Vector.vectorSpace.indexOf(basisName)]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Vector<*, *, *>

        if (numVector != other.numVector) return false
        if (vectorSpace != other.vectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVector.hashCode()
        result = 31 * result + vectorSpace.hashCode()
        return result
    }

    fun toString(basisToString: (B) -> String): String {
        val coeffList = this.vectorSpace.numVectorSpace.withContext {
            this@Vector.numVector.toList()
        }
        val basis = this.vectorSpace.basisNames.map(basisToString)
        return coeffList.zip(basis).joinToString(separator = " + ") { (coeff, basisElm) -> "$coeff $basisElm" }
    }

    override fun toString(): String {
        return this.toString { it.toString() }
    }
}

operator fun <B, S : Scalar<S>, V : NumVector<S, V>> Int.times(vector: Vector<B, S, V>): Vector<B, S, V> {
    return vector * this
}

operator fun <B, S : Scalar<S>, V : NumVector<S, V>> S.times(vector: Vector<B, S, V>): Vector<B, S, V> {
    return vector * this
}

class VectorSpace<B, S : Scalar<S>, V : NumVector<S, V>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    val basisNames: List<B>
) {
    val dim = basisNames.size
    val field = this.numVectorSpace.field

    fun fromNumVector(numVector: V): Vector<B, S, V> {
        return Vector(numVector, this)
    }

    fun fromCoeff(coeff: List<S>): Vector<B, S, V> {
        val numVector = this.numVectorSpace.fromValues(coeff)
        return this.fromNumVector(numVector)
    }

    fun fromCoeff(vararg coeff: S): Vector<B, S, V> {
        return this.fromCoeff(coeff.toList())
    }

    val zero: Vector<B, S, V>
        get() = Vector(this.numVectorSpace.getZero(this.dim), this)

    fun getBasis(): List<Vector<B, S, V>> {
        val zero = this.field.withContext { zero }
        val one = this.field.withContext { one }
        return (0 until this.dim).map { i ->
            val coeff = (0 until this.dim).map { j -> if (i == j) one else zero }
            this.fromCoeff(coeff)
        }
    }

    fun indexOf(basisName: B): Int {
        val index = this.basisNames.indexOf(basisName)
        if (index == -1)
            throw Exception("$basisName is not a name of basis element of this vector space")
        return index
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as VectorSpace<*, *, *>

        if (numVectorSpace != other.numVectorSpace) return false
        if (dim != other.dim) return false
        if (basisNames != other.basisNames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVectorSpace.hashCode()
        result = 31 * result + dim
        result = 31 * result + basisNames.hashCode()
        return result
    }
}
