package com.github.shwaka.kohomology.linalg

class MatrixOfRank2<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val mat: M
) {
    init {
        this.matrixSpace.context.run {
            if (mat.rowCount != 2 || mat.colCount != 2)
                throw IllegalArgumentException("mat should be square matrix of rank 2")
        }
    }
    private val a = mat[0, 0]
    private val b = mat[0, 1]
    private val c = mat[1, 0]
    private val d = mat[1, 1]

    operator fun plus(other: MatrixOfRank2<S, V, M>): MatrixOfRank2<S, V, M> {
        val mat = this.matrixSpace.context.run {
            val self = this@MatrixOfRank2
            self.matrixSpace.fromRowList(
                listOf(
                    listOf(self.a + other.a, self.b + other.b),
                    listOf(self.c + other.c, self.d + other.d)
                )
            )
        }
        return MatrixOfRank2(this.matrixSpace, mat)
    }

    operator fun minus(other: MatrixOfRank2<S, V, M>): MatrixOfRank2<S, V, M> {
        val mat = this.matrixSpace.context.run {
            val self = this@MatrixOfRank2
            self.matrixSpace.fromRowList(
                listOf(
                    listOf(self.a - other.a, self.b - other.b),
                    listOf(self.c - other.c, self.d - other.d)
                )
            )
        }
        return MatrixOfRank2(this.matrixSpace, mat)
    }

    operator fun unaryMinus(): MatrixOfRank2<S, V, M> {
        val mat = this.matrixSpace.context.run {
            val self = this@MatrixOfRank2
            self.matrixSpace.fromRowList(
                listOf(
                    listOf(-self.a, -self.b),
                    listOf(-self.c, -self.d)
                )
            )
        }
        return MatrixOfRank2(this.matrixSpace, mat)
    }

    fun det(): S {
        return this.matrixSpace.context.run {
            val self = this@MatrixOfRank2
            self.a * self.d - self.b * self.c
        }
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
