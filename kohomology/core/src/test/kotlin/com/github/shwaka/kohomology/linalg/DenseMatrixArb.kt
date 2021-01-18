package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map

fun <S : Scalar<S>> DenseMatrixSpace<S>.arb(scalarArb: Arb<S>, rowCount: Int, colCount: Int): Arb<DenseMatrix<S>> {
    val size = rowCount * colCount
    return Arb.list(scalarArb, size..size).map { elmList -> this.fromFlatList(elmList, rowCount, colCount) }
}
