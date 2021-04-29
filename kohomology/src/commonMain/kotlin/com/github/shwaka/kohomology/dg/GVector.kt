package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.DefaultVectorPrinter
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorPrinter
import com.github.shwaka.kohomology.vectsp.VectorSpace
import mu.KotlinLogging

sealed class GVectorOrZero<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>> {
    abstract fun isZero(): Boolean
    fun isNotZero(): Boolean = !this.isZero()
}

class ZeroGVector<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>> : GVectorOrZero<B, D, S, V>() {
    override fun isZero() = true
    override fun toString(): String {
        return "0"
    }
}

open class GVector<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>,
    val degree: D,
    val gVectorSpace: GVectorSpace<B, D, S, V>
) : GVectorOrZero<B, D, S, V>() {
    override fun isZero(): Boolean {
        return this.vector.isZero()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GVector<*, *, *, *>

        if (vector != other.vector) return false
        if (degree != other.degree) return false
        if (gVectorSpace != other.gVectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.hashCode()
        result = 31 * result + degree.hashCode()
        result = 31 * result + gVectorSpace.hashCode()
        return result
    }

    override fun toString(): String = this.gVectorSpace.printer.stringify(this.vector)
}

interface GVectorOperations<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>> {
    operator fun contains(gVector: GVector<B, D, S, V>): Boolean
    fun add(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V>
    fun subtract(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V>
    fun multiply(scalar: S, gVector: GVector<B, D, S, V>): GVector<B, D, S, V>
    val zeroGVector: ZeroGVector<B, D, S, V>
}

open class GVectorContext<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, D, S, V>,
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), GVectorOperations<B, D, S, V> by gVectorOperations {
    operator fun GVector<B, D, S, V>.plus(other: GVector<B, D, S, V>): GVector<B, D, S, V> = this@GVectorContext.add(this, other)
    operator fun GVectorOrZero<B, D, S, V>.plus(other: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> other
                is GVector -> this@GVectorContext.add(this, other)
            }
        }
    }

    operator fun GVector<B, D, S, V>.minus(other: GVector<B, D, S, V>): GVector<B, D, S, V> = this@GVectorContext.subtract(this, other)
    operator fun GVectorOrZero<B, D, S, V>.minus(other: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> this@GVectorContext.multiply(this@GVectorContext.field.fromInt(-1), other)
                is GVector -> this@GVectorContext.subtract(this, other)
            }
        }
    }

    operator fun GVector<B, D, S, V>.times(scalar: S): GVector<B, D, S, V> = this@GVectorContext.multiply(scalar, this)
    operator fun S.times(gVector: GVector<B, D, S, V>): GVector<B, D, S, V> = this@GVectorContext.multiply(this, gVector)
    operator fun GVector<B, D, S, V>.times(scalar: Int): GVector<B, D, S, V> = this@GVectorContext.multiply(scalar.toScalar(), this)
    operator fun Int.times(gVector: GVector<B, D, S, V>): GVector<B, D, S, V> = this@GVectorContext.multiply(this.toScalar(), gVector)
    operator fun GVectorOrZero<B, D, S, V>.times(scalar: S): GVectorOrZero<B, D, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.zeroGVector
            is GVector -> this@GVectorContext.multiply(scalar, this)
        }
    }
    operator fun S.times(gVector: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> = gVector * this
    operator fun GVectorOrZero<B, D, S, V>.times(scalar: Int): GVectorOrZero<B, D, S, V> = this * scalar.toScalar()
    operator fun Int.times(gVector: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> = gVector * this

    operator fun GVector<B, D, S, V>.unaryMinus(): GVector<B, D, S, V> = this@GVectorContext.multiply((-1).toScalar(), this)
    operator fun GVectorOrZero<B, D, S, V>.unaryMinus(): GVectorOrZero<B, D, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.zeroGVector
            is GVector -> -this
        }
    }
}

