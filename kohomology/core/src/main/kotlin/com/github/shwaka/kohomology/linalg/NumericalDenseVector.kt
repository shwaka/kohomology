package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

class NumericalDenseVector<S>(val values: List<Scalar<S>>) : NumericalVector<S, NumericalDenseVector<S>> {
    override fun plus(other: NumericalDenseVector<S>): NumericalDenseVector<S> {
        val result: MutableList<Scalar<S>> = mutableListOf()
        for (i in this.values.indices) {
            result.add(this.values[i] + other.values[i])
        }
        return NumericalDenseVector(result)
    }

    override fun times(other: Scalar<S>): NumericalDenseVector<S> {
        return NumericalDenseVector(this.values.map { it * other })
    }

    override fun unwrap(): NumericalDenseVector<S> {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as NumericalDenseVector<*>

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

    override val vectorSpace: NumericalVectorSpace<S, NumericalDenseVector<S>> = NumericalDenseVectorSpace<S>()
}

class NumericalDenseVectorSpace<S> : NumericalVectorSpace<S, NumericalDenseVector<S>> {
    override fun wrap(v: NumericalDenseVector<S>): NumericalVector<S, NumericalDenseVector<S>> {
        return v
    }
}
