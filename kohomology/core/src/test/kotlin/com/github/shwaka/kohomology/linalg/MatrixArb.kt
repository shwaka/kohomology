package com.github.shwaka.kohomology.linalg

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next

private fun <A> myArbList(arb: Arb<A>, n: Int): Arb<List<A>> {
    // Arb.list がおかしいので自作した
    return arbitrary { rs: RandomSource ->
        (0 until n).map { _ -> arb.next(rs) }
    }
}

fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V, M>> MatrixSpace<S, V, M>.arb(scalarArb: Arb<S>, rowCount: Int, colCount: Int): Arb<M> {
    val size = rowCount * colCount
    return myArbList(scalarArb, size).map { elmList -> this.fromFlatList(elmList, rowCount, colCount) }
    // return Arb.list(scalarArb, size..size).map { elmList -> this.fromFlatList(elmList, rowCount, colCount) }
}
