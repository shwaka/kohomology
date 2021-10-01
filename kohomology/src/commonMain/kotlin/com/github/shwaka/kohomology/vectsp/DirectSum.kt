package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

public data class DirectSumBasis<B>(val index: Int, val basisName: B) : BasisName

private class DirectSumFactory<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val vectorSpaceList: List<VectorSpace<B, S, V>>,
    val matrixSpace: MatrixSpace<S, V, M>,
) {
    val numVectorSpace: NumVectorSpace<S, V> = matrixSpace.numVectorSpace
    // TODO: vectorSpaceList から取得できる numVectorSpace たちと一致しているかチェックする？

    val basisNames: List<DirectSumBasis<B>> = vectorSpaceList.mapIndexed { index, vectorSpace ->
        vectorSpace.basisNames.map { basisName ->
            DirectSumBasis(index, basisName)
        }
    }.flatten()

    // TODO: internalPrintConfig も渡す？
}

public class DirectSum<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: DirectSumFactory<B, S, V, M>
) : VectorSpace<DirectSumBasis<B>, S, V>(factory.numVectorSpace, factory.basisNames) {
    public val vectorSpaceList: List<VectorSpace<B, S, V>> = factory.vectorSpaceList
    public val matrixSpace: MatrixSpace<S, V, M> = factory.matrixSpace
    public val size: Int = vectorSpaceList.size

    public constructor(
        vectorSpaceList: List<VectorSpace<B, S, V>>,
        matrixSpace: MatrixSpace<S, V, M>,
    ) : this(DirectSumFactory(vectorSpaceList, matrixSpace))

    private val inclusionList: List<LinearMap<B, DirectSumBasis<B>, S, V, M>> by lazy {
        val one = this.matrixSpace.field.one
        (0 until this.size).map { index ->
            // ↓累積和にした方が速いけど、その必要ある？
            val accumulatedDim = (0 until index).map { this.vectorSpaceList[it].dim }.sum()
            val currentDim = this.vectorSpaceList[index].dim
            val rowMap: Map<Int, Map<Int, S>> = (0 until currentDim).map { colIndex ->
                Pair(accumulatedDim + colIndex, mapOf(colIndex to one))
            }.toMap()
            val matrix = this.matrixSpace.fromRowMap(rowMap, rowCount = this.dim, colCount = currentDim)
            LinearMap.fromMatrix(this.vectorSpaceList[index], this, this.matrixSpace, matrix)
        }
    }

    public fun inclusion(index: Int): LinearMap<B, DirectSumBasis<B>, S, V, M> {
        if (index < 0)
            throw IllegalArgumentException("index must be non-negative")
        if (index >= this.size)
            throw IllegalArgumentException("index must be smaller than the number of vector spaces in the direct sum")
        return inclusionList[index]
    }
}
