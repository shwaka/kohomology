package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface DGMagmaContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorContext<D, B, S, V, M>, GMagmaContext<D, B, S, V, M> {
    public val dgMagma: DGMagma<D, B, S, V, M>

    override operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@DGMagmaContext.dgMagma.multiply(this, other)
    }
}

internal class DGMagmaContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val dgMagma: DGMagma<D, B, S, V, M>,
) : DGMagmaContext<D, B, S, V, M> {
    // If we write
    //   DGVectorContext<D, B, S, V, M> by DGVectorContextImpl(dgMagma),
    //   GMagmaContext<D, B, S, V, M> by GMagmaContextImpl(dgMagma)
    // to implement the interfaces,
    // a lot of methods conflict.
    override val dgVectorSpace: DGVectorSpace<D, B, S, V, M> = dgMagma
    override val gMagma: GMagma<D, B, S, V, M> = dgMagma
    override val gVectorSpace: GVectorSpace<D, B, S, V> = gMagma
    override val numVectorSpace: NumVectorSpace<S, V> = gMagma.numVectorSpace
    override val field: Field<S> = gMagma.field
}

public interface DGMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorSpace<D, B, S, V, M>, GMagma<D, B, S, V, M> {
    override val context: DGMagmaContext<D, B, S, V, M>
    override val cohomology: SubQuotGMagma<D, B, S, V, M>

    /**
     * Returns a [DGLinearMap] which multiplies [cocycle] from left.
     *
     * [cocycle] must be a cocycle.
     * Use [GMagma.leftMultiplication] to get a [GLinearMap]
     * which multiplies a cochain (not cocycle) from left.
     */
    public fun leftMultiplicationByCocycle(cocycle: GVector<D, B, S, V>): DGLinearMap<D, B, B, S, V, M> {
        this.context.run {
            if (d(cocycle).isNotZero())
                throw IllegalArgumentException("Not cocycle: $cocycle (Use GMagma.leftMultiplication to multiply a non-cocycle)")
        }
        val gLinearMap = this.leftMultiplication(cocycle)
        return DGLinearMap(this, this, gLinearMap)
    }

    override fun getIdentity(): DGLinearMap<D, B, B, S, V, M> {
        val gLinearMap = this.getIdentity(this.matrixSpace)
        return DGLinearMap(this, this, gLinearMap)
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gMagma: GMagma<D, B, S, V, M>,
            differential: GLinearMap<D, B, B, S, V, M>,
        ): DGMagma<D, B, S, V, M> {
            val dgVectorSpace = DGVectorSpace(gMagma, differential)
            return DGMagmaImpl(gMagma, differential, gMagma.multiplication, dgVectorSpace.cohomology)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectorSpace(
            matrixSpace: MatrixSpace<S, V, M>,
            gVectorSpace: GVectorSpace<D, B, S, V>,
        ): DGMagma<D, B, S, V, M> {
            val multiplication = GBilinearMap.getZero(
                matrixSpace,
                source1 = gVectorSpace,
                source2 = gVectorSpace,
                target = gVectorSpace,
                degree = gVectorSpace.degreeGroup.zero
            )
            val gMagma = GMagma(matrixSpace, gVectorSpace, multiplication)
            val differential = GLinearMap.getZero(
                matrixSpace,
                source = gVectorSpace,
                target = gVectorSpace,
                degree = gVectorSpace.degreeGroup.fromInt(1),
            )
            return DGMagma(gMagma, differential)
        }
    }
}

internal class DGMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val differential: GLinearMap<D, B, B, S, V, M>,
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M>,
    private val cohomologyGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
) : DGMagma<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    override val context: DGMagmaContext<D, B, S, V, M> = DGMagmaContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = differential.matrixSpace

    private fun getCohomologyMultiplication(): GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M> {
        val bilinearMapName = "Multiplication(H^*($name))"
        return GBilinearMap(
            this.cohomologyGVectorSpace,
            this.cohomologyGVectorSpace,
            this.cohomologyGVectorSpace,
            0,
            bilinearMapName,
        ) { p, q ->
            getSubQuotMultiplicationAtDegree(
                this.cohomologyGVectorSpace,
                this.matrixSpace,
                this.multiplication,
                p, q,
            )
        }
    }

    override val cohomology: SubQuotGMagma<D, B, S, V, M> by lazy {
        SubQuotGMagma(
            matrixSpace,
            this.cohomologyGVectorSpace,
            this.getCohomologyMultiplication(),
        )
    }
}

private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
getSubQuotMultiplicationAtDegree(
    subQuotGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>, // TODO: use subQuotGVectorSpace.matrixSpace
    multiplication: GBilinearMap<B, B, B, D, S, V, M>,
    p: D, q: D,
): BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
    val subQuotAtDegP = subQuotGVectorSpace[p]
    val subQuotAtDegQ = subQuotGVectorSpace[q]
    val pPlusQ = subQuotGVectorSpace.degreeGroup.context.run { p + q }
    val subQuotAtDegPPlusQ = subQuotGVectorSpace[pPlusQ]
    val basisLift1: List<Vector<B, S, V>> =
        subQuotAtDegP.getBasis().map { vector1: Vector<SubQuotBasis<B, S, V>, S, V> ->
            subQuotAtDegP.section(vector1)
        }
    val basisLift2: List<Vector<B, S, V>> =
        subQuotAtDegQ.getBasis().map { vector2: Vector<SubQuotBasis<B, S, V>, S, V> ->
            subQuotAtDegQ.section(vector2)
        }
    val totalGVectorSpace = subQuotGVectorSpace.totalGVectorSpace
    val valueList: List<List<Vector<SubQuotBasis<B, S, V>, S, V>>> =
        basisLift1.map { vector1: Vector<B, S, V> ->
            basisLift2.map { vector2: Vector<B, S, V> ->
                subQuotAtDegPPlusQ.projection(
                    multiplication(
                        totalGVectorSpace.fromVector(vector1, p),
                        totalGVectorSpace.fromVector(vector2, q),
                    ).vector
                )
            }
        }
    return ValueBilinearMap(
        subQuotAtDegP,
        subQuotAtDegQ,
        subQuotAtDegPPlusQ,
        matrixSpace,
        valueList,
    )
}
