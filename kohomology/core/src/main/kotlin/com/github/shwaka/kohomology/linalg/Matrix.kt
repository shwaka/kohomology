package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.field.times
import com.github.shwaka.kohomology.util.getPermutation

interface Matrix<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    operator fun plus(other: M): M
    operator fun minus(other: M): M
    operator fun times(other: M): M
    operator fun times(vector: V): V
    operator fun times(scalar: S): M
    operator fun times(scalar: Int): M {
        return this * this.matrixSpace.field.fromInt(scalar)
    }
    operator fun unaryMinus(): M {
        return this * (-1)
    }
    fun rowEchelonForm(): Triple<M, List<Int>, Int>
    fun det(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val (rowEchelonMatrix: M, _, exchangeCount: Int) = this.rowEchelonForm()
        val detUpToSign = (0 until this.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
        return if (exchangeCount % 2 == 0) detUpToSign else (-detUpToSign)
    }
    fun detByPermutations(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val n = this.rowCount
        var result: S = this.matrixSpace.field.zero
        for ((perm, sign) in getPermutation((0 until n).toList())) {
            val product: S = (0 until n).zip(perm).map { (i, j) -> this[i, j] }.reduce { a, b -> a * b }
            result += sign * product
        }
        return result
    }
    operator fun get(rowInd: Int, colInd: Int): S
    fun toPrettyString(): String
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
}

interface MatrixSpace<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val field: Field<S>
    val vectorSpace: NumVectorSpace<S, V>
    fun fromRows(values: List<List<S>>): M
    fun fromRows(vararg rows: List<S>): M
    fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M
}
