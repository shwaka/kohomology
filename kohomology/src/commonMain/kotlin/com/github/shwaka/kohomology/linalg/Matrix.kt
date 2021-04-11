package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.getPermutation

interface Matrix<S : Scalar, V : NumVector<S>> {
    val numVectorSpace: NumVectorSpace<S, V>
    val rowCount: Int
    val colCount: Int
    operator fun get(rowInd: Int, colInd: Int): S
    fun toPrettyString(): String

    fun toList(): List<List<S>> {
        return (0 until this.rowCount).map { i -> (0 until this.colCount).map { j -> this[i, j] } }
    }

    fun toNumVectorList(): List<V> {
        return (0 until this.colCount).map { j ->
            this.numVectorSpace.fromValueList((0 until this.rowCount).map { i -> this[i, j] })
        }
    }
}

interface MatrixOperations<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    operator fun contains(matrix: M): Boolean
    fun add(first: M, second: M): M
    fun subtract(first: M, second: M): M
    fun multiply(first: M, second: M): M
    fun multiply(matrix: M, numVector: V): V
    fun multiply(matrix: M, scalar: S): M
    fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M>
    fun computeTranspose(matrix: M): M
    fun computeInnerProduct(matrix: M, numVector1: V, numVector2: V): S
    fun joinMatrices(matrixList: List<M>): M
    fun computeRowSlice(matrix: M, rowRange: IntRange): M
    fun computeColSlice(matrix: M, colRange: IntRange): M
}

class MatrixContext<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
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
    fun M.rowSlice(rowRange: IntRange): M = this@MatrixContext.computeRowSlice(this, rowRange)
    fun M.colSlice(colRange: IntRange): M = this@MatrixContext.computeColSlice(this, colRange)
    fun List<M>.join(): M = this@MatrixContext.joinMatrices(this)

    fun M.det(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val rowEchelonForm = this.rowEchelonForm
        val rowEchelonMatrix: M = rowEchelonForm.matrix
        val sign: Sign = rowEchelonForm.sign
        return this@MatrixContext.field.context.run {
            val detUpToSign = (0 until this@det.rowCount).map { i -> rowEchelonMatrix[i, i] }.reduce { a, b -> a * b }
            detUpToSign * sign
        }
    }

    fun M.detByPermutations(): S {
        if (this.rowCount != this.colCount)
            throw ArithmeticException("Determinant is defined only for square matrices")
        val n = this.rowCount
        var result: S = zero
        this@MatrixContext.field.context.run {
            for ((perm, sign) in getPermutation((0 until n).toList())) {
                val product: S = (0 until n).zip(perm).map { (i, j) -> this@detByPermutations[i, j] }.reduce { a, b -> a * b }
                result += sign * product
            }
        }
        return result
    }

    fun M.isInvertible(): Boolean {
        if (this.rowCount != this.colCount)
            throw IllegalArgumentException("Invertibility of non-square matrix is not defined")
        val pivots: List<Int> = this.rowEchelonForm.pivots
        return pivots.size == this.rowCount
    }

    fun M.transpose(): M {
        return this@MatrixContext.computeTranspose(this)
    }

    fun M.innerProduct(numVector1: V, numVector2: V): S {
        return this@MatrixContext.computeInnerProduct(this, numVector1, numVector2)
    }

    fun M.computeKernelBasis(): List<V> {
        val rowEchelonForm = this.rowEchelonForm // TODO: cache できてないので、とりあえず local 変数に代入して誤魔化す
        val dim = this.colCount
        val pivots = rowEchelonForm.pivots
        val firstNonZeroIndex: Int = if (pivots.isEmpty()) this.colCount else pivots[0]
        val trivialVectors: List<V> = (0 until firstNonZeroIndex).map { i ->
            this.numVectorSpace.getOneAtIndex(i, dim)
        }
        val matrix = rowEchelonForm.reducedMatrix
        val vectorsForPivots: List<V> = pivots.indices.map { p ->
            val start = pivots[p] + 1
            val limit = if (p + 1 < pivots.size) pivots[p + 1] else dim
            (start until limit).map { k ->
                var numVector = this.numVectorSpace.getOneAtIndex(k, dim)
                for (q in p downTo 0) {
                    numVector -= this.numVectorSpace.getOneAtIndex(pivots[q], dim) * matrix[q, k]
                }
                numVector
            }
        }.flatten()
        return trivialVectors + vectorsForPivots
    }
}

interface MatrixSpace<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> : MatrixOperations<S, V, M> {
    val context: MatrixContext<S, V, M>
    val numVectorSpace: NumVectorSpace<S, V>
    val field: Field<S>
    fun fromRowList(rows: List<List<S>>, colCount: Int? = null): M
    fun fromColList(cols: List<List<S>>, rowCount: Int? = null): M
    fun fromNumVectorList(numVectors: List<V>, dim: Int? = null): M {
        if (numVectors.isEmpty() && (dim == null))
            throw IllegalArgumentException("Vector list is empty and dim is not specified")
        val cols = this.numVectorSpace.context.run { numVectors.map { v -> v.toList() } }
        return this.fromColList(cols, dim)
    }
    fun fromFlatList(list: List<S>, rowCount: Int, colCount: Int): M

    fun getZero(rowCount: Int, colCount: Int): M {
        val zero = this.field.zero
        val rows = List(rowCount) { List(colCount) { zero } }
        return this.fromRowList(rows)
    }

    fun getZero(dim: Int): M {
        return this.getZero(dim, dim)
    }

    fun getId(dim: Int): M {
        val zero = this.field.zero
        val one = this.field.one
        val rows = List(dim) { i ->
            List(dim) { j ->
                if (i == j)
                    one
                else
                    zero
            }
        }
        return this.fromRowList(rows, colCount = dim)
    }
}

interface RowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val matrix: M
    val reducedMatrix: M
    val pivots: List<Int>
    val sign: Sign
}
