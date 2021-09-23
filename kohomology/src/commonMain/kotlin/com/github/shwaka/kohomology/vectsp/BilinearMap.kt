package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

public interface BilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val source1: VectorSpace<BS1, S, V>
    public val source2: VectorSpace<BS2, S, V>
    public val target: VectorSpace<BT, S, V>
    public val matrixSpace: MatrixSpace<S, V, M>
    public operator fun invoke(vector1: Vector<BS1, S, V>, vector2: Vector<BS2, S, V>): Vector<BT, S, V>
}

public class ValueBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source1: VectorSpace<BS1, S, V>,
    override val source2: VectorSpace<BS2, S, V>,
    override val target: VectorSpace<BT, S, V>,
    override val matrixSpace: MatrixSpace<S, V, M>,
    private val values: List<List<Vector<BT, S, V>>>,
) : BilinearMap<BS1, BS2, BT, S, V, M> {
    init {
        // TODO: check rowCount, colCount, and dim of 'values'
    }

    public constructor(
        source1: VectorSpace<BS1, S, V>,
        source2: VectorSpace<BS2, S, V>,
        target: VectorSpace<BT, S, V>,
        matrixSpace: MatrixSpace<S, V, M>,
        getValue: (BS1, BS2) -> Vector<BT, S, V>,
    ) : this(
        source1,
        source2,
        target,
        matrixSpace,
        source1.basisNames.map { basisName1 ->
            source2.basisNames.map { basisName2 ->
                getValue(basisName1, basisName2)
            }
        }
    )

    override operator fun invoke(vector1: Vector<BS1, S, V>, vector2: Vector<BS2, S, V>): Vector<BT, S, V> {
        return this.target.context.run {
            vector1.numVector.toMap().mapValues { (ind1, coeff1) ->
                vector2.numVector.toMap().mapValues { (ind2, coeff2) ->
                    this@ValueBilinearMap.values[ind1][ind2] * coeff1 * coeff2
                }.values.sum()
            }.values.sum()
        }
    }
}

public class LazyBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source1: VectorSpace<BS1, S, V>,
    override val source2: VectorSpace<BS2, S, V>,
    override val target: VectorSpace<BT, S, V>,
    override val matrixSpace: MatrixSpace<S, V, M>,
    private val getValue: (BS1, BS2) -> Vector<BT, S, V>,
) : BilinearMap<BS1, BS2, BT, S, V, M> {
    override fun invoke(vector1: Vector<BS1, S, V>, vector2: Vector<BS2, S, V>): Vector<BT, S, V> {
        return this.target.context.run {
            vector1.toBasisMap().mapValues { (basisName1, coeff1) ->
                vector2.toBasisMap().mapValues { (basisName2, coeff2) ->
                    this@LazyBilinearMap.getValue(basisName1, basisName2) * coeff1 * coeff2
                }.values.sum()
            }.values.sum()
        }
    }
}

// class MatrixSequence<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
//     val matrixSpace: MatrixSpace<S, V, M>,
//     matrixMap: Map<Int, M>,
//     val size: Int,
//     val rowCount: Int,
//     val colCount: Int,
// ) {
//     private val matrixMap: Map<Int, M> = matrixMap.filterValues { it.isNotZero() }
//     constructor(
//         matrixSpace: MatrixSpace<S, V, M>,
//         matrixList: List<M>,
//         rowCount: Int,
//         colCount: Int,
//     ) : this(
//         matrixSpace,
//         matrixList.mapIndexed { index, matrix -> Pair(index, matrix) }.toMap(),
//         matrixList.size,
//         rowCount,
//         colCount
//     )
//
//     init {
//         for (matrix in this.matrixMap.values) {
//             if (matrix.rowCount != this.rowCount)
//                 throw InvalidSizeException("invalid matrix size")
//             if (matrix.colCount != this.colCount)
//                 throw InvalidSizeException("invalid matrix size")
//         }
//     }
//     fun multiply(numVector1: V, numVector2: V): V {
//         val valueMap = this.matrixSpace.context.run {
//             matrixMap.mapValues { (_, matrix) -> matrix.innerProduct(numVector1, numVector2) }
//         }
//         return matrixSpace.numVectorSpace.fromValueMap(valueMap, this.size)
//     }
// }
//
// class OldBilinearMap<BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
//     val source1: VectorSpace<BS1, S, V>,
//     val source2: VectorSpace<BS2, S, V>,
//     val target: VectorSpace<BT, S, V>,
//     private val matrixSequence: MatrixSequence<S, V, M>,
// ) {
//     init {
//         if (matrixSequence.rowCount != source1.dim)
//             throw InvalidSizeException("The rowCount of the matrix list does not match the dim of the first source vector space")
//         if (matrixSequence.colCount != source2.dim)
//             throw InvalidSizeException("The rowCount of the matrix list does not match the dim of the second source vector space")
//         if (matrixSequence.size != target.dim)
//             throw InvalidSizeException("The size of the matrix list does not match the dim of the target vector space")
//     }
//
//     operator fun invoke(vector1: Vector<BS1, S, V>, vector2: Vector<BS2, S, V>): Vector<BT, S, V> {
//         val numVector: V = this.matrixSequence.multiply(vector1.numVector, vector2.numVector)
//         return target.fromNumVector(numVector)
//     }
//
//     companion object {
//         fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
//             source1: VectorSpace<BS1, S, V>,
//             source2: VectorSpace<BS2, S, V>,
//             target: VectorSpace<BT, S, V>,
//             matrixSpace: MatrixSpace<S, V, M>,
//             vectors: List<List<Vector<BT, S, V>>>,
//         ): OldBilinearMap<BS1, BS2, BT, S, V, M> {
//             val rowCount = source1.dim
//             val colCount = source2.dim
//             val matrixList: List<M> = (0 until target.dim).map { k ->
//                 val rows: List<List<S>> = matrixSpace.context.run {
//                     (0 until rowCount).map { i ->
//                         (0 until colCount).map { j ->
//                             vectors[i][j].numVector[k]
//                         }
//                     }
//                 }
//                 matrixSpace.fromRowList(rows, colCount)
//             }
//             val matrixSequence = MatrixSequence(matrixSpace, matrixList, rowCount, colCount)
//             return OldBilinearMap(source1, source2, target, matrixSequence)
//         }
//     }
// }
