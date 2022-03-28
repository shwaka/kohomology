package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.PrintType
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Sign

public interface BasisName : Printable {
    public override fun toString(printConfig: PrintConfig): String = this.toString()
}
public class StringBasisName(public val name: String, tex: String? = null) : BasisName {
    public val tex: String = tex ?: name

    override fun toString(): String = this.name
    override fun toString(printConfig: PrintConfig): String {
        return when (printConfig.printType) {
            PrintType.PLAIN -> this.name
            PrintType.TEX -> this.tex
        }
    }

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

public class Vector<B : BasisName, S : Scalar, V : NumVector<S>>(
    public val numVector: V,
    public val vectorSpace: VectorSpace<B, S, V>
) : Printable {
    init {
        if (numVector.dim != vectorSpace.dim)
            throw InvalidSizeException("Dimension of the numerical vector does not match the dimension of the vector space")
    }

    public fun toNumVector(): V {
        return this.numVector
    }

    public fun coeffOf(basisName: B): S {
        return this.vectorSpace.numVectorSpace.context.run {
            this@Vector.numVector[this@Vector.vectorSpace.indexOf(basisName)]
        }
    }

    public fun isZero(): Boolean {
        return this.numVector.isZero()
    }

    public fun toBasisMap(): Map<B, S> {
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
        return this.toString(PrintConfig())
    }

    override fun toString(printConfig: PrintConfig): String {
        val internalPrintConfig = this.vectorSpace.getInternalPrintConfig(printConfig)
        return this.print(printConfig, internalPrintConfig)
    }

    public fun print(printConfig: PrintConfig, internalPrintConfig: InternalPrintConfig<B, S>): String {
        val basisStringWithCoeff: List<Pair<S, String>> = run {
            val coeffList = this.numVector.toList()
            // val basis = vector.vectorSpace.basisNames.map(basisToString)
            val basisWithCoeff = coeffList.zip(this.vectorSpace.basisNames).filter { (coeff, _) -> coeff.isNotZero() }
            val sortedBasisWithCoeff = if (internalPrintConfig.basisComparator == null) {
                basisWithCoeff
            } else {
                basisWithCoeff.sortedWith(compareBy(internalPrintConfig.basisComparator) { it.second })
            }
            sortedBasisWithCoeff.map { (coeff, basisName) -> Pair(coeff, internalPrintConfig.basisToString(basisName)) }
        }
        return this.numVector.field.context.run {
            if (basisStringWithCoeff.isEmpty()) {
                "0"
            } else {
                var result = ""
                basisStringWithCoeff[0].let { (coeff, basisElm) ->
                    result += when (val coeffStr = internalPrintConfig.coeffToString(coeff, true)) {
                        "1" -> basisElm
                        "-1" -> "-${printConfig.afterSign}$basisElm"
                        else -> "$coeffStr${printConfig.afterCoeff}$basisElm"
                    }
                }
                result += basisStringWithCoeff.drop(1).joinToString(separator = "") { (coeff, basisElm) ->
                    val sign = if (coeff.isPrintedPositively()) "+" else "-"
                    val str = when (val coeffStr = internalPrintConfig.coeffToString(coeff, false)) {
                        "1" -> basisElm
                        else -> "$coeffStr${printConfig.afterCoeff}$basisElm"
                    }
                    "${printConfig.beforeSign}$sign${printConfig.afterSign}$str"
                }
                result
            }
        }
    }
}

public interface VectorOperations<B : BasisName, S : Scalar, V : NumVector<S>> {
    public operator fun contains(vector: Vector<B, S, V>): Boolean
    public fun add(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    public fun subtract(a: Vector<B, S, V>, b: Vector<B, S, V>): Vector<B, S, V>
    public fun multiply(scalar: S, vector: Vector<B, S, V>): Vector<B, S, V>
    public val zeroVector: Vector<B, S, V>
}

public class VectorContext<B : BasisName, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    vectorOperations: VectorOperations<B, S, V>
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), VectorOperations<B, S, V> by vectorOperations {
    public operator fun Vector<B, S, V>.plus(other: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.add(this, other)
    public operator fun Vector<B, S, V>.minus(other: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.subtract(this, other)
    public operator fun Vector<B, S, V>.times(scalar: S): Vector<B, S, V> = this@VectorContext.multiply(scalar, this)
    public operator fun S.times(vector: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.multiply(this, vector)
    public operator fun Vector<B, S, V>.times(scalar: Int): Vector<B, S, V> = this@VectorContext.multiply(scalar.toScalar(), this)
    public operator fun Int.times(vector: Vector<B, S, V>): Vector<B, S, V> = this@VectorContext.multiply(this.toScalar(), vector)
    public operator fun Vector<B, S, V>.times(sign: Sign): Vector<B, S, V> {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(vector: Vector<B, S, V>): Vector<B, S, V> = vector * this
    public operator fun Vector<B, S, V>.unaryMinus(): Vector<B, S, V> = Vector(-this.numVector, this.vectorSpace)
    public fun Iterable<Vector<B, S, V>>.sum(): Vector<B, S, V> = this.fold(zeroVector) { acc, v -> acc + v }
}

public open class VectorSpace<B : BasisName, S : Scalar, V : NumVector<S>>(
    public val numVectorSpace: NumVectorSpace<S, V>,
    public val basisNames: List<B>,
    public val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S> = InternalPrintConfig.Companion::default,
) : VectorOperations<B, S, V> {
    public companion object {
        public operator fun <S : Scalar, V : NumVector<S>> invoke(
            numVectorSpace: NumVectorSpace<S, V>,
            basisNames: List<String>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<StringBasisName, S> = InternalPrintConfig.Companion::default,
        ): VectorSpace<StringBasisName, S, V> {
            return VectorSpace(numVectorSpace, basisNames.map { StringBasisName(it) }, getInternalPrintConfig)
        }
    }

    public val dim: Int = basisNames.size
    public val field: Field<S> = this.numVectorSpace.field

    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    public val context: VectorContext<B, S, V> by lazy {
        VectorContext(numVectorSpace.field, numVectorSpace, this)
    }

    private val basisNameToIndex: Map<B, Int> by lazy {
        // cache for indexOf(basisName)
        this.basisNames.mapIndexed { index, basisName -> Pair(basisName, index) }.toMap()
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

    public fun fromNumVector(numVector: V): Vector<B, S, V> {
        return Vector(numVector, this)
    }

    public fun fromCoeffList(coeffList: List<S>): Vector<B, S, V> {
        val numVector = this.numVectorSpace.fromValueList(coeffList)
        return this.fromNumVector(numVector)
    }

    public fun fromCoeffMap(coeffMap: Map<Int, S>): Vector<B, S, V> {
        val numVector = this.numVectorSpace.fromValueMap(coeffMap, this.dim)
        return this.fromNumVector(numVector)
    }

    public fun fromBasisName(basisName: B): Vector<B, S, V> {
        val index = this.indexOf(basisName)
        val coeffMap: Map<Int, S> = mapOf(index to this.field.one)
        // directly call fromReducedValueMap since coeffMap is reduced (contains no zero)
        val numVector = this.numVectorSpace.fromReducedValueMap(coeffMap, this.dim)
        return this.fromNumVector(numVector)
    }

    public fun fromBasisName(basisName: B, coeff: S): Vector<B, S, V> {
        return this.context.run { this@VectorSpace.fromBasisName(basisName) * coeff }
    }

    public fun fromBasisName(basisName: B, coeff: Int): Vector<B, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, coeffScalar)
    }

    override val zeroVector: Vector<B, S, V>
        get() = Vector(this.numVectorSpace.getZero(this.dim), this)

    public fun getBasis(): List<Vector<B, S, V>> {
        val zero = this.field.zero
        val one = this.field.one
        return (0 until this.dim).map { i ->
            val coeff = (0 until this.dim).map { j -> if (i == j) one else zero }
            this.fromCoeffList(coeff)
        }
    }

    public fun indexOf(basisName: B): Int {
        return basisNameToIndex[basisName]
            ?: throw NoSuchElementException("$basisName is not a name of basis element of the vector space $this")
    }

    public fun <M : Matrix<S, V>> isBasis(
        vectorList: List<Vector<B, S, V>>,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        if (vectorList.size != this.dim) return false
        return matrixSpace.context.run {
            matrixSpace.fromNumVectorList(vectorList.map { it.numVector }, this@VectorSpace.dim)
                .isInvertible()
        }
    }

    public fun <M : Matrix<S, V>> getIdentity(matrixSpace: MatrixSpace<S, V, M>): LinearMap<B, B, S, V, M> {
        return LinearMap.getIdentity(this, matrixSpace)
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
