package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar

class MatrixOfRank2<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(private val mat: M) {
    init {
        if (mat.rowCount != 2 || mat.colCount != 2)
            throw IllegalArgumentException("mat should be square matrix of rank 2")
    }
    private val a = mat[0, 0]
    private val b = mat[0, 1]
    private val c = mat[1, 0]
    private val d = mat[1, 1]

    operator fun plus(other: MatrixOfRank2<S, V, M>): MatrixOfRank2<S, V, M> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(this.a + other.a, this.b + other.b),
            listOf(this.c + other.c, this.d + other.d)
        )
        return MatrixOfRank2(mat)
    }

    operator fun minus(other: MatrixOfRank2<S, V, M>): MatrixOfRank2<S, V, M> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(this.a - other.a, this.b - other.b),
            listOf(this.c - other.c, this.d - other.d)
        )
        return MatrixOfRank2(mat)
    }

    operator fun unaryMinus(): MatrixOfRank2<S, V, M> {
        val mat = this.mat.matrixSpace.fromRows(
            listOf(-this.a, -this.b),
            listOf(-this.c, -this.d)
        )
        return MatrixOfRank2(mat)
    }

    fun det(): S {
        return this.a * this.d - this.b * this.c
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as MatrixOfRank2<*, *, *>

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
