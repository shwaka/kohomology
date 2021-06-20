package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
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
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.PrintType
import com.github.shwaka.kohomology.vectsp.Printable
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace
import mu.KotlinLogging

sealed class GVectorOrZero<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> {
    abstract fun isZero(): Boolean
    fun isNotZero(): Boolean = !this.isZero()
}

class ZeroGVector<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> : GVectorOrZero<D, B, S, V>() {
    override fun isZero() = true
    override fun toString(): String {
        return "0"
    }
}

open class GVector<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>,
    val degree: D,
    val gVectorSpace: GVectorSpace<D, B, S, V>
) : GVectorOrZero<D, B, S, V>(), Printable {
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

    override fun toString(): String {
        return this.toString(PrintType.PLAIN)
    }

    override fun toString(printType: PrintType): String {
        return this.vector.print(this.gVectorSpace.getPrintConfig(printType))
    }
}

interface GVectorOperations<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> {
    operator fun contains(gVector: GVector<D, B, S, V>): Boolean
    fun add(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V>
    fun subtract(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V>
    fun multiply(scalar: S, gVector: GVector<D, B, S, V>): GVector<D, B, S, V>
    val zeroGVector: ZeroGVector<D, B, S, V>
}

open class GVectorContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), GVectorOperations<D, B, S, V> by gVectorOperations {
    operator fun GVector<D, B, S, V>.plus(other: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.add(this, other)
    operator fun GVectorOrZero<D, B, S, V>.plus(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> other
                is GVector -> this@GVectorContext.add(this, other)
            }
        }
    }

    operator fun GVector<D, B, S, V>.minus(other: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.subtract(this, other)
    operator fun GVectorOrZero<D, B, S, V>.minus(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> this@GVectorContext.multiply(this@GVectorContext.field.fromInt(-1), other)
                is GVector -> this@GVectorContext.subtract(this, other)
            }
        }
    }

    operator fun GVector<D, B, S, V>.times(scalar: S): GVector<D, B, S, V> = this@GVectorContext.multiply(scalar, this)
    operator fun S.times(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.multiply(this, gVector)
    operator fun GVector<D, B, S, V>.times(scalar: Int): GVector<D, B, S, V> = this@GVectorContext.multiply(scalar.toScalar(), this)
    operator fun Int.times(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.multiply(this.toScalar(), gVector)
    operator fun GVectorOrZero<D, B, S, V>.times(scalar: S): GVectorOrZero<D, B, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.zeroGVector
            is GVector -> this@GVectorContext.multiply(scalar, this)
        }
    }
    operator fun S.times(gVector: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> = gVector * this
    operator fun GVectorOrZero<D, B, S, V>.times(scalar: Int): GVectorOrZero<D, B, S, V> = this * scalar.toScalar()
    operator fun Int.times(gVector: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> = gVector * this

    operator fun GVector<D, B, S, V>.unaryMinus(): GVector<D, B, S, V> = this@GVectorContext.multiply((-1).toScalar(), this)
    operator fun GVectorOrZero<D, B, S, V>.unaryMinus(): GVectorOrZero<D, B, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.zeroGVector
            is GVector -> -this
        }
    }
}

