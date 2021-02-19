package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.BigRationalField
import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.IntRationalField
import com.github.shwaka.kohomology.field.LongRationalField
import com.github.shwaka.kohomology.field.Scalar

data class DenseNumVector<S : Scalar<S>>(
    val values: List<S>,
    override val numVectorSpace: NumVectorSpace<S, DenseNumVector<S>>
) : NumVector<S, DenseNumVector<S>> {
    override val dim: Int
        get() = this.values.size
}

class DenseNumVectorSpace<S : Scalar<S>>(
    override val field: Field<S>
) : NumVectorSpace<S, DenseNumVector<S>> {
    companion object {
        // TODO: cache まわりの型が割とやばい
        // generic type に対する cache ってどうすれば良いだろう？
        private val cache: MutableMap<Field<*>, DenseNumVectorSpace<*>> = mutableMapOf()
        fun <S : Scalar<S>> from(field: Field<S>): DenseNumVectorSpace<S> {
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

    override val numVectorContext = NumVectorContext(this)

    override fun add(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot add numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.withContext {
            for (i in a.values.indices) {
                result.add(a.values[i] + b.values[i])
            }
        }
        return DenseNumVector(result, a.numVectorSpace)
    }

    override fun subtract(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a.dim != b.dim)
            throw IllegalArgumentException("Cannot subtract numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.withContext {
            for (i in a.values.indices) {
                result.add(a.values[i] - b.values[i])
            }
        }
        return DenseNumVector(result, a.numVectorSpace)
    }

    override fun multiply(scalar: S, numVector: DenseNumVector<S>): DenseNumVector<S> {
        val values: List<S> = this.field.withContext { numVector.values.map { it * scalar } }
        return DenseNumVector(values, numVector.numVectorSpace)
    }

    override fun getElement(numVector: DenseNumVector<S>, ind: Int): S {
        return numVector.values[ind]
    }

    override fun fromValues(values: List<S>): DenseNumVector<S> {
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

val DenseNumVectorSpaceOverIntRational = DenseNumVectorSpace.from(IntRationalField)
val DenseNumVectorSpaceOverLongRational = DenseNumVectorSpace.from(LongRationalField)
val DenseNumVectorSpaceOverBigRational = DenseNumVectorSpace.from(BigRationalField)
