package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field

interface Matrix<S, V, M> {
    operator fun plus(other: M): M
    operator fun plus(other: Matrix<S, V, M>): Matrix<S, V, M> {
        return this.matrixSpace.wrap(this + other.unwrap())
    }
    operator fun minus(other: M): M
    operator fun minus(other: Matrix<S, V, M>): Matrix<S, V, M> {
        return this.matrixSpace.wrap(this - other.unwrap())
    }
    operator fun times(vector: V): V
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
}

interface MatrixSpace<S, V, M> {
    fun wrap(m: M): Matrix<S, V, M>
    val field: Field<S>
    val vectorSpace: NumVectorSpace<S, V>
}
