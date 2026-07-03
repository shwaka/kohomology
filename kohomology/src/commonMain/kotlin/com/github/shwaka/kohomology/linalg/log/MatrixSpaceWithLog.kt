package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixContext
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar

public class MatrixSpaceWithLog<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val originalMatrixSpace: MatrixSpace<S, V, M>,
) : MatrixSpace<S, V, M> {
    public val logger: MatrixLogger = MatrixLogger()

    override val context: MatrixContext<S, V, M> by lazy {
        MatrixContext(this)
    }
    override val numVectorSpace: NumVectorSpace<S, V>
        get() = originalMatrixSpace.numVectorSpace
    override val field: Field<S>
        get() = originalMatrixSpace.field

    override fun getZero(rowCount: Int, colCount: Int): M {
        return this.originalMatrixSpace.getZero(rowCount, colCount)
    }

    override fun getZero(dim: Int): M {
        return this.originalMatrixSpace.getZero(dim)
    }

    override fun getIdentity(dim: Int): M {
        return this.originalMatrixSpace.getIdentity(dim)
    }

    override fun contains(matrix: M): Boolean {
        return this.originalMatrixSpace.contains(matrix)
    }

    override fun add(first: M, second: M): M {
        val input = MatrixOperationInput.Add(
            rowCount = first.rowCount,
            colCount = first.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.add(first, second)
        }
    }

    override fun subtract(first: M, second: M): M {
        val input = MatrixOperationInput.Subtract(
            rowCount = first.rowCount,
            colCount = first.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.subtract(first, second)
        }
    }

    override fun multiply(first: M, second: M): M {
        val input = MatrixOperationInput.MultiplyMatrix(
            firstRowCount = first.rowCount,
            firstColCount = first.colCount,
            secondColCount = second.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.multiply(first, second)
        }
    }

    override fun multiply(matrix: M, numVector: V): V {
        val input = MatrixOperationInput.MultiplyNumVector(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.multiply(matrix, numVector)
        }
    }

    override fun multiply(matrix: M, scalar: S): M {
        val input = MatrixOperationInput.MultiplyScalar(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.multiply(matrix, scalar)
        }
    }

    override fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M> {
        val input = MatrixOperationInput.ComputeRowEchelonForm(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.computeRowEchelonForm(matrix)
        }
    }

    override fun computeTranspose(matrix: M): M {
        val input = MatrixOperationInput.ComputeTranspose(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.computeTranspose(matrix)
        }
    }

    override fun joinMatrices(matrix1: M, matrix2: M): M {
        val input = MatrixOperationInput.JoinMatrices(
            rowCount = matrix1.rowCount,
            firstColCount = matrix1.colCount,
            secondColCount = matrix2.colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.joinMatrices(matrix1, matrix2)
        }
    }

    override fun computeRowSlice(matrix: M, rowRange: IntRange): M {
        val input = MatrixOperationInput.ComputeRowSlice(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
            rangeSize = rowRange.toList().size,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.computeRowSlice(matrix, rowRange)
        }
    }

    override fun computeColSlice(matrix: M, colRange: IntRange): M {
        val input = MatrixOperationInput.ComputeColSlice(
            rowCount = matrix.rowCount,
            colCount = matrix.colCount,
            rangeSize = colRange.toList().size,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.computeColSlice(matrix, colRange)
        }
    }

    override fun fromRowList(rowList: List<List<S>>, colCount: Int?): M {
        val input = MatrixOperationInput.FromRowList(
            rowCount = rowList.size,
            colCount = colCount ?: rowList[0].size,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.fromRowList(rowList, colCount)
        }
    }

    override fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M {
        val input = MatrixOperationInput.FromRowMap(
            rowCount = rowCount,
            colCount = colCount,
        )
        return this.logger.measureOperation(input) {
            this.originalMatrixSpace.fromRowMap(rowMap, rowCount, colCount)
        }
    }

    override fun toString(): String {
        return "MatrixSpaceWithLog(${this.originalMatrixSpace})"
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
MatrixSpace<S, V, M>.withLog(): MatrixSpaceWithLog<S, V, M> {
    return MatrixSpaceWithLog(this)
}
