package com.github.shwaka.kohomology.linalg

interface NumVector<S : Scalar> {
    val field: Field<S>
    val dim: Int
    fun isZero(): Boolean
}

interface NumVectorOperations<S : Scalar, V : NumVector<S>> {
    operator fun contains(numVector: V): Boolean
    fun add(a: V, b: V): V
    fun subtract(a: V, b: V): V
    fun multiply(scalar: S, numVector: V): V
    fun getElement(numVector: V, ind: Int): S
    fun innerProduct(numVector1: V, numVector2: V): S
}

open class NumVectorContext<S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    private val numVectorOperations: NumVectorOperations<S, V>
) : ScalarContext<S>(scalarOperations), NumVectorOperations<S, V> by numVectorOperations {
    operator fun V.plus(other: V): V = this@NumVectorContext.add(this, other)
    operator fun V.minus(other: V): V = this@NumVectorContext.subtract(this, other)
    operator fun V.times(scalar: S): V = this@NumVectorContext.multiply(scalar, this)
    operator fun S.times(numVector: V): V = numVector * this
    operator fun V.times(scalar: Int): V = this * fromInt(scalar)
    operator fun Int.times(numVector: V): V = numVector * this
    infix fun V.dot(other: V): S = this@NumVectorContext.innerProduct(this, other)
    operator fun V.unaryMinus(): V = this * (-1)
    operator fun V.get(ind: Int): S = this@NumVectorContext.getElement(this, ind)
    fun V.toList(): List<S> {
        return (0 until this.dim).map { i -> this[i] }
    }
}

interface NumVectorSpace<S : Scalar, V : NumVector<S>> : NumVectorOperations<S, V> {
    val field: Field<S>
    val context: NumVectorContext<S, V>
    fun getZero(dim: Int): V
    fun fromValueList(valueList: List<S>): V
    fun fromValueMap(valueMap: Map<Int, S>, dim: Int): V
    fun getOneAtIndex(index: Int, dim: Int): V {
        val valueList = this.field.context.run {
            (0 until dim).map { if (it == index) one else zero }
        }
        return this.fromValueList(valueList)
    }
}
