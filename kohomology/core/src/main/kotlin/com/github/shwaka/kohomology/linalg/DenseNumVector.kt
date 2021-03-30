package com.github.shwaka.kohomology.linalg

data class DenseNumVector<S : Scalar>(
    val values: List<S>,
    override val field: Field<S>,
) : NumVector<S, DenseNumVector<S>> {
    override val dim: Int
        get() = this.values.size
}

class DenseNumVectorSpace<S : Scalar>(
    override val field: Field<S>
) : NumVectorSpace<S, DenseNumVector<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<Field<*>, DenseNumVectorSpace<*>> = mutableMapOf()
        fun <S : Scalar> from(field: Field<S>): DenseNumVectorSpace<S> {
            if (this.cache.containsKey(field)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[field] as DenseNumVectorSpace<S>
            } else {
                val numVectorSpace = DenseNumVectorSpace(field)
                this.cache[field] = numVectorSpace
                return numVectorSpace
            }
        }
    }

    override val numVectorContext = NumVectorContext(this.field, this)

    override fun contains(numVector: DenseNumVector<S>): Boolean {
        return numVector.field == this.field
    }

    override fun add(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a !in this)
            throw ArithmeticException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw ArithmeticException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot add numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.withContext {
            for (i in a.values.indices) {
                result.add(a.values[i] + b.values[i])
            }
        }
        return DenseNumVector(result, this.field)
    }

    override fun subtract(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a !in this)
            throw ArithmeticException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw ArithmeticException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot subtract numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.withContext {
            for (i in a.values.indices) {
                result.add(a.values[i] - b.values[i])
            }
        }
        return DenseNumVector(result, this.field)
    }

    override fun multiply(scalar: S, numVector: DenseNumVector<S>): DenseNumVector<S> {
        if (numVector !in this)
            throw ArithmeticException("The denseNumVector $numVector does not match the context ($this)")
        if (scalar !in this.field)
            throw ArithmeticException("The scalar $scalar does not match the context (field = ${this.field})")
        val values: List<S> = this.field.withContext { numVector.values.map { it * scalar } }
        return DenseNumVector(values, this.field)
    }

    override fun getElement(numVector: DenseNumVector<S>, ind: Int): S {
        return numVector.values[ind]
    }

    override fun innerProduct(numVector1: DenseNumVector<S>, numVector2: DenseNumVector<S>): S {
        if (numVector1 !in this)
            throw IllegalArgumentException("The numVector $numVector1 does not match the context")
        if (numVector2 !in this)
            throw IllegalArgumentException("The numVector $numVector2 does not match the context")
        if (numVector1.dim != numVector2.dim)
            throw IllegalArgumentException("Cannot take the inner product of two numVectors with different length")
        return this.withContext {
            numVector1.values.zip(numVector2.values).map { (a, b) -> a * b }.reduce { acc, x -> acc + x }
        }
    }

    override fun fromValues(values: List<S>): DenseNumVector<S> {
        return DenseNumVector(values, this.field)
    }

    override fun fromValues(vararg values: S): DenseNumVector<S> {
        return this.fromValues(values.toList())
    }

    override fun getZero(dim: Int): DenseNumVector<S> {
        val values = List(dim) { this.field.withContext { zero } }
        return this.fromValues(values)
    }
}