open class GVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    open val degreeGroup: DegreeGroup<D>,
    val name: String,
    val listDegreesForAugmentedDegree: ((Int) -> List<D>)?,
    private val getVectorSpace: (D) -> VectorSpace<B, S, V>,
) : GVectorOperations<D, B, S, V> {
    constructor(
        numVectorSpace: NumVectorSpace<S, V>,
        degreeGroup: DegreeGroup<D>,
        name: String,
        getVectorSpace: (D) -> VectorSpace<B, S, V>,
    ) : this(numVectorSpace, degreeGroup, name, null, getVectorSpace)

    val field = this.numVectorSpace.field
    private val cache: MutableMap<D, VectorSpace<B, S, V>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    open val context by lazy { GVectorContext(numVectorSpace.field, numVectorSpace, this) }

    companion object {
        fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> fromBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getBasisNames: (D) -> List<B>,
        ): GVectorSpace<D, B, S, V> {
            return GVectorSpace<D, B, S, V>(numVectorSpace, degreeGroup, name) { degree -> VectorSpace<B, S, V>(numVectorSpace, getBasisNames(degree)) }
        }

        fun <D : Degree, S : Scalar, V : NumVector<S>> fromStringBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getBasisNames: (D) -> List<String>,
        ): GVectorSpace<D, StringBasisName, S, V> {
            // The following explicit type arguments cannot be removed in order to avoid freeze of Intellij Idea
            return GVectorSpace<D, StringBasisName, S, V>(numVectorSpace, degreeGroup, name) { degree ->
                val basisNames = getBasisNames(degree).map { StringBasisName(it) }
                VectorSpace<StringBasisName, S, V>(numVectorSpace, basisNames)
            }
        }

        fun <S : Scalar, V : NumVector<S>> fromStringBasisNamesWithIntDegree(
            numVectorSpace: NumVectorSpace<S, V>,
            name: String,
            getBasisNames: (Int) -> List<String>,
        ): GVectorSpace<IntDegree, StringBasisName, S, V> {
            // The following explicit type arguments cannot be removed in order to avoid freeze of Intellij Idea
            return GVectorSpace<IntDegree, StringBasisName, S, V>(numVectorSpace, IntDegreeGroup, name) { degree ->
                val basisNames = getBasisNames(degree.value).map { StringBasisName(it) }
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
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun fromVector(vector: Vector<B, S, V>, degree: D): GVector<D, B, S, V> {
        return GVector(vector, degree, this)
    }

    fun fromNumVector(numVector: V, degree: D): GVector<D, B, S, V> {
        val vectorSpace = this[degree]
        val vector = Vector(numVector, vectorSpace)
        return this.fromVector(vector, degree)
    }

    fun fromCoeff(coeff: List<S>, degree: D): GVector<D, B, S, V> {
        val numVector = this.numVectorSpace.fromValueList(coeff)
        return this.fromNumVector(numVector, degree)
    }

    fun fromBasisName(basisName: B, degree: D): GVector<D, B, S, V> {
        val vector = this[degree].fromBasisName(basisName)
        return this.fromVector(vector, degree)
    }

    fun fromBasisName(basisName: B, degree: D, coeff: S): GVector<D, B, S, V> {
        return this.context.run { this@GVectorSpace.fromBasisName(basisName, degree) * coeff }
    }

    fun fromBasisName(basisName: B, degree: D, coeff: Int): GVector<D, B, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, degree, coeffScalar)
    }

    fun getBasis(degree: D): List<GVector<D, B, S, V>> {
        return this[degree].getBasis().map { vector ->
            this.fromVector(vector, degree)
        }
    }

    fun getBasisForAugmentedDegree(augmentedDegree: Int): List<GVector<D, B, S, V>> {
        val listDegreesForAugmentedDegree: (Int) -> List<D> = this.listDegreesForAugmentedDegree
            ?: throw NotImplementedError(
                "GVectorSpace.getBasisForAugmentedDegree() cannot be called" +
                    "since the property listDegreesForAugmentedDegree is null"
            )
        // ↓local 変数に代入しておかないと、listDegreesForAugmentedDegree が nullable になってしまう
        return listDegreesForAugmentedDegree(augmentedDegree)
            .map { degree -> this.getBasis(degree) }
            .flatten()
    }

    fun getZero(degree: D): GVector<D, B, S, V> {
        val vector = this[degree].zeroVector
        return this.fromVector(vector, degree)
    }

    fun convertToGVector(gVectorOrZero: GVectorOrZero<D, B, S, V>, degree: D): GVector<D, B, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }

    operator fun get(degree: Int): VectorSpace<B, S, V> = this[this.degreeGroup.fromInt(degree)]
    fun fromVector(vector: Vector<B, S, V>, degree: Int): GVector<D, B, S, V> = this.fromVector(vector, this.degreeGroup.fromInt(degree))
    fun fromNumVector(numVector: V, degree: Int): GVector<D, B, S, V> = this.fromNumVector(numVector, this.degreeGroup.fromInt(degree))
    fun fromCoeff(coeff: List<S>, degree: Int): GVector<D, B, S, V> = this.fromCoeff(coeff, this.degreeGroup.fromInt(degree))
    fun fromBasisName(basisName: B, degree: Int): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree))
    fun fromBasisName(basisName: B, degree: Int, coeff: S): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree), coeff)
    fun fromBasisName(basisName: B, degree: Int, coeff: Int): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree), coeff)
    fun getBasis(degree: Int): List<GVector<D, B, S, V>> = this.getBasis(this.degreeGroup.fromInt(degree))
    fun getZero(degree: Int): GVector<D, B, S, V> = this.getZero(this.degreeGroup.fromInt(degree))
    fun convertToGVector(gVectorOrZero: GVectorOrZero<D, B, S, V>, degree: Int): GVector<D, B, S, V> = this.convertToGVector(gVectorOrZero, this.degreeGroup.fromInt(degree))

    override fun contains(gVector: GVector<D, B, S, V>): Boolean {
        return gVector.gVectorSpace == this
    }

    override fun add(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
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

    override fun subtract(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
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

    override fun multiply(scalar: S, gVector: GVector<D, B, S, V>): GVector<D, B, S, V> {
        if (gVector !in this)
            throw IllegalContextException("The gVector $gVector does not match the context")
        val vector = gVector.vector.vectorSpace.context.run { scalar * gVector.vector }
        return this.fromVector(vector, gVector.degree)
    }

    override val zeroGVector: ZeroGVector<D, B, S, V> = ZeroGVector()

    fun <M : Matrix<S, V>> isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
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
        gVectorList: List<GVector<D, B, S, V>>,
        degree: Int,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        return this.isBasis(gVectorList, this.degreeGroup.fromInt(degree), matrixSpace)
    }

    fun <M : Matrix<S, V>> getId(matrixSpace: MatrixSpace<S, V, M>): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap(this, this, 0, matrixSpace, "id") { degree ->
            this[degree].getId(matrixSpace)
        }
    }

    open fun getPrintConfig(printType: PrintType): PrintConfig<B, S> {
        return PrintConfig.default(printType)
    }

    override fun toString(): String {
        return this.name
    }
}
