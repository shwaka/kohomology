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
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
}

interface MatrixSpace<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    fun wrap(m: M): Matrix<S, V, M>
    val field: Field<S>
    val vectorSpace: NumVectorSpace<S, V>
}
