package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Scalar
import kotlin.IllegalArgumentException

data class IntMatrix(val a: Int, val b: Int, val c: Int, val d: Int) {
    fun <S : Scalar<S>> toDenseMatrix(matrixSpace: DenseMatrixSpace<S>): DenseMatrix<S> {
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

    operator fun unaryMinus(): IntMatrix {
        return IntMatrix(-this.a, -this.b, -this.c, -this.d)
    }
}

class IntMatrixTestGenerator<S : Scalar<S>>(private val matrixSpace: DenseMatrixSpace<S>) {
    private fun generateIntMatrixList(elmList: List<Int>, numMatrices: Int): List<IntMatrix> {
        if (elmList.size != numMatrices * 4) {
            throw IllegalArgumentException("The length of elmList is not equal to the required number (${numMatrices * 4})")
        }
        return (0 until numMatrices).map { i ->
            IntMatrix(elmList[4 * i], elmList[4 * i + 1], elmList[4 * i + 2], elmList[4 * i + 3])
        }
    }

    fun generate1Arg(
        elmList: List<Int>,
        expect: (intMat: IntMatrix) -> IntMatrix
    ): Pair<DenseMatrix<S>, DenseMatrix<S>> {
        val (intMat) = this.generateIntMatrixList(elmList, 1)
        val mat = intMat.toDenseMatrix(this.matrixSpace)
        val expected = expect(intMat).toDenseMatrix(this.matrixSpace)
        return Pair(mat, expected)
    }

    fun generate2Arg(
        elmList: List<Int>,
        expect: (intMat1: IntMatrix, intMat2: IntMatrix) -> IntMatrix
    ): Triple<DenseMatrix<S>, DenseMatrix<S>, DenseMatrix<S>> {
        val (intMat1, intMat2) = this.generateIntMatrixList(elmList, 2)
        val mat1 = intMat1.toDenseMatrix(this.matrixSpace)
        val mat2 = intMat2.toDenseMatrix(this.matrixSpace)
        val expected = expect(intMat1, intMat2).toDenseMatrix(this.matrixSpace)
        return Triple(mat1, mat2, expected)
    }
}
