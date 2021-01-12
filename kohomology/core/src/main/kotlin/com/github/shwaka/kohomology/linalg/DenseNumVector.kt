package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

class DenseNumVector<S>(val values: List<Scalar<S>>, override val vectorSpace: DenseNumVectorSpace<S>) : NumVector<S, DenseNumVector<S>> {
    override fun plus(other: DenseNumVector<S>): DenseNumVector<S> {
        val result: MutableList<Scalar<S>> = mutableListOf()
        for (i in this.values.indices) {
            result.add(this.values[i] + other.values[i])
        }
        return DenseNumVector(result, this.vectorSpace)
    }

    override fun times(other: Scalar<S>): DenseNumVector<S> {
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

// 多分 interface に対して実装するのは無理っぽい (のでここで実装した)
operator fun <S> Scalar<S>.times(other: DenseNumVector<S>): DenseNumVector<S> {
    return other * this
}

class DenseNumVectorSpace<S>(override val field: Field<S>, override val dim: Int) : NumVectorSpace<S, DenseNumVector<S>> {
    // TODO: 各 field に対して cache する
    override fun wrap(v: DenseNumVector<S>): NumVector<S, DenseNumVector<S>> {
        return v
    }
    fun get(values: List<Scalar<S>>): DenseNumVector<S> {
        if (values.size != this.dim) {
            throw IllegalArgumentException("The size of vector doesn't equal to the dimension: $values.size != ${this.dim}")
        }
        return DenseNumVector(values, this)
    }
    fun get(vararg values: Scalar<S>): DenseNumVector<S> {
        return this.get(values.toList())
    }
}
