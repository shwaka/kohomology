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
    public fun transpose(): BilinearMap<BS2, BS1, BT, S, V, M>

    public fun induce(
        source1Sub: SubVectorSpace<BS1, S, V, M>,
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>,
    ): BilinearMap<
        SubBasis<BS1, S, V>,
        SubBasis<BS2, S, V>,
        SubBasis<BT, S, V>,
        S, V, M,
        >

    // Used in SubModule
    public fun induce(
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>,
    ): BilinearMap<
        BS1,
        SubBasis<BS2, S, V>,
        SubBasis<BT, S, V>,
        S, V, M,
        >

    public fun induce(
        source1Quot: QuotVectorSpace<BS1, S, V, M>,
        source2Quot: QuotVectorSpace<BS2, S, V, M>,
        targetQuot: QuotVectorSpace<BT, S, V, M>,
    ): BilinearMap<
        QuotBasis<BS1, S, V>,
        QuotBasis<BS2, S, V>,
        QuotBasis<BT, S, V>,
        S, V, M,
        >

    public fun induce(
        source1SubQuot: SubQuotVectorSpace<BS1, S, V, M>,
        source2SubQuot: SubQuotVectorSpace<BS2, S, V, M>,
        targetSubQuot: SubQuotVectorSpace<BT, S, V, M>,
    ): BilinearMap<
        SubQuotBasis<BS1, S, V>,
        SubQuotBasis<BS2, S, V>,
        SubQuotBasis<BT, S, V>,
        S, V, M,
        >

    public fun image(
        source1Sub: SubVectorSpace<BS1, S, V, M> = this.source1.asSubVectorSpace(this.matrixSpace),
        source2Sub: SubVectorSpace<BS2, S, V, M> = this.source2.asSubVectorSpace(this.matrixSpace),
    ): SubVectorSpace<BT, S, V, M> {
        val imageGenerator: List<Vector<BT, S, V>> = source1Sub.generator.map { vector1: Vector<BS1, S, V> ->
            source2Sub.generator.map { vector2: Vector<BS2, S, V> ->
                this(vector1, vector2)
            }
        }.flatten()
        return SubVectorSpace(
            this.matrixSpace,
            totalVectorSpace = this.target,
            generator = imageGenerator,
        )
    }

    public companion object {
        public fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            source1: VectorSpace<BS1, S, V>,
            source2: VectorSpace<BS2, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
        ): BilinearMap<BS1, BS2, BT, S, V, M> {
            return LazyBilinearMap(source1, source2, target, matrixSpace) { _, _ ->
                target.zeroVector
            }
        }
    }
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
        require(vector1 in this.source1) {
            "Invalid vector is given as an argument of BilinearMap: $vector1 is not an element of ${this.source1}"
        }
        require(vector2 in this.source2) {
            "Invalid vector is given as an argument of BilinearMap: $vector2 is not an element of ${this.source2}"
        }
        return this.target.context.run {
            vector1.numVector.toMap().mapValues { (ind1, coeff1) ->
                vector2.numVector.toMap().mapValues { (ind2, coeff2) ->
                    this@ValueBilinearMap.values[ind1][ind2] * coeff1 * coeff2
                }.values.sum()
            }.values.sum()
        }
    }

    override fun transpose(): ValueBilinearMap<BS2, BS1, BT, S, V, M> {
        val transposedValues = (0 until this.source2.dim).map { index2 ->
            (0 until this.source1.dim).map { index1 ->
                this.values[index1][index2]
            }
        }
        return ValueBilinearMap(
            source1 = this.source2,
            source2 = this.source1,
            target = this.target,
            matrixSpace = this.matrixSpace,
            values = transposedValues,
        )
    }

    override fun induce(
        source1Sub: SubVectorSpace<BS1, S, V, M>,
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>
    ): ValueBilinearMap<SubBasis<BS1, S, V>, SubBasis<BS2, S, V>, SubBasis<BT, S, V>, S, V, M> {
        return ValueBilinearMap.getInducedMap(
            this,
            source1Sub, source2Sub,
            targetSub,
        )
    }

    override fun induce(
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>
    ): BilinearMap<BS1, SubBasis<BS2, S, V>, SubBasis<BT, S, V>, S, V, M> {
        return ValueBilinearMap.getInducedMap(
            this,
            this.source1, source2Sub,
            targetSub,
        )
    }

    override fun induce(
        source1Quot: QuotVectorSpace<BS1, S, V, M>,
        source2Quot: QuotVectorSpace<BS2, S, V, M>,
        targetQuot: QuotVectorSpace<BT, S, V, M>
    ): ValueBilinearMap<QuotBasis<BS1, S, V>, QuotBasis<BS2, S, V>, QuotBasis<BT, S, V>, S, V, M> {
        return ValueBilinearMap.getInducedMap(
            this,
            source1Quot, source2Quot,
            targetQuot,
        )
    }

    override fun induce(
        source1SubQuot: SubQuotVectorSpace<BS1, S, V, M>,
        source2SubQuot: SubQuotVectorSpace<BS2, S, V, M>,
        targetSubQuot: SubQuotVectorSpace<BT, S, V, M>
    ): ValueBilinearMap<SubQuotBasis<BS1, S, V>, SubQuotBasis<BS2, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        return ValueBilinearMap.getInducedMap(
            this,
            source1SubQuot, source2SubQuot,
            targetSubQuot,
        )
    }

    public companion object {
        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1Sub: SubVectorSpace<BS1, S, V, M>,
            source2Sub: SubVectorSpace<BS2, S, V, M>,
            targetSub: SubVectorSpace<BT, S, V, M>,
        ): ValueBilinearMap<
            SubBasis<BS1, S, V>,
            SubBasis<BS2, S, V>,
            SubBasis<BT, S, V>,
            S, V, M,
            > {
            val basisIncl1: List<Vector<BS1, S, V>> =
                source1Sub.getBasis().map { subVector1: Vector<SubBasis<BS1, S, V>, S, V> ->
                    source1Sub.inclusion(subVector1)
                }
            val basisIncl2: List<Vector<BS2, S, V>> =
                source2Sub.getBasis().map { subVector2: Vector<SubBasis<BS2, S, V>, S, V> ->
                    source2Sub.inclusion(subVector2)
                }
            val valueList: List<List<Vector<SubBasis<BT, S, V>, S, V>>> =
                basisIncl1.map { vector1: Vector<BS1, S, V> ->
                    basisIncl2.map { vector2: Vector<BS2, S, V> ->
                        targetSub.retraction(
                            bilinearMap(vector1, vector2)
                        )
                    }
                }
            return ValueBilinearMap(
                source1Sub,
                source2Sub,
                targetSub,
                bilinearMap.matrixSpace,
                valueList,
            )
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1: VectorSpace<BS1, S, V>,
            source2Sub: SubVectorSpace<BS2, S, V, M>,
            targetSub: SubVectorSpace<BT, S, V, M>,
        ): ValueBilinearMap<
            BS1,
            SubBasis<BS2, S, V>,
            SubBasis<BT, S, V>,
            S, V, M,
            > {
            val basis1: List<Vector<BS1, S, V>> = source1.getBasis()
            val basisIncl2: List<Vector<BS2, S, V>> =
                source2Sub.getBasis().map { subVector2: Vector<SubBasis<BS2, S, V>, S, V> ->
                    source2Sub.inclusion(subVector2)
                }
            val valueList: List<List<Vector<SubBasis<BT, S, V>, S, V>>> =
                basis1.map { vector1: Vector<BS1, S, V> ->
                    basisIncl2.map { vector2: Vector<BS2, S, V> ->
                        targetSub.retraction(
                            bilinearMap(vector1, vector2)
                        )
                    }
                }
            return ValueBilinearMap(
                source1,
                source2Sub,
                targetSub,
                bilinearMap.matrixSpace,
                valueList,
            )
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1Quot: QuotVectorSpace<BS1, S, V, M>,
            source2Quot: QuotVectorSpace<BS2, S, V, M>,
            targetQuot: QuotVectorSpace<BT, S, V, M>,
        ): ValueBilinearMap<
            QuotBasis<BS1, S, V>,
            QuotBasis<BS2, S, V>,
            QuotBasis<BT, S, V>,
            S, V, M,
            > {
            val basisLift1: List<Vector<BS1, S, V>> =
                source1Quot.getBasis().map { quotVector1: Vector<QuotBasis<BS1, S, V>, S, V> ->
                    source1Quot.section(quotVector1)
                }
            val basisLift2: List<Vector<BS2, S, V>> =
                source2Quot.getBasis().map { quotVector2: Vector<QuotBasis<BS2, S, V>, S, V> ->
                    source2Quot.section(quotVector2)
                }
            val valueList: List<List<Vector<QuotBasis<BT, S, V>, S, V>>> =
                basisLift1.map { vector1: Vector<BS1, S, V> ->
                    basisLift2.map { vector2: Vector<BS2, S, V> ->
                        targetQuot.projection(
                            bilinearMap(vector1, vector2)
                        )
                    }
                }
            return ValueBilinearMap(
                source1Quot,
                source2Quot,
                targetQuot,
                bilinearMap.matrixSpace,
                valueList,
            )
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1SubQuot: SubQuotVectorSpace<BS1, S, V, M>,
            source2SubQuot: SubQuotVectorSpace<BS2, S, V, M>,
            targetSubQuot: SubQuotVectorSpace<BT, S, V, M>,
        ): ValueBilinearMap<
            SubQuotBasis<BS1, S, V>,
            SubQuotBasis<BS2, S, V>,
            SubQuotBasis<BT, S, V>,
            S, V, M,
            > {
            val basisLift1: List<Vector<BS1, S, V>> =
                source1SubQuot.getBasis().map { subQuotVector1: Vector<SubQuotBasis<BS1, S, V>, S, V> ->
                    source1SubQuot.section(subQuotVector1)
                }
            val basisLift2: List<Vector<BS2, S, V>> =
                source2SubQuot.getBasis().map { subQuotVector2: Vector<SubQuotBasis<BS2, S, V>, S, V> ->
                    source2SubQuot.section(subQuotVector2)
                }
            val valueList: List<List<Vector<SubQuotBasis<BT, S, V>, S, V>>> =
                basisLift1.map { vector1: Vector<BS1, S, V> ->
                    basisLift2.map { vector2: Vector<BS2, S, V> ->
                        targetSubQuot.projection(
                            bilinearMap(vector1, vector2)
                        )
                    }
                }
            return ValueBilinearMap(
                source1SubQuot,
                source2SubQuot,
                targetSubQuot,
                bilinearMap.matrixSpace,
                valueList,
            )
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
        require(vector1 in this.source1) {
            "Invalid vector is given as an argument of BilinearMap: $vector1 is not an element of ${this.source1}"
        }
        require(vector2 in this.source2) {
            "Invalid vector is given as an argument of BilinearMap: $vector2 is not an element of ${this.source2}"
        }
        return this.target.context.run {
            vector1.toBasisMap().mapValues { (basisName1, coeff1) ->
                vector2.toBasisMap().mapValues { (basisName2, coeff2) ->
                    this@LazyBilinearMap.getValue(basisName1, basisName2) * coeff1 * coeff2
                }.values.sum()
            }.values.sum()
        }
    }

    override fun transpose(): LazyBilinearMap<BS2, BS1, BT, S, V, M> {
        return LazyBilinearMap(
            source1 = this.source2,
            source2 = this.source1,
            target = this.target,
            matrixSpace = this.matrixSpace,
        ) { basisName2, basisName1 -> this.getValue(basisName1, basisName2) }
    }

    override fun induce(
        source1Sub: SubVectorSpace<BS1, S, V, M>,
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>
    ): LazyBilinearMap<SubBasis<BS1, S, V>, SubBasis<BS2, S, V>, SubBasis<BT, S, V>, S, V, M> {
        return LazyBilinearMap.getInducedMap(
            this,
            source1Sub, source2Sub,
            targetSub,
        )
    }

    override fun induce(
        source2Sub: SubVectorSpace<BS2, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>
    ): BilinearMap<BS1, SubBasis<BS2, S, V>, SubBasis<BT, S, V>, S, V, M> {
        return LazyBilinearMap.getInducedMap(
            this,
            this.source1, source2Sub,
            targetSub,
        )
    }

    override fun induce(
        source1Quot: QuotVectorSpace<BS1, S, V, M>,
        source2Quot: QuotVectorSpace<BS2, S, V, M>,
        targetQuot: QuotVectorSpace<BT, S, V, M>
    ): LazyBilinearMap<QuotBasis<BS1, S, V>, QuotBasis<BS2, S, V>, QuotBasis<BT, S, V>, S, V, M> {
        return LazyBilinearMap.getInducedMap(
            this,
            source1Quot, source2Quot,
            targetQuot,
        )
    }

    override fun induce(
        source1SubQuot: SubQuotVectorSpace<BS1, S, V, M>,
        source2SubQuot: SubQuotVectorSpace<BS2, S, V, M>,
        targetSubQuot: SubQuotVectorSpace<BT, S, V, M>
    ): LazyBilinearMap<SubQuotBasis<BS1, S, V>, SubQuotBasis<BS2, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        return LazyBilinearMap.getInducedMap(
            this,
            source1SubQuot, source2SubQuot,
            targetSubQuot,
        )
    }

    public companion object {
        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1Sub: SubVectorSpace<BS1, S, V, M>,
            source2Sub: SubVectorSpace<BS2, S, V, M>,
            targetSub: SubVectorSpace<BT, S, V, M>,
        ): LazyBilinearMap<
            SubBasis<BS1, S, V>,
            SubBasis<BS2, S, V>,
            SubBasis<BT, S, V>,
            S, V, M,
            > {
            return LazyBilinearMap(
                source1Sub, source2Sub,
                targetSub,
                bilinearMap.matrixSpace,
            ) { subBasisName1, subBasisName2 ->
                val vector1 = subBasisName1.vector
                val vector2 = subBasisName2.vector
                targetSub.retraction(bilinearMap(vector1, vector2))
            }
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1: VectorSpace<BS1, S, V>,
            source2Sub: SubVectorSpace<BS2, S, V, M>,
            targetSub: SubVectorSpace<BT, S, V, M>,
        ): LazyBilinearMap<
            BS1,
            SubBasis<BS2, S, V>,
            SubBasis<BT, S, V>,
            S, V, M,
            > {
            return LazyBilinearMap(
                source1, source2Sub,
                targetSub,
                bilinearMap.matrixSpace,
            ) { basisName1, subBasisName2 ->
                val vector1 = source1.fromBasisName(basisName1)
                val vector2 = subBasisName2.vector
                targetSub.retraction(bilinearMap(vector1, vector2))
            }
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1Quot: QuotVectorSpace<BS1, S, V, M>,
            source2Quot: QuotVectorSpace<BS2, S, V, M>,
            targetQuot: QuotVectorSpace<BT, S, V, M>,
        ): LazyBilinearMap<
            QuotBasis<BS1, S, V>,
            QuotBasis<BS2, S, V>,
            QuotBasis<BT, S, V>,
            S, V, M,
            > {
            return LazyBilinearMap(
                source1Quot, source2Quot,
                targetQuot,
                bilinearMap.matrixSpace,
            ) { quotBasisName1, quotBasisName2 ->
                val vector1 = quotBasisName1.vector
                val vector2 = quotBasisName2.vector
                targetQuot.projection(bilinearMap(vector1, vector2))
            }
        }

        private fun <BS1 : BasisName, BS2 : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getInducedMap(
            bilinearMap: BilinearMap<BS1, BS2, BT, S, V, M>,
            source1SubQuot: SubQuotVectorSpace<BS1, S, V, M>,
            source2SubQuot: SubQuotVectorSpace<BS2, S, V, M>,
            targetSubQuot: SubQuotVectorSpace<BT, S, V, M>,
        ): LazyBilinearMap<
            SubQuotBasis<BS1, S, V>,
            SubQuotBasis<BS2, S, V>,
            SubQuotBasis<BT, S, V>,
            S, V, M,
            > {
            return LazyBilinearMap(
                source1SubQuot, source2SubQuot,
                targetSubQuot,
                bilinearMap.matrixSpace,
            ) { subQuotBasisName1, subQuotBasisName2 ->
                val vector1 = subQuotBasisName1.vector
                val vector2 = subQuotBasisName2.vector
                targetSubQuot.projection(bilinearMap(vector1, vector2))
            }
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
