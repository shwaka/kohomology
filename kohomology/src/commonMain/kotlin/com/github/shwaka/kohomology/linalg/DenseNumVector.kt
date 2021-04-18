package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.exception.IllegalContextException
import com.github.shwaka.kohomology.exception.InvalidSizeException

data class DenseNumVector<S : Scalar>(
    val valueList: List<S>,
    override val field: Field<S>,
) : NumVector<S> {
    override val dim: Int
        get() = this.valueList.size

    override fun isZero(): Boolean {
        return this.valueList.all { it.isZero() }
    }

    override fun toList(): List<S> {
        return this.valueList
    }

    override fun toMap(): Map<Int, S> {
        return this.valueList.mapIndexedNotNull { index, value ->
            if (value.isNotZero()) Pair(index, value) else null
        }.toMap()
    }
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

    override val context = NumVectorContext(this.field, this)

    override fun contains(numVector: DenseNumVector<S>): Boolean {
        return numVector.field == this.field
    }

    override fun add(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot add numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.context.run {
            for (i in a.valueList.indices) {
                result.add(a.valueList[i] + b.valueList[i])
            }
        }
        return DenseNumVector(result, this.field)
    }

    override fun subtract(a: DenseNumVector<S>, b: DenseNumVector<S>): DenseNumVector<S> {
        if (a !in this)
            throw IllegalContextException("The denseNumVector $a does not match the context ($this)")
        if (b !in this)
            throw IllegalContextException("The denseNumVector $b does not match the context ($this)")
        if (a.dim != b.dim)
            throw InvalidSizeException("Cannot subtract numVectors of different dim")
        val result: MutableList<S> = mutableListOf()
        this.field.context.run {
            for (i in a.valueList.indices) {
                result.add(a.valueList[i] - b.valueList[i])
            }
        }
        return DenseNumVector(result, this.field)
    }

    override fun multiply(scalar: S, numVector: DenseNumVector<S>): DenseNumVector<S> {
        if (numVector !in this)
            throw IllegalContextException("The denseNumVector $numVector does not match the context ($this)")
        if (scalar !in this.field)
            throw IllegalContextException("The scalar $scalar does not match the context (field = ${this.field})")
        val valueList: List<S> = this.field.context.run { numVector.valueList.map { it * scalar } }
        return DenseNumVector(valueList, this.field)
    }

    override fun unaryMinusOf(numVector: DenseNumVector<S>): DenseNumVector<S> {
        if (numVector !in this)
            throw IllegalContextException("The denseNumVector $numVector does not match the context ($this)")
        val valueList: List<S> = this.field.context.run { numVector.valueList.map { -it } }
        return DenseNumVector(valueList, this.field)
    }

    override fun getElement(numVector: DenseNumVector<S>, ind: Int): S {
        return numVector.valueList[ind]
    }

    override fun innerProduct(numVector1: DenseNumVector<S>, numVector2: DenseNumVector<S>): S {
        if (numVector1 !in this)
            throw IllegalContextException("The numVector $numVector1 does not match the context")
        if (numVector2 !in this)
            throw IllegalContextException("The numVector $numVector2 does not match the context")
        if (numVector1.dim != numVector2.dim)
            throw InvalidSizeException("Cannot take the inner product of two numVectors with different length")
        val zero = this.field.zero
        return this.context.run {
            numVector1.valueList.zip(numVector2.valueList)
                .map { (a, b) -> a * b }
                .fold(zero) { acc, x -> acc + x }
        }
    }

    override fun fromValueList(valueList: List<S>): DenseNumVector<S> {
        return DenseNumVector(valueList, this.field)
    }

    override fun fromValueMap(valueMap: Map<Int, S>, dim: Int): DenseNumVector<S> {
        val valueList = (0 until dim).map { i ->
            valueMap[i] ?: this.field.zero
        }
        return DenseNumVector(valueList, this.field)
    }

    override fun getZero(dim: Int): DenseNumVector<S> {
        val valueList = List(dim) { this.field.zero }
        return this.fromValueList(valueList)
    }
}
