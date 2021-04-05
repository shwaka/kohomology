package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations

class Vector<B, S : Scalar, V : NumVector<S>>(val numVector: V, val vectorSpace: VectorSpace<B, S, V>) {
    init {
        if (numVector.dim != vectorSpace.dim)
            throw IllegalArgumentException("Dimension of the numerical vector does not match the dimension of the vector space")
    }

    fun toNumVector(): V {
        return this.numVector
    }

    fun coeffOf(basisName: B): S {
        return this.vectorSpace.numVectorSpace.withContext {
            this@Vector.numVector[this@Vector.vectorSpace.indexOf(basisName)]
        }
    }

    fun isZero(): Boolean {
        return this.numVector.isZero()
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
        return this.numVector.field.withContext {
            val basisWithCoeff = coeffList.zip(basis).filter { (coeff, _) -> coeff != zero }
            if (basisWithCoeff.isEmpty()) {
                "0"
            } else {
                basisWithCoeff.joinToString(separator = " + ") { (coeff, basisElm) ->
                    if (coeff == one)
                        basisElm
                    else
                        "$coeff $basisElm"
                }
            }
        }
    }

    override fun toString(): String {
        return this.toString { it.toString() }
    }
}

interface VectorOperations<B, S : Scalar, V : NumVector<S>> {
    operator fun contains(vector: Vector<B, S, V>): Boolean
    fun add(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    fun subtract(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    fun multiply(scalar: S, vector: Vector<B, S, V>): Vector<B, S, V>
    val zeroVector: Vector<B, S, V>
}

class VectorContext<B, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    vectorOperations: VectorOperations<B, S, V>
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), VectorOperations<B, S, V> by vectorOperations {
    operator fun Vector<B, S, V>.plus(other: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.add(this, other)
    operator fun Vector<B, S, V>.minus(other: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.subtract(this, other)
    operator fun Vector<B, S, V>.times(scalar: S): Vector<B, S, V> = this@VectorContext.multiply(scalar, this)
    operator fun S.times(vector: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.multiply(this, vector)
    operator fun Vector<B, S, V>.times(scalar: Int): Vector<B, S, V> = this@VectorContext.multiply(scalar.toScalar(), this)
    operator fun Int.times(vector: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.multiply(this.toScalar(), vector)
    operator fun Vector<B, S, V>.unaryMinus(): Vector<B, S, V> = Vector(-this.numVector, this.vectorSpace)
}

open class VectorSpace<B, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    val basisNames: List<B>
) : VectorOperations<B, S, V> {
    val dim = basisNames.size
    val field = this.numVectorSpace.field

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    private val vectorContext by lazy {
        VectorContext(numVectorSpace.field, numVectorSpace, this)
    }
    fun <T> withContext(block: VectorContext<B, S, V>.() -> T) = this.vectorContext.block()

    override fun contains(vector: Vector<B, S, V>): Boolean {
        return vector.vectorSpace == this
    }

    override fun add(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        if (a !in this)
            throw ArithmeticException("The vector $a is not contained in the vector space $this")
        if (b !in this)
            throw ArithmeticException("The vector $b is not contained in the vector space $this")
        return numVectorSpace.withContext {
            Vector(a.numVector + b.numVector, this@VectorSpace)
        }
    }

    override fun subtract(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        if (a !in this)
            throw ArithmeticException("The vector $a is not contained in the vector space $this")
        if (b !in this)
            throw ArithmeticException("The vector $b is not contained in the vector space $this")
        return this.numVectorSpace.withContext {
            Vector(a.numVector - b.numVector, this@VectorSpace)
        }
    }

    override fun multiply(scalar: S, vector: Vector<B, S, V>): Vector<B, S, V> {
        if (scalar !in this.field)
            throw ArithmeticException("The scalar $scalar does not match the context (${this.field})")
        if (vector !in this)
            throw ArithmeticException("The vector $vector is not contained in the vector space $this")
        return this.numVectorSpace.withContext {
            Vector(vector.numVector * scalar, vector.vectorSpace)
        }
    }

    fun fromNumVector(numVector: V): Vector<B, S, V> {
        return Vector(numVector, this)
    }

    fun fromCoeff(coeffList: List<S>): Vector<B, S, V> {
        val numVector = this.numVectorSpace.fromValues(coeffList)
        return this.fromNumVector(numVector)
    }

    fun fromCoeff(vararg coeff: S): Vector<B, S, V> {
        return this.fromCoeff(coeff.toList())
    }

    fun fromBasisName(basisName: B): Vector<B, S, V> {
        val index = this.indexOf(basisName)
        val coeffList: List<S> = this.field.withContext {
            (0 until this@VectorSpace.dim).map { i ->
                if (i == index) one else zero
            }
        }
        return this.fromCoeff(coeffList)
    }

    fun fromBasisName(basisName: B, coeff: S): Vector<B, S, V> {
        return this.withContext { this@VectorSpace.fromBasisName(basisName) * coeff }
    }

    fun fromBasisName(basisName: B, coeff: Int): Vector<B, S, V> {
        val coeffScalar = this.withContext { coeff.toScalar() }
        return this.fromBasisName(basisName, coeffScalar)
    }

    override val zeroVector: Vector<B, S, V>
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

    fun <M : Matrix<S, V>> isBasis(
        vectorList: List<Vector<B, S, V>>,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        if (vectorList.size != this.dim) return false
        return matrixSpace.withContext {
            matrixSpace.fromNumVectors(vectorList.map { it.numVector }, this@VectorSpace.dim)
                .isInvertible()
        }
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

    override fun toString(): String {
        val basisNamesString = this.basisNames.joinToString(", ") { it.toString() }
        return "VectorSpace($basisNamesString)"
    }
}
