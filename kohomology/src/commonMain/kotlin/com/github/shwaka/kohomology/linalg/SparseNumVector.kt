package com.github.shwaka.kohomology.linalg

class SparseNumVector<S : Scalar>(
    values: Map<Int, S>,
    override val field: Field<S>,
    override val dim: Int,
) : NumVector<S> {
    val values: Map<Int, S> = values.filterValues { it != field.zero }
    override fun isZero(): Boolean {
        return this.values.all { (_, value) -> value.isZero() }
    }

    override fun toString(): String {
        return "SparseNumVector(values=$values, field=$field, dim=$dim)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SparseNumVector<*>

        if (values != other.values) return false
        if (field != other.field) return false
        if (dim != other.dim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
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
            throw ArithmeticException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw ArithmeticException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot add numVectors of different dim")
        val values: MutableMap<Int, S> = a.values.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.values) {
                when (val valueFromA: S? = values[i]) {
                    null -> values[i] = value
                    else -> values[i] = valueFromA + value
                }
            }
        }
        return SparseNumVector(values, this.field, a.dim)
    }

    override fun subtract(a: SparseNumVector<S>, b: SparseNumVector<S>): SparseNumVector<S> {
        if (a !in this)
            throw ArithmeticException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw ArithmeticException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot add numVectors of different dim")
        val values: MutableMap<Int, S> = a.values.toMutableMap()
        this.field.context.run {
            for ((i, value) in b.values) {
                when (val valueFromA: S? = values[i]) {
                    null -> values[i] = -value
                    else -> values[i] = valueFromA - value
                }
            }
        }
        return SparseNumVector(values, this.field, a.dim)
    }

    override fun multiply(scalar: S, numVector: SparseNumVector<S>): SparseNumVector<S> {
        if (numVector !in this)
            throw ArithmeticException("The denseNumVector $numVector does not match the context ($this)")
        if (scalar !in this.field)
            throw ArithmeticException("The scalar $scalar does not match the context (field = ${this.field})")
        val values = this.field.context.run {
            numVector.values.mapValues { (_, value) ->
                scalar * value
            }
        }
        return SparseNumVector(values, this.field, numVector.dim)
    }

    override fun getElement(numVector: SparseNumVector<S>, ind: Int): S {
        numVector.values[ind]?.let { return it }
        return this.field.zero
    }

    override fun innerProduct(numVector1: SparseNumVector<S>, numVector2: SparseNumVector<S>): S {
        if (numVector1 !in this)
            throw IllegalArgumentException("The numVector $numVector1 does not match the context")
        if (numVector2 !in this)
            throw IllegalArgumentException("The numVector $numVector2 does not match the context")
        if (numVector1.dim != numVector2.dim)
            throw IllegalArgumentException("Cannot take the inner product of two numVectors with different length")
        val zero = this.field.zero
        val indices = numVector1.values.keys.intersect(numVector2.values.keys)
        return this.field.context.run {
            indices.map { i ->
                // we know that both 'values' contain the key 'i'
                numVector1.values[i]!! * numVector2.values[i]!!
            }.fold(zero) { acc, x -> acc + x }
        }
    }

    override fun fromValues(values: List<S>): SparseNumVector<S> {
        val valuesMap: MutableMap<Int, S> = mutableMapOf()
        for ((i, value) in values.withIndex()) {
            if (value != this.field.zero)
                valuesMap[i] = value
        }
        return SparseNumVector(valuesMap, this.field, values.size)
    }

    override fun getZero(dim: Int): SparseNumVector<S> {
        return SparseNumVector(mapOf(), this.field, dim)
    }
}
