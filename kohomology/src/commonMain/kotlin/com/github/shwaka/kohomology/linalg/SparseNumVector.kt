package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException

class SparseNumVector<S : Scalar>(
    valueMap: Map<Int, S>,
    override val field: Field<S>,
    override val dim: Int,
) : NumVector<S> {
    val valueList: Map<Int, S> = valueMap.filterValues { it.isNotZero() }
    override fun isZero(): Boolean {
        return this.valueList.all { (_, value) -> value.isZero() }
    }

    override fun toString(): String {
        return "SparseNumVector(valueList=$valueList, field=$field, dim=$dim)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SparseNumVector<*>

        if (valueList != other.valueList) return false
        if (field != other.field) return false
        if (dim != other.dim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = valueList.hashCode()
        result = 31 * result + field.hashCode()
        result = 31 * result + dim
        return result
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
        val valueList: MutableMap<Int, S> = a.valueList.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.valueList) {
                when (val valueFromA: S? = valueList[i]) {
                    null -> valueList[i] = value
                    else -> valueList[i] = valueFromA + value
                }
            }
        }
        return SparseNumVector(valueList, this.field, a.dim)
    }

    override fun subtract(a: SparseNumVector<S>, b: SparseNumVector<S>): SparseNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot add numVectors of different dim")
        val valueList: MutableMap<Int, S> = a.valueList.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.valueList) {
                when (val valueFromA: S? = valueList[i]) {
                    null -> valueList[i] = -value
                    else -> valueList[i] = valueFromA - value
                }
            }
        }
        return SparseNumVector(valueList, this.field, a.dim)
    }

    override fun multiply(scalar: S, numVector: SparseNumVector<S>): SparseNumVector<S> {
        if (numVector !in this)
            throw IllegalContextException("The denseNumVector $numVector does not match the context ($this)")
        if (scalar !in this.field)
            throw IllegalContextException("The scalar $scalar does not match the context (field = ${this.field})")
        if (scalar.isZero()) return SparseNumVector(mapOf(), this.field, numVector.dim)
        val values = this.field.context.run {
            numVector.valueList.mapValues { (_, value) ->
                scalar * value
            }
        }
        return SparseNumVector(values, this.field, numVector.dim)
    }

    override fun getElement(numVector: SparseNumVector<S>, ind: Int): S {
        numVector.valueList[ind]?.let { return it }
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
        val indices = numVector1.valueList.keys.intersect(numVector2.valueList.keys)
        return this.field.context.run {
            indices.map { i ->
                // we know that both 'values' contain the key 'i'
                numVector1.valueList[i]!! * numVector2.valueList[i]!!
            }.fold(zero) { acc, x -> acc + x }
        }
    }

    override fun fromValueList(values: List<S>): SparseNumVector<S> {
        val valuesMap: MutableMap<Int, S> = mutableMapOf()
        for ((i, value) in values.withIndex()) {
            if (value.isNotZero())
                valuesMap[i] = value
        }
        return SparseNumVector(valuesMap, this.field, values.size)
    }

    override fun getZero(dim: Int): SparseNumVector<S> {
        return SparseNumVector(mapOf(), this.field, dim)
    }
}
