package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

class DenseMatrixOfRank2<S : Scalar<S>>(private val mat: DenseMatrix<S>) {
    init {
        if (mat.rowCount != 2 || mat.colCount != 2)
            throw IllegalArgumentException("mat should be square matrix of rank 2")
    }
    val a = mat.getElm(0, 0)
    val b = mat.getElm(0, 1)
    val c = mat.getElm(1, 0)
    val d = mat.getElm(1, 1)

    operator fun plus(other: DenseMatrixOfRank2<S>): DenseMatrixOfRank2<S> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(this.a + other.a, this.b + other.b),
            listOf(this.c + other.c, this.d + other.d)
        )
        return DenseMatrixOfRank2(mat)
    }

    operator fun minus(other: DenseMatrixOfRank2<S>): DenseMatrixOfRank2<S> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(this.a - other.a, this.b - other.b),
            listOf(this.c - other.c, this.d - other.d)
        )
        return DenseMatrixOfRank2(mat)
    }

    operator fun unaryMinus(): DenseMatrixOfRank2<S> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(-this.a, -this.b),
            listOf(-this.c, -this.d)
        )
        return DenseMatrixOfRank2(mat)
    }

    fun det(): S {
        return this.a * this.d - this.b * this.c
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as DenseMatrixOfRank2<*>

        if (a != other.a) return false
        if (b != other.b) return false
        if (c != other.c) return false
        if (d != other.d) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a.hashCode()
        result = 31 * result + b.hashCode()
        result = 31 * result + c.hashCode()
        result = 31 * result + d.hashCode()
        return result
    }
}
