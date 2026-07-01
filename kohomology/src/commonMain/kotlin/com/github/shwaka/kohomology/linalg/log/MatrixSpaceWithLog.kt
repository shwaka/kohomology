package com.github.shwaka.kohomology.linalg.log

import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixContext
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import kotlin.time.measureTimedValue

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
        return this.originalMatrixSpace.add(first, second)
    }

    override fun subtract(first: M, second: M): M {
        return this.originalMatrixSpace.subtract(first, second)
    }

    override fun multiply(first: M, second: M): M {
        val (value, duration) = measureTimedValue {
            this.originalMatrixSpace.multiply(first, second)
        }
        val data = MultiplyMatrixLog(
            firstRowCount = first.rowCount,
            firstColCount = first.colCount,
            secondColCount = second.colCount,
        )
        this.logger.addEntry(MatrixLogEntry(duration, data))
        return value
    }

    override fun multiply(matrix: M, numVector: V): V {
        return this.originalMatrixSpace.multiply(matrix, numVector)
    }

    override fun multiply(matrix: M, scalar: S): M {
        return this.originalMatrixSpace.multiply(matrix, scalar)
    }

    override fun computeRowEchelonForm(matrix: M): RowEchelonForm<S, V, M> {
        return this.originalMatrixSpace.computeRowEchelonForm(matrix)
    }

    override fun computeTranspose(matrix: M): M {
        return this.originalMatrixSpace.computeTranspose(matrix)
    }

    override fun joinMatrices(matrix1: M, matrix2: M): M {
        return this.originalMatrixSpace.joinMatrices(matrix1, matrix2)
    }

    override fun computeRowSlice(matrix: M, rowRange: IntRange): M {
        return this.originalMatrixSpace.computeRowSlice(matrix, rowRange)
    }

    override fun computeColSlice(matrix: M, colRange: IntRange): M {
        return this.originalMatrixSpace.computeColSlice(matrix, colRange)
    }

    override fun fromRowList(rowList: List<List<S>>, colCount: Int?): M {
        return this.originalMatrixSpace.fromRowList(rowList, colCount)
    }

    override fun fromRowMap(rowMap: Map<Int, Map<Int, S>>, rowCount: Int, colCount: Int): M {
        return this.originalMatrixSpace.fromRowMap(rowMap, rowCount, colCount)
    }

    override fun toString(): String {
        return "MatrixSpaceWithLog(${this.originalMatrixSpace})"
    }
}

public fun <S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
MatrixSpace<S, V, M>.withLog(): MatrixSpaceWithLog<S, V, M> {
    return MatrixSpaceWithLog(this)
}