open class GVectorSpace<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    val degreeMonoid: DegreeMonoid<D>,
    val name: String,
    var printer: VectorPrinter<B, S, V>,
    private val getVectorSpace: (D) -> VectorSpace<B, S, V>,
) : GVectorOperations<B, D, S, V> {
    constructor(
        numVectorSpace: NumVectorSpace<S, V>,
        degreeMonoid: DegreeMonoid<D>,
        name: String,
        getVectorSpace: (D) -> VectorSpace<B, S, V>,
    ) : this(numVectorSpace, degreeMonoid, name, DefaultVectorPrinter(), getVectorSpace)

    val field = this.numVectorSpace.field
    private val cache: MutableMap<D, VectorSpace<B, S, V>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    open val context by lazy { GVectorContext(numVectorSpace.field, numVectorSpace, this) }

    companion object {
        fun <B : BasisName, D : Degree, S : Scalar, V : NumVector<S>> fromBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeMonoid: DegreeMonoid<D>,
            name: String,
            getBasisNames: (D) -> List<B>,
        ): GVectorSpace<B, D, S, V> {
            return GVectorSpace<B, D, S, V>(numVectorSpace, degreeMonoid, name) { degree -> VectorSpace<B, S, V>(numVectorSpace, getBasisNames(degree)) }
        }

        fun <D : Degree, S : Scalar, V : NumVector<S>> fromStringBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeMonoid: DegreeMonoid<D>,
            name: String,
            getBasisNames: (D) -> List<String>,
        ): GVectorSpace<StringBasisName, D, S, V> {
            // The following explicit type arguments cannot be removed in order to avoid freeze of Intellij Idea
            return GVectorSpace<StringBasisName, D, S, V>(numVectorSpace, degreeMonoid, name) { degree ->
                val basisNames = getBasisNames(degree).map { StringBasisName(it) }
                VectorSpace<StringBasisName, S, V>(numVectorSpace, basisNames)
            }
        }

        fun <S : Scalar, V : NumVector<S>> fromStringBasisNamesWithIntDegree(
            numVectorSpace: NumVectorSpace<S, V>,
            name: String,
            getBasisNames: (Int) -> List<String>,
        ): GVectorSpace<StringBasisName, IntDegree, S, V> {
            // The following explicit type arguments cannot be removed in order to avoid freeze of Intellij Idea
            return GVectorSpace<StringBasisName, IntDegree, S, V>(numVectorSpace, IntDegreeMonoid, name) { degree ->
                val basisNames = getBasisNames(degree.toInt()).map { StringBasisName(it) }
                VectorSpace<StringBasisName, S, V>(numVectorSpace, basisNames)
            }
        }
    }

    operator fun get(degree: D): VectorSpace<B, S, V> {
        this.cache[degree]?.let {
            // if cache exists
            this.logger.debug { "cache found for $this[$degree]" }
            return it
        }
        // if cache does not exist
        this.logger.debug { "cache not found for $this[$degree], create new instance" }
        val vectorSpace = this.getVectorSpace(degree)
        vectorSpace.printer = this.printer
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun fromVector(vector: Vector<B, S, V>, degree: D): GVector<B, D, S, V> {
        return GVector(vector, degree, this)
    }

    fun fromNumVector(numVector: V, degree: D): GVector<B, D, S, V> {
        val vectorSpace = this[degree]
        val vector = Vector(numVector, vectorSpace)
        return this.fromVector(vector, degree)
    }

    fun fromCoeff(coeff: List<S>, degree: D): GVector<B, D, S, V> {
        val numVector = this.numVectorSpace.fromValueList(coeff)
        return this.fromNumVector(numVector, degree)
    }

    fun fromBasisName(basisName: B, degree: D): GVector<B, D, S, V> {
        val vector = this[degree].fromBasisName(basisName)
        return this.fromVector(vector, degree)
    }

    fun fromBasisName(basisName: B, degree: D, coeff: S): GVector<B, D, S, V> {
        return this.context.run { this@GVectorSpace.fromBasisName(basisName, degree) * coeff }
    }

    fun fromBasisName(basisName: B, degree: D, coeff: Int): GVector<B, D, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, degree, coeffScalar)
    }

    fun getBasis(degree: D): List<GVector<B, D, S, V>> {
        return this[degree].getBasis().map { vector ->
            this.fromVector(vector, degree)
        }
    }

    fun getZero(degree: D): GVector<B, D, S, V> {
        val vector = this[degree].zeroVector
        return this.fromVector(vector, degree)
    }

    fun convertToGVector(gVectorOrZero: GVectorOrZero<B, D, S, V>, degree: D): GVector<B, D, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }

    operator fun get(degree: Int): VectorSpace<B, S, V> = this[this.degreeMonoid.fromInt(degree)]
    fun fromVector(vector: Vector<B, S, V>, degree: Int): GVector<B, D, S, V> = this.fromVector(vector, this.degreeMonoid.fromInt(degree))
    fun fromNumVector(numVector: V, degree: Int): GVector<B, D, S, V> = this.fromNumVector(numVector, this.degreeMonoid.fromInt(degree))
    fun fromCoeff(coeff: List<S>, degree: Int): GVector<B, D, S, V> = this.fromCoeff(coeff, this.degreeMonoid.fromInt(degree))
    fun fromBasisName(basisName: B, degree: Int): GVector<B, D, S, V> = this.fromBasisName(basisName, this.degreeMonoid.fromInt(degree))
    fun fromBasisName(basisName: B, degree: Int, coeff: S): GVector<B, D, S, V> = this.fromBasisName(basisName, this.degreeMonoid.fromInt(degree), coeff)
    fun fromBasisName(basisName: B, degree: Int, coeff: Int): GVector<B, D, S, V> = this.fromBasisName(basisName, this.degreeMonoid.fromInt(degree), coeff)
    fun getBasis(degree: Int): List<GVector<B, D, S, V>> = this.getBasis(this.degreeMonoid.fromInt(degree))
    fun getZero(degree: Int): GVector<B, D, S, V> = this.getZero(this.degreeMonoid.fromInt(degree))
    fun convertToGVector(gVectorOrZero: GVectorOrZero<B, D, S, V>, degree: Int): GVector<B, D, S, V> = this.convertToGVector(gVectorOrZero, this.degreeMonoid.fromInt(degree))

    override fun contains(gVector: GVector<B, D, S, V>): Boolean {
        return gVector.gVectorSpace == this
    }

    override fun add(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V> {
        if (a !in this)
            throw IllegalContextException("The gVector $a does not match the context")
        if (b !in this)
            throw IllegalContextException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.context.run {
            a.vector + b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun subtract(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V> {
        if (a !in this)
            throw IllegalContextException("The gVector $a does not match the context")
        if (b !in this)
            throw IllegalContextException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.context.run {
            a.vector - b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun multiply(scalar: S, gVector: GVector<B, D, S, V>): GVector<B, D, S, V> {
        if (gVector !in this)
            throw IllegalContextException("The gVector $gVector does not match the context")
        val vector = gVector.vector.vectorSpace.context.run { scalar * gVector.vector }
        return this.fromVector(vector, gVector.degree)
    }

    override val zeroGVector: ZeroGVector<B, D, S, V> = ZeroGVector()

    fun <M : Matrix<S, V>> isBasis(
        gVectorList: List<GVector<B, D, S, V>>,
        degree: D,
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

    fun <M : Matrix<S, V>> isBasis(
        gVectorList: List<GVector<B, D, S, V>>,
        degree: Int,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        return this.isBasis(gVectorList, this.degreeMonoid.fromInt(degree), matrixSpace)
    }

    fun <M : Matrix<S, V>> getId(matrixSpace: MatrixSpace<S, V, M>): GLinearMap<B, B, D, S, V, M> {
        return GLinearMap(this, this, 0, matrixSpace, "id") { degree ->
            this[degree].getId(matrixSpace)
        }
    }

    override fun toString(): String {
        return this.name
    }
}
