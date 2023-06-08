package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign

public interface NumVector<S : Scalar> {
    public val field: Field<S>
    public val dim: Int
    public fun isZero(): Boolean
    public fun toList(): List<S>
    public fun toMap(): Map<Int, S>
}

public interface NumVectorContext<S : Scalar, V : NumVector<S>> : ScalarContext<S> {
    public val numVectorSpace: NumVectorSpace<S, V>

    public operator fun V.plus(other: V): V = this@NumVectorContext.numVectorSpace.add(this, other)
    public operator fun V.minus(other: V): V = this@NumVectorContext.numVectorSpace.subtract(this, other)
    public operator fun V.times(scalar: S): V = this@NumVectorContext.numVectorSpace.multiply(scalar, this)
    public operator fun S.times(numVector: V): V = numVector * this
    public operator fun V.times(scalar: Int): V = this * this@NumVectorContext.field.fromInt(scalar)
    public operator fun Int.times(numVector: V): V = numVector * this
    public operator fun V.times(sign: Sign): V {
        return when (sign) {
            Sign.PLUS -> this
            Sign.MINUS -> -this
        }
    }
    public operator fun Sign.times(numVector: V): V = numVector * this
    public infix fun V.dot(other: V): S = this@NumVectorContext.numVectorSpace.innerProduct(this, other)
    public operator fun V.unaryMinus(): V = this@NumVectorContext.numVectorSpace.unaryMinusOf(this)
    public operator fun V.get(ind: Int): S = this@NumVectorContext.numVectorSpace.getElement(this, ind)
    public fun List<S>.toNumVector(): V = this@NumVectorContext.numVectorSpace.fromValueList(this)
    public fun Map<Int, S>.toNumVector(dim: Int): V = this@NumVectorContext.numVectorSpace.fromValueMap(this, dim)
}

internal class NumVectorContextImpl<S : Scalar, V : NumVector<S>>(
    override val numVectorSpace: NumVectorSpace<S, V>
) : NumVectorContext<S, V>, ScalarContext<S> by ScalarContextImpl(numVectorSpace.field)

public interface NumVectorSpace<S : Scalar, V : NumVector<S>> {
    public val field: Field<S>
    public val context: NumVectorContext<S, V>
    public fun getZero(dim: Int): V
    public fun getOneAtIndex(index: Int, dim: Int): V {
        val valueList = this.field.context.run {
            (0 until dim).map { if (it == index) one else zero }
        }
        return this.fromValueList(valueList)
    }
    public operator fun contains(numVector: V): Boolean
    public fun add(a: V, b: V): V
    public fun subtract(a: V, b: V): V
    public fun multiply(scalar: S, numVector: V): V
    // Since divideByNumVector isn't usual operation, it isn't added to NumVectorContext
    public fun divideByNumVector(a: V, b: V): S?
    public fun unaryMinusOf(numVector: V): V
    public fun getElement(numVector: V, ind: Int): S
    public fun innerProduct(numVector1: V, numVector2: V): S
    public fun fromValueList(valueList: List<S>): V
    public fun fromValueMap(valueMap: Map<Int, S>, dim: Int): V
    public fun fromReducedValueMap(valueMap: Map<Int, S>, dim: Int): V = this.fromValueMap(valueMap, dim)
}
