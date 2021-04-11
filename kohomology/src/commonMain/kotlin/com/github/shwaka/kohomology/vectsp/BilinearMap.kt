package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

// TODO: make sparse
class MatrixSequence<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val matrixList: List<M>,
    val rowCount: Int,
    val colCount: Int,
) {
    val size: Int = this.matrixList.size
    init {
        for (matrix in this.matrixList) {
            if (matrix.rowCount != this.rowCount)
                throw IllegalArgumentException("invalid matrix size")
            if (matrix.colCount != this.colCount)
                throw IllegalArgumentException("invalid matrix size")
        }
    }
    fun multiply(numVector1: V, numVector2: V): V {
        val valueList = this.matrixSpace.context.run {
            matrixList.map { matrix -> matrix.innerProduct(numVector1, numVector2) }
        }
        return matrixSpace.numVectorSpace.fromValues(valueList)
    }
}

class BilinearMap<BS1, BS2, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    val source1: VectorSpace<BS1, S, V>,
    val source2: VectorSpace<BS2, S, V>,
    val target: VectorSpace<BT, S, V>,
    private val matrixSequence: MatrixSequence<S, V, M>,
) {
    init {
        if (matrixSequence.rowCount != source1.dim)
            throw IllegalArgumentException("The rowCount of the matrix list does not match the dim of the first source vector space")
        if (matrixSequence.colCount != source2.dim)
            throw IllegalArgumentException("The rowCount of the matrix list does not match the dim of the second source vector space")
        if (matrixSequence.size != target.dim)
            throw IllegalArgumentException("The size of the matrix list does not match the dim of the target vector space")
    }

    operator fun invoke(vector1: Vector<BS1, S, V>, vector2: Vector<BS2, S, V>): Vector<BT, S, V> {
        val numVector: V = this.matrixSequence.multiply(vector1.numVector, vector2.numVector)
        return target.fromNumVector(numVector)
    }

    companion object {
        fun <BS1, BS2, BT, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
            source1: VectorSpace<BS1, S, V>,
            source2: VectorSpace<BS2, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            vectors: List<List<Vector<BT, S, V>>>,
        ): BilinearMap<BS1, BS2, BT, S, V, M> {
            val rowCount = source1.dim
            val colCount = source2.dim
            val matrixList: List<M> = (0 until target.dim).map { k ->
                val rows: List<List<S>> = matrixSpace.context.run {
                    (0 until rowCount).map { i ->
                        (0 until colCount).map { j ->
                            vectors[i][j].numVector[k]
                        }
                    }
                }
                matrixSpace.fromRows(rows, colCount)
            }
            val matrixSequence = MatrixSequence(matrixSpace, matrixList, rowCount, colCount)
            return BilinearMap(source1, source2, target, matrixSequence)
        }
    }
}
