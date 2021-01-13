package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field

interface Matrix<S, V, M> {
    operator fun plus(other: M): M
    operator fun plus(other: Matrix<S, V, M>): Matrix<S, V, M> {
        return this.matrixSpace.wrap(this + other.unwrap())
    }
    operator fun times(other: V): V
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
}

interface MatrixSpace<S, V, M> {
    fun wrap(m: M): Matrix<S, V, M>
    val field: Field<S>
    val rowCount: Int
    val colCount: Int
}
