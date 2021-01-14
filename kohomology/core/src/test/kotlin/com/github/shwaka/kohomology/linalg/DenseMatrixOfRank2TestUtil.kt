package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]

data class IntMatrix(val a: Int, val b: Int, val c: Int, val d: Int) {
    fun <S> toDenseMatrix(matrixSpace: DenseMatrixSpace<S>): DenseMatrix<S> {
        val field = matrixSpace.field
        val (a, b, c, d) = listOf(this.a, this.b, this.c, this.d).map(field::fromInt)
        return matrixSpace.fromRows(
            listOf(a, b),
            listOf(c, d)
        )
    }

    operator fun plus(other: IntMatrix): IntMatrix {
        return IntMatrix(this.a + other.a, this.b + other.b, this.c + other.c, this.d + other.d)
    }

    operator fun minus(other: IntMatrix): IntMatrix {
        return IntMatrix(this.a - other.a, this.b - other.b, this.c - other.c, this.d - other.d)
    }
}

class IntMatrixTestGenerator<S>(private val matrixSpace: DenseMatrixSpace<S>) {
    fun generateMatricesOfRank2(
        elmList: List<Int>,
        expect: (intMat1: IntMatrix, intMat2: IntMatrix) -> IntMatrix
    ): Triple<DenseMatrix<S>, DenseMatrix<S>, DenseMatrix<S>> {
        val (a, b, c, d, e, f, g, h) = elmList // .map(field::fromInt)
        val intMat1 = IntMatrix(a, b, c, d)
        val intMat2 = IntMatrix(e, f, g, h)
        val mat1 = intMat1.toDenseMatrix(this.matrixSpace)
        val mat2 = intMat2.toDenseMatrix(this.matrixSpace)
        val expected = expect(intMat1, intMat2).toDenseMatrix(this.matrixSpace)
        return Triple(mat1, mat2, expected)
    }

}
