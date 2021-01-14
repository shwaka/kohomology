package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

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
        if (this.rowCount != this.colCount) throw ArithmeticException("Determinant is defined only for square matrices")
        val (rowEchelonMatrix: M, _, exchangeCount: Int) = this.rowEchelonForm()
        val detUpToSign = (0 until this.rowCount).map { i -> rowEchelonMatrix.getElm(i, i) }.reduce { a, b -> a * b }
        return if (exchangeCount % 2 == 0) detUpToSign else (-detUpToSign)
    }
    fun getElm(rowInd: Int, colInd: Int): S
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
}

interface MatrixSpace<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val field: Field<S>
    val vectorSpace: NumVectorSpace<S, V>
}
