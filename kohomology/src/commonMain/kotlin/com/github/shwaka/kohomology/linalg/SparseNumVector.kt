package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException

class SparseNumVector<S : Scalar> private constructor(
    val valueMap: Map<Int, S>,
    override val field: Field<S>,
    override val dim: Int,
) : NumVector<S> {
    companion object {
        operator fun <S : Scalar> invoke(
            valueMap: Map<Int, S>,
            field: Field<S>,
            dim: Int,
        ): SparseNumVector<S> {
            val filteredValueMap: Map<Int, S> = valueMap.filterValues { it.isNotZero() }
            return SparseNumVector(filteredValueMap, field, dim)
        }

        internal fun <S : Scalar> fromReduced(
            valueMap: Map<Int, S>,
            field: Field<S>,
            dim: Int,
        ): SparseNumVector<S> {
            // If valueMap does not contain any zero in its values
            return SparseNumVector(valueMap, field, dim)
        }
    }
    override fun isZero(): Boolean {
        return this.valueMap.all { (_, value) -> value.isZero() }
    }

    override fun toString(): String {
        return "SparseNumVector(valueMap=$valueMap, field=$field, dim=$dim)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SparseNumVector<*>

        if (valueMap != other.valueMap) return false
        if (field != other.field) return false
        if (dim != other.dim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = valueMap.hashCode()
        result = 31 * result + field.hashCode()
        result = 31 * result + dim
        return result
    }

    override fun toList(): List<S> {
        return (0 until this.dim).map { index -> this.valueMap[index] ?: this.field.zero }
    }

    override fun toMap(): Map<Int, S> {
        return this.valueMap
    }
}

class SparseNumVectorSpace<S : Scalar>(
    override val field: Field<S>
) : NumVectorSpace<S, SparseNumVector<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<Field<*>, SparseNumVectorSpace<*>> = mutableMapOf()
        fun <S : Scalar> from(field: Field<S>): SparseNumVectorSpace<S> {
            if (this.cache.containsKey(field)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[field] as SparseNumVectorSpace<S>
            } else {
                val numVectorSpace = SparseNumVectorSpace(field)
                this.cache[field] = numVectorSpace
                return numVectorSpace
            }
        }
    }

    override val context = NumVectorContext(this.field, this)

    override fun contains(numVector: SparseNumVector<S>): Boolean {
        return numVector.field == this.field
    }

    override fun add(a: SparseNumVector<S>, b: SparseNumVector<S>): SparseNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot add numVectors of different dim")
        val valueMap: MutableMap<Int, S> = a.valueMap.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.valueMap) {
                when (val valueFromA: S? = valueMap[i]) {
                    null -> valueMap[i] = value
                    else -> valueMap[i] = valueFromA + value
                }
            }
        }
        return SparseNumVector(valueMap, this.field, a.dim)
    }

    override fun subtract(a: SparseNumVector<S>, b: SparseNumVector<S>): SparseNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot add numVectors of different dim")
        val valueMap: MutableMap<Int, S> = a.valueMap.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.valueMap) {
                when (val valueFromA: S? = valueMap[i]) {
                    null -> valueMap[i] = -value
                    else -> valueMap[i] = valueFromA - value
                }
            }
        }
        return SparseNumVector(valueMap, this.field, a.dim)
    }

    override fun multiply(scalar: S, numVector: SparseNumVector<S>): SparseNumVector<S> {
        if (numVector !in this)
            throw IllegalContextException("The denseNumVector $numVector does not match the context ($this)")
        if (scalar !in this.field)
            throw IllegalContextException("The scalar $scalar does not match the context (field = ${this.field})")
        if (scalar.isZero()) return SparseNumVector(mapOf(), this.field, numVector.dim)
        val valueMap = this.field.context.run {
            numVector.valueMap.mapValues { (_, value) ->
                scalar * value
            }
        }
        return SparseNumVector.fromReduced(valueMap, this.field, numVector.dim)
    }

    override fun unaryMinusOf(numVector: SparseNumVector<S>): SparseNumVector<S> {
        if (numVector !in this)
            throw IllegalContextException("The denseNumVector $numVector does not match the context ($this)")
        val valueMap = this.field.context.run {
            numVector.valueMap.mapValues { (_, value) -> -value }
        }
        return SparseNumVector.fromReduced(valueMap, this.field, numVector.dim)
    }

    override fun getElement(numVector: SparseNumVector<S>, ind: Int): S {
        numVector.valueMap[ind]?.let { return it }
        return this.field.zero
    }

    override fun innerProduct(numVector1: SparseNumVector<S>, numVector2: SparseNumVector<S>): S {
        if (numVector1 !in this)
            throw IllegalContextException("The numVector $numVector1 does not match the context")
        if (numVector2 !in this)
            throw IllegalContextException("The numVector $numVector2 does not match the context")
        if (numVector1.dim != numVector2.dim)
            throw InvalidSizeException("Cannot take the inner product of two numVectors with different length")
        val zero = this.field.zero
        val indices = numVector1.valueMap.keys.intersect(numVector2.valueMap.keys)
        return this.field.context.run {
            indices.map { i ->
                // we know that both 'values' contain the key 'i'
                numVector1.valueMap[i]!! * numVector2.valueMap[i]!!
            }.fold(zero) { acc, x -> acc + x }
        }
    }

    override fun fromValueList(valueList: List<S>): SparseNumVector<S> {
        val valueMap: MutableMap<Int, S> = mutableMapOf()
        for ((i, value) in valueList.withIndex()) {
            if (value.isNotZero())
                valueMap[i] = value
        }
        return SparseNumVector(valueMap, this.field, valueList.size)
    }

    override fun fromValueMap(valueMap: Map<Int, S>, dim: Int): SparseNumVector<S> {
        return SparseNumVector(valueMap, this.field, dim)
    }

    override fun fromReducedValueMap(valueMap: Map<Int, S>, dim: Int): SparseNumVector<S> {
        // If valueMap does not contain any zero in its values
        debugOnly {
            this.assertReduced(valueMap)
        }
        return SparseNumVector.fromReduced(valueMap, this.field, dim)
    }

    private fun assertReduced(valueMap: Map<Int, S>) {
        if (valueMap.values.any { it.isZero() })
            throw IllegalArgumentException("valueMap is not reduced (contains zero as a value)")
    }

    override fun getZero(dim: Int): SparseNumVector<S> {
        return SparseNumVector(mapOf(), this.field, dim)
    }
}
