package com.github.shwaka.kohomology.linalg

import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import com.github.shwaka.kohomology.myArbList

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>> MatrixSpace<S, V, M>.arb(scalarArb: Arb<S>, rowCount: Int, colCount: Int): Arb<M> {
    val size = rowCount * colCount
    return myArbList(scalarArb, size).map { elmList -> this.fromFlatList(elmList, rowCount, colCount) }
    // return Arb.list(scalarArb, size..size).map { elmList -> this.fromFlatList(elmList, rowCount, colCount) }
}

fun <S : Scalar, V : NumVector<S>> NumVectorSpace<S, V>.arb(scalarArb: Arb<S>, dim: Int): Arb<V> {
    return myArbList(scalarArb, dim).map { valueList -> this.fromValueList(valueList) }
}
