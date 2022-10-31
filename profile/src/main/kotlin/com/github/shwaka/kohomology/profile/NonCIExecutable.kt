package com.github.shwaka.kohomology.profile

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

class ComputeReducedRowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val dim: Int,
) : Executable() {
    override val description = "compute row echelon form with $matrixSpace"
    private var matrix: M? = null

    override fun setupFun() {
        val dim = this.dim
        val rowMap = this.matrixSpace.context.run {
            // Jordan matrix
            val eigenValue = one
            (0 until dim).map { rowInd ->
                val row = if (rowInd < dim - 1) {
                    mapOf(rowInd to eigenValue, (rowInd + 1) to one)
                } else {
                    mapOf(rowInd to eigenValue)
                }
                Pair(rowInd, row)
            }.toMap()
        }
        this.matrix = this.matrixSpace.fromRowMap(rowMap, rowCount = dim, colCount = dim)
    }

    override fun mainFun(): String {
        val matrix: M = this.matrix ?: throw Exception("setupFun() is not called")
        return this.matrixSpace.context.run {
            matrix.rowEchelonForm.reducedMatrix[0, 0].toString() // 全体を toString() すると重い
        }
    }
}
