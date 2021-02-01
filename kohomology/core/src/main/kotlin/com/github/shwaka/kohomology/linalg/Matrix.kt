package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.field.times
import com.github.shwaka.kohomology.util.getPermutation

interface Matrix<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    operator fun plus(other: M): M
    operator fun minus(other: M): M
    operator fun times(other: M): M
    operator fun times(numVector: V): V
    operator fun times(scalar: S): M
    operator fun times(scalar: Int): M {
        return this * this.matrixSpace.field.fromInt(scalar)
    }
    operator fun unaryMinus(): M {
        return this * (-1)
    }
    val rowEchelonForm: RowEchelonForm<S, V, M>
    fun det(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val rowEchelonForm = this.rowEchelonForm
        val rowEchelonMatrix: M = rowEchelonForm.matrix
        val sign: Int = rowEchelonForm.sign
        val detUpToSign = (0 until this.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
        return detUpToSign * sign
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
    fun toList(): List<List<S>> {
        return (0 until this.rowCount).map { i -> (0 until this.colCount).map { j -> this[i, j] } }
    }
    fun toPrettyString(): String
    fun unwrap(): M
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
}

interface RowEchelonForm<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val matrix: M
    val reducedMatrix: M
    val pivots: List<Int>
    val sign: Int
}

interface MatrixSpace<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val field: Field<S>
    val numVectorSpace: NumVectorSpace<S, V>
    fun fromRows(rows: List<List<S>>): M
    fun fromRows(vararg rows: List<S>): M {
        if (rows.isEmpty())
            throw IllegalArgumentException("Row list is empty, which is not supported")
        return this.fromRows(rows.toList())
    }
    fun fromCols(cols: List<List<S>>): M
    fun fromCols(vararg cols: List<S>): M {
        if (cols.isEmpty())
            throw IllegalArgumentException("Column list is empty, which is not supported")
        return this.fromCols(cols.toList())
    }
    fun fromNumVectors(numVectors: List<V>): M {
        if (numVectors.isEmpty())
            throw IllegalArgumentException("Vector list is empty, which is not supported")
        val cols = numVectors.map { v -> v.toList() }
        return this.fromCols(cols)
    }
    fun fromNumVectors(vararg numVectors: V): M {
        // This does not work (due to type erasure?)
        return this.fromNumVectors(numVectors.toList())
    }

    fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M

    fun getZero(rowCount: Int, colCount: Int): M {
        val rows = List(rowCount) { _ -> List(colCount) { _ -> this.field.zero } }
        return this.fromRows(rows)
    }

    fun getZero(dim: Int): M {
        return this.getZero(dim, dim)
    }

    fun getId(dim: Int): M {
        val rows = List(dim) { i ->
            List(dim) { j ->
                if (i == j)
                    this.field.one
                else
                    this.field.zero
            }
        }
        return this.fromRows(rows)
    }
}
