package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

sealed class GVectorOrZero<B, S : Scalar, V : NumVector<S>> {
    abstract fun isZero(): Boolean
}

class ZeroGVector<B, S : Scalar, V : NumVector<S>> : GVectorOrZero<B, S, V>() {
    override fun isZero() = true
}

open class GVector<B, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>,
    val degree: Degree,
    val gVectorSpace: GVectorSpace<B, S, V>
) : GVectorOrZero<B, S, V>() {
    override fun isZero(): Boolean {
        return this.vector.isZero()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GVector<*, *, *>

        if (vector != other.vector) return false
        if (degree != other.degree) return false
        if (gVectorSpace != other.gVectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.hashCode()
        result = 31 * result + degree
        result = 31 * result + gVectorSpace.hashCode()
        return result
    }

    override fun toString(): String = this.vector.toString()

    fun toString(basisToString: (B) -> String): String = this.vector.toString(basisToString)
}

interface GVectorOperations<B, S : Scalar, V : NumVector<S>> {
    operator fun contains(gVector: GVector<B, S, V>): Boolean
    fun add(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
    fun subtract(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
    fun multiply(scalar: S, gVector: GVector<B, S, V>): GVector<B, S, V>
    val zeroGVector: ZeroGVector<B, S, V>
}

open class GVectorContext<B, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), GVectorOperations<B, S, V> by gVectorOperations {
    operator fun GVector<B, S, V>.plus(other: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.add(this, other)
    operator fun GVector<B, S, V>.minus(other: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.subtract(this, other)
    operator fun GVector<B, S, V>.times(scalar: S): GVector<B, S, V> = this@GVectorContext.multiply(scalar, this)
    operator fun S.times(gVector: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.multiply(this, gVector)
    operator fun GVector<B, S, V>.times(scalar: Int): GVector<B, S, V> = this@GVectorContext.multiply(scalar.toScalar(), this)
    operator fun Int.times(gVector: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.multiply(this.toScalar(), gVector)
    operator fun GVector<B, S, V>.unaryMinus(): GVector<B, S, V> = this@GVectorContext.multiply((-1).toScalar(), this)
}

open class GVectorSpace<B, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    private val getVectorSpace: (Degree) -> VectorSpace<B, S, V>
) : GVectorOperations<B, S, V> {
    val field = this.numVectorSpace.field
    private val cache: MutableMap<Degree, VectorSpace<B, S, V>> = mutableMapOf()

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    open val context by lazy { GVectorContext(numVectorSpace.field, numVectorSpace, this) }

    companion object {
        fun <B, S : Scalar, V : NumVector<S>> fromBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            getBasisNames: (Degree) -> List<B>,
        ): GVectorSpace<B, S, V> {
            return GVectorSpace<B, S, V>(numVectorSpace) { degree -> VectorSpace<B, S, V>(numVectorSpace, getBasisNames(degree)) }
        }
    }

    operator fun get(degree: Degree): VectorSpace<B, S, V> {
        // if cache exists
        this.cache[degree]?.let { return it }
        // if cache does not exist
        val vectorSpace = this.getVectorSpace(degree)
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun fromVector(vector: Vector<B, S, V>, degree: Degree): GVector<B, S, V> {
        return GVector(vector, degree, this)
    }

    fun fromNumVector(numVector: V, degree: Degree): GVector<B, S, V> {
        val vectorSpace = this[degree]
        val vector = Vector(numVector, vectorSpace)
        return this.fromVector(vector, degree)
    }

    fun fromCoeff(coeff: List<S>, degree: Degree): GVector<B, S, V> {
        val numVector = this.numVectorSpace.fromValues(coeff)
        return this.fromNumVector(numVector, degree)
    }

    fun fromBasisName(basisName: B, degree: Degree): GVector<B, S, V> {
        val vector = this[degree].fromBasisName(basisName)
        return this.fromVector(vector, degree)
    }

    fun fromBasisName(basisName: B, degree: Degree, coeff: S): GVector<B, S, V> {
        return this.context.run { this@GVectorSpace.fromBasisName(basisName, degree) * coeff }
    }

    fun fromBasisName(basisName: B, degree: Degree, coeff: Int): GVector<B, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, degree, coeffScalar)
    }

    fun getBasis(degree: Degree): List<GVector<B, S, V>> {
        return this[degree].getBasis().map { vector ->
            this.fromVector(vector, degree)
        }
    }

    fun getZero(degree: Degree): GVector<B, S, V> {
        val vector = this[degree].zeroVector
        return this.fromVector(vector, degree)
    }

    fun convertToGVector(gVectorOrZero: GVectorOrZero<B, S, V>, degree: Degree): GVector<B, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }

    override fun contains(gVector: GVector<B, S, V>): Boolean {
        return gVector.gVectorSpace == this
    }

    override fun add(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        if (a !in this)
            throw ArithmeticException("The gVector $a does not match the context")
        if (b !in this)
            throw ArithmeticException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.context.run {
            a.vector + b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun subtract(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        if (a !in this)
            throw ArithmeticException("The gVector $a does not match the context")
        if (b !in this)
            throw ArithmeticException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.context.run {
            a.vector - b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun multiply(scalar: S, gVector: GVector<B, S, V>): GVector<B, S, V> {
        if (gVector !in this)
            throw ArithmeticException("The gVector $gVector does not match the context")
        val vector = gVector.vector.vectorSpace.context.run { scalar * gVector.vector }
        return this.fromVector(vector, gVector.degree)
    }

    override val zeroGVector: ZeroGVector<B, S, V> = ZeroGVector()

    fun <M : Matrix<S, V>> isBasis(
        gVectorList: List<GVector<B, S, V>>,
        degree: Degree,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        for (gVector in gVectorList) {
            if (gVector.degree != degree)
                throw IllegalArgumentException("The degree of $gVector is not equal to the given degree $degree")
        }
        val vectorSpace = this[degree]
        val vectorList = gVectorList.map { it.vector }
        return vectorSpace.isBasis(vectorList, matrixSpace)
    }
}
