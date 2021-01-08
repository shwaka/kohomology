package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

class DenseNumVector<S>(val values: List<Scalar<S>>) : NumVector<S, DenseNumVector<S>> {
    override fun plus(other: DenseNumVector<S>): DenseNumVector<S> {
        val result: MutableList<Scalar<S>> = mutableListOf()
        for (i in this.values.indices) {
            result.add(this.values[i] + other.values[i])
        }
        return DenseNumVector(result)
    }

    override fun times(other: Scalar<S>): DenseNumVector<S> {
        return DenseNumVector(this.values.map { it * other })
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

    override val vectorSpace: NumVectorSpace<S, DenseNumVector<S>> = DenseNumVectorSpace<S>()
}

// 多分 interface に対して実装するのは無理っぽい (のでここで実装した)
operator fun <S> Scalar<S>.times(other: DenseNumVector<S>): DenseNumVector<S> {
    return other * this
}

class DenseNumVectorSpace<S> : NumVectorSpace<S, DenseNumVector<S>> {
    override fun wrap(v: DenseNumVector<S>): NumVector<S, DenseNumVector<S>> {
        return v
    }
}
