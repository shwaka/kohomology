package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorContextImpl
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.Printable
import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public sealed class GVectorOrZero<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> {
    public abstract fun isZero(): Boolean
    public fun isNotZero(): Boolean = !this.isZero()
}

public class ZeroGVector<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> : GVectorOrZero<D, B, S, V>() {
    override fun isZero(): Boolean = true
    override fun toString(): String {
        return "0"
    }
}

public open class GVector<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    public val vector: Vector<B, S, V>,
    public val degree: D,
    public val gVectorSpace: GVectorSpace<D, B, S, V>
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
        return this.toString(PrintConfig())
    }

    override fun toString(printConfig: PrintConfig): String {
        val internalPrintConfig = this.gVectorSpace.getInternalPrintConfig(printConfig)
        return this.vector.print(printConfig, internalPrintConfig)
    }
}

public interface GVectorContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> :
    NumVectorContext<S, V> {
    public val gVectorSpace: GVectorSpace<D, B, S, V>

    public operator fun GVector<D, B, S, V>.plus(other: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.add(this, other)
    public operator fun GVectorOrZero<D, B, S, V>.plus(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> other
                is GVector -> this@GVectorContext.gVectorSpace.add(this, other)
            }
        }
    }

    public operator fun GVector<D, B, S, V>.minus(other: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.subtract(this, other)
    public operator fun GVectorOrZero<D, B, S, V>.minus(other: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (other) {
            is ZeroGVector -> this
            is GVector -> when (this) {
                is ZeroGVector -> this@GVectorContext.gVectorSpace.multiply(this@GVectorContext.gVectorSpace.field.fromInt(-1), other)
                is GVector -> this@GVectorContext.gVectorSpace.subtract(this, other)
            }
        }
    }

    public operator fun GVector<D, B, S, V>.times(scalar: S): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.multiply(scalar, this)
    public operator fun S.times(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.multiply(this, gVector)
    public operator fun GVector<D, B, S, V>.times(scalar: Int): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.multiply(scalar.toScalar(), this)
    public operator fun Int.times(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.multiply(this.toScalar(), gVector)
    public operator fun GVectorOrZero<D, B, S, V>.times(scalar: S): GVectorOrZero<D, B, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.gVectorSpace.zeroGVector
            is GVector -> this@GVectorContext.gVectorSpace.multiply(scalar, this)
        }
    }
    public operator fun S.times(gVector: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> = gVector * this
    public operator fun GVectorOrZero<D, B, S, V>.times(scalar: Int): GVectorOrZero<D, B, S, V> = this * scalar.toScalar()
    public operator fun Int.times(gVector: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> = gVector * this

    public operator fun GVector<D, B, S, V>.times(sign: Sign): GVector<D, B, S, V> {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun GVectorOrZero<D, B, S, V>.times(sign: Sign): GVectorOrZero<D, B, S, V> {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return gVector * this
    }
    public operator fun Sign.times(gVector: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return gVector * this
    }

    public operator fun GVector<D, B, S, V>.unaryMinus(): GVector<D, B, S, V> = this@GVectorContext.gVectorSpace.multiply((-1).toScalar(), this)
    public operator fun GVectorOrZero<D, B, S, V>.unaryMinus(): GVectorOrZero<D, B, S, V> {
        return when (this) {
            is ZeroGVector -> this@GVectorContext.gVectorSpace.zeroGVector
            is GVector -> -this
        }
    }
    public fun Iterable<GVector<D, B, S, V>>.sum(degree: D? = null): GVector<D, B, S, V> {
        return if (this.any()) {
            this.reduce { v, w -> v + w }
        } else {
            if (degree == null)
                throw IllegalArgumentException("degree cannot be null when an iterator of GVector is empty")
            return this@GVectorContext.gVectorSpace.getZero(degree)
        }
    }
    public fun Iterable<GVector<D, B, S, V>>.sum(degree: Int): GVector<D, B, S, V> {
        val degreeInternal = this@GVectorContext.gVectorSpace.degreeGroup.fromInt(degree)
        return this.sum(degreeInternal)
    }
}

internal class GVectorContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    override val gVectorSpace: GVectorSpace<D, B, S, V>,
) : GVectorContext<D, B, S, V>, NumVectorContext<S, V> by NumVectorContextImpl(gVectorSpace.numVectorSpace)

public interface GVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> {
    public val numVectorSpace: NumVectorSpace<S, V>
    public val degreeGroup: DegreeGroup<D>
    public val name: String
    public val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>
    public val listDegreesForAugmentedDegree: ((Int) -> List<D>)?
    public operator fun get(degree: D): VectorSpace<B, S, V>

    public val field: Field<S> get() = this.numVectorSpace.field
    public val context: GVectorContext<D, B, S, V>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> invoke(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> VectorSpace<B, S, V>,
        ): GVectorSpace<D, B, S, V> {
            return GVectorSpaceImpl(numVectorSpace, degreeGroup, name, InternalPrintConfig.Companion::default, null, getVectorSpace)
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> invoke(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)?,
            getVectorSpace: (D) -> VectorSpace<B, S, V>,
        ): GVectorSpace<D, B, S, V> {
            return GVectorSpaceImpl(numVectorSpace, degreeGroup, name, InternalPrintConfig.Companion::default, listDegreesForAugmentedDegree, getVectorSpace)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>> fromBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getBasisNames: (D) -> List<B>,
        ): GVectorSpace<D, B, S, V> {
            return GVectorSpace<D, B, S, V>(numVectorSpace, degreeGroup, name) { degree -> VectorSpace<B, S, V>(numVectorSpace, getBasisNames(degree)) }
        }

        public fun <B : BasisName, S : Scalar, V : NumVector<S>> fromBasisNames(
            numVectorSpace: NumVectorSpace<S, V>,
            name: String,
            getBasisNames: (Int) -> List<B>,
        ): GVectorSpace<IntDegree, B, S, V> {
            return GVectorSpace<IntDegree, B, S, V>(numVectorSpace, IntDegreeGroup, name) { degree -> VectorSpace<B, S, V>(numVectorSpace, getBasisNames(degree.value)) }
        }

        public fun <D : Degree, S : Scalar, V : NumVector<S>> fromStringBasisNames(
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

        public fun <S : Scalar, V : NumVector<S>> fromStringBasisNamesWithIntDegree(
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

    public fun fromVector(vector: Vector<B, S, V>, degree: D): GVector<D, B, S, V> {
        return GVector(vector, degree, this)
    }

    public fun fromNumVector(numVector: V, degree: D): GVector<D, B, S, V> {
        val vectorSpace = this[degree]
        val vector = Vector(numVector, vectorSpace)
        return this.fromVector(vector, degree)
    }

    public fun fromCoeffList(coeffList: List<S>, degree: D): GVector<D, B, S, V> {
        val vector = this[degree].fromCoeffList(coeffList)
        return this.fromVector(vector, degree)
    }

    public fun fromCoeffMap(coeffMap: Map<Int, S>, degree: D): GVector<D, B, S, V> {
        val vector = this[degree].fromCoeffMap(coeffMap)
        return this.fromVector(vector, degree)
    }

    public fun fromBasisName(basisName: B, degree: D): GVector<D, B, S, V> {
        val vector = this[degree].fromBasisName(basisName)
        return this.fromVector(vector, degree)
    }

    public fun fromBasisName(basisName: B, degree: D, coeff: S): GVector<D, B, S, V> {
        return this.context.run { this@GVectorSpace.fromBasisName(basisName, degree) * coeff }
    }

    public fun fromBasisName(basisName: B, degree: D, coeff: Int): GVector<D, B, S, V> {
        val coeffScalar = this.context.run { coeff.toScalar() }
        return this.fromBasisName(basisName, degree, coeffScalar)
    }

    public fun getBasis(degree: D): List<GVector<D, B, S, V>> {
        return this[degree].getBasis().map { vector ->
            this.fromVector(vector, degree)
        }
    }

    public fun getBasisForAugmentedDegree(augmentedDegree: Int): List<GVector<D, B, S, V>> {
        val listDegreesForAugmentedDegree: (Int) -> List<D> = this.listDegreesForAugmentedDegree
            ?: throw NotImplementedError(
                "GVectorSpace.getBasisForAugmentedDegree() cannot be called " +
                    "since the property listDegreesForAugmentedDegree is null"
            )
        // ↓local 変数に代入しておかないと、listDegreesForAugmentedDegree が nullable になってしまう
        return listDegreesForAugmentedDegree(augmentedDegree)
            .map { degree -> this.getBasis(degree) }
            .flatten()
    }

    public fun getZero(degree: D): GVector<D, B, S, V> {
        val vector = this[degree].zeroVector
        return this.fromVector(vector, degree)
    }

    public fun convertToGVector(gVectorOrZero: GVectorOrZero<D, B, S, V>, degree: D): GVector<D, B, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }

    public operator fun get(degree: Int): VectorSpace<B, S, V> = this[this.degreeGroup.fromInt(degree)]
    public fun fromVector(vector: Vector<B, S, V>, degree: Int): GVector<D, B, S, V> = this.fromVector(vector, this.degreeGroup.fromInt(degree))
    public fun fromNumVector(numVector: V, degree: Int): GVector<D, B, S, V> = this.fromNumVector(numVector, this.degreeGroup.fromInt(degree))
    public fun fromCoeffList(coeffList: List<S>, degree: Int): GVector<D, B, S, V> = this.fromCoeffList(coeffList, this.degreeGroup.fromInt(degree))
    public fun fromCoeffMap(coeffMap: Map<Int, S>, degree: Int): GVector<D, B, S, V> = this.fromCoeffMap(coeffMap, this.degreeGroup.fromInt(degree))
    public fun fromBasisName(basisName: B, degree: Int): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree))
    public fun fromBasisName(basisName: B, degree: Int, coeff: S): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree), coeff)
    public fun fromBasisName(basisName: B, degree: Int, coeff: Int): GVector<D, B, S, V> = this.fromBasisName(basisName, this.degreeGroup.fromInt(degree), coeff)
    public fun getBasis(degree: Int): List<GVector<D, B, S, V>> = this.getBasis(this.degreeGroup.fromInt(degree))
    public fun getZero(degree: Int): GVector<D, B, S, V> = this.getZero(this.degreeGroup.fromInt(degree))
    public fun convertToGVector(gVectorOrZero: GVectorOrZero<D, B, S, V>, degree: Int): GVector<D, B, S, V> = this.convertToGVector(gVectorOrZero, this.degreeGroup.fromInt(degree))

    public operator fun contains(gVector: GVector<D, B, S, V>): Boolean {
        return gVector.gVectorSpace == this
    }

    public fun add(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        if (a !in this)
            throw IllegalContextException("The gVector $a does not match the context")
        if (b !in this)
            throw IllegalContextException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException(
                "Cannot add two graded vectors of different degrees: " +
                    "deg($a)=${a.degree} and deg($b)=${b.degree}"
            )
        val vector = a.vector.vectorSpace.context.run {
            a.vector + b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    public fun subtract(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        if (a !in this)
            throw IllegalContextException("The gVector $a does not match the context")
        if (b !in this)
            throw IllegalContextException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException(
                "Cannot subtract a graded vector from another one with a different degree: " +
                    "deg($a)=${a.degree} and deg($b)=${b.degree}"
            )
        val vector = a.vector.vectorSpace.context.run {
            a.vector - b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    public fun multiply(scalar: S, gVector: GVector<D, B, S, V>): GVector<D, B, S, V> {
        if (gVector !in this)
            throw IllegalContextException("The gVector $gVector does not match the context")
        val vector = gVector.vector.vectorSpace.context.run { scalar * gVector.vector }
        return this.fromVector(vector, gVector.degree)
    }

    public val zeroGVector: ZeroGVector<D, B, S, V> get() = ZeroGVector()

    public fun <M : Matrix<S, V>> isBasis(
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

    public fun <M : Matrix<S, V>> isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
        degree: Int,
        matrixSpace: MatrixSpace<S, V, M>
    ): Boolean {
        return this.isBasis(gVectorList, this.degreeGroup.fromInt(degree), matrixSpace)
    }

    public fun <M : Matrix<S, V>> getIdentity(matrixSpace: MatrixSpace<S, V, M>): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap(this, this, 0, matrixSpace, "id") { degree ->
            this[degree].getIdentity(matrixSpace)
        }
    }
}

public class GVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>>(
    public override val numVectorSpace: NumVectorSpace<S, V>,
    public override val degreeGroup: DegreeGroup<D>,
    public override val name: String,
    public override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
    public override val listDegreesForAugmentedDegree: ((Int) -> List<D>)?,
    private val getVectorSpace: (D) -> VectorSpace<B, S, V>,
) : GVectorSpace<D, B, S, V> {
    private val cache: MutableMap<D, VectorSpace<B, S, V>> = mutableMapOf()
    override val context: GVectorContext<D, B, S, V> by lazy { GVectorContextImpl(this) }

    override fun get(degree: D): VectorSpace<B, S, V> {
        this.cache[degree]?.let {
            // if cache exists
            return it
        }
        // if cache does not exist
        val vectorSpace = this.getVectorSpace(degree)
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    override fun toString(): String {
        return this.name
    }
}
