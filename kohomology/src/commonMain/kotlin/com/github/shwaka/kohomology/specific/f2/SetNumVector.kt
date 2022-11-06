package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.FiniteField
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorContextImpl
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

public class SetNumVector<S : Scalar> private constructor(
    public val valueSet: Set<Int>,
    override val field: Field<S>,
    override val dim: Int,
) : NumVector<S> {
    public companion object {
        public operator fun <S : Scalar> invoke(
            valueSet: Set<Int>,
            field: Field<S>,
            dim: Int,
        ): SetNumVector<S> {
            require(field.characteristic == 2) {
                "field ($field) for SetNumVector must have characteristic 2"
            }
            require(valueSet.size <= dim) {
                "valueSet.size (=${valueSet.size}) must be smaller than or equal to dim (=$dim)"
            }
            return SetNumVector(valueSet, field, dim)
        }
    }

    override fun isZero(): Boolean {
        return this.valueSet.isEmpty()
    }

    override fun toString(): String {
        return "SetNumVector(valueSet=$valueSet, field=$field, dim=$dim)"
    }

    override fun toList(): List<S> {
        return (0 until this.dim).map { index ->
            if (this.valueSet.contains(index)) {
                field.one
            } else {
                field.zero
            }
        }
    }

    override fun toMap(): Map<Int, S> {
        return this.valueSet.map { index -> Pair(index, field.one) }.toMap()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SetNumVector<*>

        if (valueSet != other.valueSet) return false
        if (field != other.field) return false
        if (dim != other.dim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = valueSet.hashCode()
        result = 31 * result + field.hashCode()
        result = 31 * result + dim
        return result
    }
}

internal infix fun <T> Set<T>.xor(other: Set<T>): Set<T> {
    // TODO: performance?
    // return (this union other) - (this intersect other) // This is slower
    val result: MutableSet<T> = mutableSetOf()
    for (x in this) {
        if (x !in other) {
            result.add(x)
        }
    }
    for (y in other) {
        if (y !in this) {
            result.add(y)
        }
    }
    return result
}

public class SetNumVectorSpace<S : Scalar> private constructor(
    override val field: FiniteField<S>
) : NumVectorSpace<S, SetNumVector<S>> {
    public companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<FiniteField<*>, SetNumVectorSpace<*>> = mutableMapOf()
        public fun <S : Scalar> from(field: FiniteField<S>): SetNumVectorSpace<S> {
            if (this.cache.containsKey(field)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[field] as SetNumVectorSpace<S>
            } else {
                val numVectorSpace = SetNumVectorSpace(field)
                this.cache[field] = numVectorSpace
                return numVectorSpace
            }
        }
    }

    init {
        require(this.field.order == 2) {
            "field for SetNumVectorSpace must have exactly 2 elements, " +
                "but had ${this.field.order} elements"
        }
    }

    override val context: NumVectorContext<S, SetNumVector<S>> = NumVectorContextImpl(this)

    override fun contains(numVector: SetNumVector<S>): Boolean {
        return numVector.field == this.field
    }

    override fun add(a: SetNumVector<S>, b: SetNumVector<S>): SetNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The numVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The numVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot add numVectors of different dim")
        return SetNumVector(a.valueSet xor b.valueSet, this.field, a.dim)
    }

    override fun subtract(a: SetNumVector<S>, b: SetNumVector<S>): SetNumVector<S> {
        // Since characteristic 2
        return this.add(a, b)
    }

    override fun multiply(scalar: S, numVector: SetNumVector<S>): SetNumVector<S> {
        return if (scalar.isZero()) {
            this.getZero(numVector.dim)
        } else {
            numVector
        }
    }

    override fun unaryMinusOf(numVector: SetNumVector<S>): SetNumVector<S> {
        // Since characteristic 2
        return numVector
    }

    override fun getElement(numVector: SetNumVector<S>, ind: Int): S {
        return if (numVector.valueSet.contains(ind)) {
            this.field.one
        } else {
            this.field.zero
        }
    }

    override fun innerProduct(numVector1: SetNumVector<S>, numVector2: SetNumVector<S>): S {
        if (numVector1 !in this)
            throw IllegalContextException("The numVector $numVector1 does not match the context")
        if (numVector2 !in this)
            throw IllegalContextException("The numVector $numVector2 does not match the context")
        if (numVector1.dim != numVector2.dim)
            throw InvalidSizeException("Cannot take the inner product of two numVectors with different length")
        return this.field.fromInt(
            (numVector1.valueSet intersect numVector2.valueSet).size
        )
    }

    override fun fromValueList(valueList: List<S>): SetNumVector<S> {
        val valueSet = valueList.withIndex()
            .filter { (_, value) -> value.isNotZero() }
            .map { (ind, _) -> ind }
            .toSet()
        return SetNumVector(valueSet, this.field, valueList.size)
    }

    override fun fromValueMap(valueMap: Map<Int, S>, dim: Int): SetNumVector<S> {
        val valueSet = valueMap
            .filter { (_, value) -> value.isNotZero() }
            .map { (ind, _) -> ind }
            .toSet()
        return SetNumVector(valueSet, this.field, dim)
    }

    override fun fromReducedValueMap(valueMap: Map<Int, S>, dim: Int): SetNumVector<S> {
        return SetNumVector(valueMap.keys, this.field, dim)
    }

    override fun getZero(dim: Int): SetNumVector<S> {
        return SetNumVector(emptySet(), this.field, dim)
    }

    override fun toString(): String {
        return "SetNumVectorSpace(${this.field})"
    }
}
