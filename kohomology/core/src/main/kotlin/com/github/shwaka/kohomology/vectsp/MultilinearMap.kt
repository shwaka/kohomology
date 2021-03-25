package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

// TODO: make sparse
class MatrixSequence<S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
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
        val values = this.matrixSpace.withContext {
            matrixList.map { matrix -> matrix.innerProduct(numVector1, numVector2) }
        }
        return matrixSpace.numVectorSpace.fromValues(values)
    }
}

class MultilinearMap<B0, B1, B2, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> private constructor(
    val source0: VectorSpace<B0, S, V>,
    val source1: VectorSpace<B1, S, V>,
    val target: VectorSpace<B2, S, V>,
    private val matrixSequence: MatrixSequence<S, V, M>,
) {
    init {
        if (matrixSequence.rowCount != source0.dim)
            throw IllegalArgumentException("The rowCount of the matrix list does not match the dim of the first source vector space")
        if (matrixSequence.colCount != source1.dim)
            throw IllegalArgumentException("The rowCount of the matrix list does not match the dim of the second source vector space")
        if (matrixSequence.size != target.dim)
            throw IllegalArgumentException("The size of the matrix list does not match the dim of the target vector space")
    }

    operator fun invoke(vector1: Vector<B0, S, V>, vector2: Vector<B1, S, V>): Vector<B2, S, V> {
        val numVector: V = this.matrixSequence.multiply(vector1.numVector, vector2.numVector)
        return target.fromNumVector(numVector)
    }

    companion object {
        fun <B0, B1, B2, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> fromVectors(
            source0: VectorSpace<B0, S, V>,
            source1: VectorSpace<B1, S, V>,
            target: VectorSpace<B2, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            vectors: List<List<Vector<B2, S, V>>>,
        ): MultilinearMap<B0, B1, B2, S, V, M> {
            val rowCount = source0.dim
            val colCount = source1.dim
            val matrixList: List<M> = (0 until target.dim).map { k ->
                val rows: List<List<S>> = matrixSpace.withContext {
                    (0 until rowCount).map { i ->
                        (0 until colCount).map { j ->
                            vectors[i][j].numVector[k]
                        }
                    }
                }
                matrixSpace.fromRows(rows)
            }
            val matrixSequence = MatrixSequence(matrixSpace, matrixList, rowCount, colCount)
            return MultilinearMap(source0, source1, target, matrixSequence)
        }
    }
}
