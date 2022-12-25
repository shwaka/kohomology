package com.github.shwaka.kohomology.profile.executable

import com.github.shwaka.kohomology.example.pullbackOfHopfFibrationOverS4
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.model.FreeLoopSpace

class ComputeRowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>
) : Executable() {
    override val description: String = "compute row echelon form of a differential of a DGA with $matrixSpace"

    private var matrix: M? = null

    override fun setupFun() {
        // val sphereDim = 2
        // val freeDGAlgebra = sphere(this.matrixSpace, sphereDim)
        val freeDGAlgebra = pullbackOfHopfFibrationOverS4(this.matrixSpace)
        val freeLoopSpace = FreeLoopSpace(freeDGAlgebra)
        val degree = 23
        this.matrix = freeLoopSpace.differential[degree].matrix
    }

    override fun mainFun(): String {
        val matrix: M = this.matrix ?: throw Exception("setupFun() is not called")
        return this.matrixSpace.context.run {
            println("${matrix.rowCount}, ${matrix.colCount}")
            matrix.rowEchelonForm.reducedMatrix[0, 0].toString() // 全体を toString() すると重い
        }
    }
}

class ComputeReducedRowEchelonForm<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val dim: Int,
) : Executable() {
    override val description = "compute reduced row echelon form of a Jordan matrix with $matrixSpace"
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
