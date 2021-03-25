package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.getPermutation

interface Matrix<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val matrixSpace: MatrixSpace<S, V, M>
    val rowCount: Int
    val colCount: Int
    fun toPrettyString(): String
}

interface MatrixOperations<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    fun add(first: M, second: M): M
    fun subtract(first: M, second: M): M
    fun multiply(first: M, second: M): M
    fun multiply(matrix: M, numVector: V): V
    fun multiply(matrix: M, scalar: S): M
    fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M>
    fun computeTranspose(matrix: M): M
    fun computeInnerProduct(matrix: M, numVector1: V, numVector2: V): S
    fun getElement(matrix: M, rowInd: Int, colInd: Int): S
}

class MatrixContext<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    private val matrixOperations: MatrixOperations<S, V, M>
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), MatrixOperations<S, V, M> by matrixOperations {
    operator fun M.plus(other: M): M = this@MatrixContext.add(this, other)
    operator fun M.minus(other: M): M = this@MatrixContext.subtract(this, other)
    operator fun M.times(other: M): M = this@MatrixContext.multiply(this, other)
    operator fun M.times(numVector: V): V = this@MatrixContext.multiply(this, numVector)
    operator fun M.times(scalar: S): M = this@MatrixContext.multiply(this, scalar)
    operator fun S.times(matrix: M): M = matrix * this
    operator fun M.times(scalar: Int): M = this@MatrixContext.multiply(this, this@MatrixContext.field.fromInt(scalar))
    operator fun Int.times(matrix: M): M = matrix * this
    operator fun M.unaryMinus(): M = this * (-1)
    val M.rowEchelonForm: RowEchelonForm<S, V, M>
        get() = this@MatrixContext.computeRowEchelonForm(this) // TODO: cache!

    operator fun M.get(rowInd: Int, colInd: Int): S = this@MatrixContext.getElement(this, rowInd, colInd)

    fun M.det(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val rowEchelonForm = this.rowEchelonForm
        val rowEchelonMatrix: M = rowEchelonForm.matrix
        val sign: Int = rowEchelonForm.sign
        return this@MatrixContext.field.withContext {
            val detUpToSign = (0 until this@det.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
            detUpToSign * sign
        }
    }

    fun M.detByPermutations(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val n = this.rowCount
        var result: S = zero
        this@MatrixContext.field.withContext {
            for ((perm, sign) in getPermutation((0 until n).toList())) {
                val product: S = (0 until n).zip(perm).map { (i, j) -> this@detByPermutations[i, j] }.reduce { a, b -> a * b }
                result += sign * product
            }
        }
        return result
    }

    fun M.transpose(): M {
        return this@MatrixContext.computeTranspose(this)
    }

    fun M.innerProduct(numVector1: V, numVector2: V): S {
        return this@MatrixContext.computeInnerProduct(this, numVector1, numVector2)
    }

    fun M.toList(): List<List<S>> {
        return (0 until this.rowCount).map { i -> (0 until this.colCount).map { j -> this[i, j] } }
    }
}

interface MatrixSpace<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> : MatrixOperations<S, V, M> {
    val matrixContext: MatrixContext<S, V, M>
    fun <T> withContext(block: MatrixContext<S, V, M>.() -> T) = this.matrixContext.block()
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
        val cols = this.numVectorSpace.withContext { numVectors.map { v -> v.toList() } }
        return this.fromCols(cols)
    }

    fun fromNumVectors(vararg numVectors: V): M {
        // This does not work (due to type erasure?)
        return this.fromNumVectors(numVectors.toList())
    }

    fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M

    fun getZero(rowCount: Int, colCount: Int): M {
        val zero = this.withContext { zero }
        val rows = List(rowCount) { _ -> List(colCount) { _ -> zero } }
        return this.fromRows(rows)
    }

    fun getZero(dim: Int): M {
        return this.getZero(dim, dim)
    }

    fun getId(dim: Int): M {
        val zero = this.withContext { zero }
        val one = this.withContext { one }
        val rows = List(dim) { i ->
            List(dim) { j ->
                if (i == j)
                    one
                else
                    zero
            }
        }
        return this.fromRows(rows)
    }
}

interface RowEchelonForm<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    val matrix: M
    val reducedMatrix: M
    val pivots: List<Int>
    val sign: Int
}
