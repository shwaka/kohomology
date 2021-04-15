package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import mu.KotlinLogging

interface BasisName {
    fun toTex(): String = this.toString()
}
class StringBasisName(val name: String, tex: String? = null) : BasisName {
    val tex: String = tex ?: name

    override fun toString(): String = this.name
    override fun toTex(): String = this.tex

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StringBasisName

        if (name != other.name) return false
        if (tex != other.tex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tex.hashCode()
        return result
    }
}

class Vector<B : BasisName, S : Scalar, V : NumVector<S>>(val numVector: V, val vectorSpace: VectorSpace<B, S, V>) {
    init {
        if (numVector.dim != vectorSpace.dim)
            throw InvalidSizeException("Dimension of the numerical vector does not match the dimension of the vector space")
    }

    fun toNumVector(): V {
        return this.numVector
    }

    fun coeffOf(basisName: B): S {
        return this.vectorSpace.numVectorSpace.context.run {
            this@Vector.numVector[this@Vector.vectorSpace.indexOf(basisName)]
        }
    }

    fun isZero(): Boolean {
        return this.numVector.isZero()
    }

    fun toMap(): Map<B, S> {
        return this.numVector.toMap().mapKeys { (index, _) ->
            this.vectorSpace.basisNames[index]
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

    override fun toString(): String {
        return this.vectorSpace.printer.stringify(this)
    }
}

interface VectorPrinter<B : BasisName, S : Scalar, V : NumVector<S>> {
    fun stringify(vector: Vector<B, S, V>): String
}

class DefaultVectorPrinter<B : BasisName, S : Scalar, V : NumVector<S>> : VectorPrinter<B, S, V> {
    fun stringify(vector: Vector<B, S, V>, coeffToString: (S) -> String, coeffToStringWithoutSign: (S) -> String, basisToString: (B) -> String): String {
        val coeffList = vector.numVector.toList()
        val basis = vector.vectorSpace.basisNames.map(basisToString)
        return vector.numVector.field.context.run {
            val basisWithCoeff = coeffList.zip(basis).filter { (coeff, _) -> coeff.isNotZero() }
            if (basisWithCoeff.isEmpty()) {
                "0"
            } else {
                var result = ""
                basisWithCoeff[0].let { (coeff, basisElm) ->
                    result += when (val coeffStr = coeffToString(coeff)) {
                        "1" -> basisElm
                        "-1" -> "- $basisElm"
                        else -> "$coeffStr $basisElm"
                    }
                }
                result += basisWithCoeff.drop(1).joinToString(separator = "") { (coeff, basisElm) ->
                    val sign = if (coeff.isPrintedPositively()) "+" else "-"
                    val str = when (val coeffStr = coeffToStringWithoutSign(coeff)) {
                        "1" -> basisElm
                        else -> "$coeffStr $basisElm"
                    }
                    " $sign $str"
                }
                result
            }
        }
    }

    override fun stringify(vector: Vector<B, S, V>): String {
        val coeffToString: (S) -> String = { it.toString() }
        val coeffToStringWithoutSign: (S) -> String = { it.toStringWithoutSign() }
        val basisToString: (B) -> String = { it.toString() }
        return this.stringify(vector, coeffToString, coeffToStringWithoutSign, basisToString)
    }
}

class TexVectorPrinter<B : BasisName, S : Scalar, V : NumVector<S>> : VectorPrinter<B, S, V> {
    private val defaultVectorPrinter = DefaultVectorPrinter<B, S, V>()
    override fun stringify(vector: Vector<B, S, V>): String {
        val coeffToString: (S) -> String = { it.toTex() }
        val coeffToStringWithoutSign: (S) -> String = { it.toTexWithoutSign() }
        val basisToString: (B) -> String = { it.toTex() }
        return this.defaultVectorPrinter.stringify(vector, coeffToString, coeffToStringWithoutSign, basisToString)
    }
}

interface VectorOperations<B : BasisName, S : Scalar, V : NumVector<S>> {
    operator fun contains(vector: Vector<B, S, V>): Boolean
    fun add(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    fun subtract(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    fun multiply(scalar: S, vector: Vector<B, S, V>): Vector<B, S, V>
    val zeroVector: Vector<B, S, V>
}

class VectorContext<B : BasisName, S : Scalar, V : NumVector<S>>(
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

open class VectorSpace<B : BasisName, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    val basisNames: List<B>,
    var printer: VectorPrinter<B, S, V> = DefaultVectorPrinter()
) : VectorOperations<B, S, V> {
    companion object {
        operator fun <S : Scalar, V : NumVector<S>> invoke(
            numVectorSpace: NumVectorSpace<S, V>,
            basisNames: List<String>,
            printer: VectorPrinter<StringBasisName, S, V> = DefaultVectorPrinter()
        ): VectorSpace<StringBasisName, S, V> {
            return VectorSpace(numVectorSpace, basisNames.map { StringBasisName(it) }, printer)
        }
    }

    val dim = basisNames.size
    val field = this.numVectorSpace.field

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    val context by lazy {
        VectorContext(numVectorSpace.field, numVectorSpace, this)
    }

    private val logger = KotlinLogging.logger {}

    init {
        this.logger.debug { "$this is created" }
    }

    override fun contains(vector: Vector<B, S, V>): Boolean {
        return vector.vectorSpace == this
    }

    override fun add(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        if (a !in this)
            throw IllegalContextException("The vector $a is not contained in the vector space $this")
        if (b !in this)
            throw IllegalContextException("The vector $b is not contained in the vector space $this")
        return numVectorSpace.context.run {
            Vector(a.numVector + b.numVector, this@VectorSpace)
        }
    }

    override fun subtract(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V> {
        if (a !in this)
            throw IllegalContextException("The vector $a is not contained in the vector space $this")
        if (b !in this)
            throw IllegalContextException("The vector $b is not contained in the vector space $this")
        return this.numVectorSpace.context.run {
            Vector(a.numVector - b.numVector, this@VectorSpace)
        }
    }

    override fun multiply(scalar: S, vector: Vector<B, S, V>): Vector<B, S, V> {
        if (scalar !in this.field)
            throw IllegalContextException("The scalar $scalar does not match the context (${this.field})")
        if (vector !in this)
            throw IllegalContextException("The vector $vector is not contained in the vector space $this")
        return this.numVectorSpace.context.run {
            Vector(vector.numVector * scalar, vector.vectorSpace)
        }
    }

    fun fromNumVector(numVector: V): Vector<B, S, V> {
        return Vector(numVector, this)
    }

    fun fromCoeff(coeffList: List<S>): Vector<B, S, V> {
        val numVector = this.numVectorSpace.fromValueList(coeffList)
        return this.fromNumVector(numVector)
    }

    fun fromCoeff(vararg coeff: S): Vector<B, S, V> {
        return this.fromCoeff(coeff.toList())
    }

    fun fromBasisName(basisName: B): Vector<B, S, V> {
        val index = this.indexOf(basisName)
        val coeffList: List<S> = this.field.context.run {
            (0 until this@VectorSpace.dim).map { i ->
                if (i == index) one else zero
            }
        }
        return this.fromCoeff(coeffList)
    }

    fun fromBasisName(basisName: B, coeff: S): Vector<B, S, V> {
        return this.context.run { this@VectorSpace.fromBasisName(basisName) * coeff }
    }

    fun fromBasisName(basisName: B, coeff: Int): Vector<B, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, coeffScalar)
    }

    override val zeroVector: Vector<B, S, V>
        get() = Vector(this.numVectorSpace.getZero(this.dim), this)

    fun getBasis(): List<Vector<B, S, V>> {
        val zero = this.field.zero
        val one = this.field.one
        return (0 until this.dim).map { i ->
            val coeff = (0 until this.dim).map { j -> if (i == j) one else zero }
            this.fromCoeff(coeff)
        }
    }

    fun indexOf(basisName: B): Int {
        val index = this.basisNames.indexOf(basisName)
        if (index == -1)
            throw NoSuchElementException("$basisName is not a name of basis element of this vector space")
        return index
    }

    fun <M : Matrix<S, V>> isBasis(
        vectorList: List<Vector<B, S, V>>,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        if (vectorList.size != this.dim) return false
        return matrixSpace.context.run {
            matrixSpace.fromNumVectorList(vectorList.map { it.numVector }, this@VectorSpace.dim)
                .isInvertible()
        }
    }

    fun <M : Matrix<S, V>> getId(matrixSpace: MatrixSpace<S, V, M>): LinearMap<B, B, S, V, M> {
        return LinearMap.getId(this, matrixSpace)
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
