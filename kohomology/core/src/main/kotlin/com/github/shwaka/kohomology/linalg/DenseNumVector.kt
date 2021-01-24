package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

class DenseNumVector<S : Scalar<S>>(val values: List<S>, override val vectorSpace: DenseNumVectorSpace<S>) : NumVector<S, DenseNumVector<S>> {
    override val dim: Int = this.values.size

    override fun plus(other: DenseNumVector<S>): DenseNumVector<S> {
        val result: MutableList<S> = mutableListOf()
        for (i in this.values.indices) {
            result.add(this.values[i] + other.values[i])
        }
        return DenseNumVector(result, this.vectorSpace)
    }

    override fun times(other: S): DenseNumVector<S> {
        return DenseNumVector(this.values.map { it * other }, this.vectorSpace)
    }

    override fun unwrap(): DenseNumVector<S> {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as DenseNumVector<*>

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    override fun toString(): String {
        return this.values.toString()
    }
}

class DenseNumVectorSpace<S : Scalar<S>>
private constructor(override val field: Field<S>) : NumVectorSpace<S, DenseNumVector<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<Field<*>, DenseNumVectorSpace<*>> = mutableMapOf()
        fun <S : Scalar<S>> from(field: Field<S>): DenseNumVectorSpace<S> {
            if (this.cache.containsKey(field)) {
                @Suppress("UNCHECKED_CAST")
                return this.cache[field] as DenseNumVectorSpace<S>
            } else {
                val vectorSpace = DenseNumVectorSpace(field)
                this.cache[field] = vectorSpace
                return vectorSpace
            }
        }
    }

    override fun fromValues(values: List<S>): DenseNumVector<S> {
        // if (values.size != this.dim) {
        //     throw IllegalArgumentException("The size of vector doesn't equal to the dimension: $values.size != ${this.dim}")
        // }
        return DenseNumVector(values, this)
    }
    override fun fromValues(vararg values: S): DenseNumVector<S> {
        return this.fromValues(values.toList())
    }

    override fun getZero(dim: Int): DenseNumVector<S> {
        val values = List(dim) { _ -> this.field.zero }
        return this.fromValues(values)
    }
}
