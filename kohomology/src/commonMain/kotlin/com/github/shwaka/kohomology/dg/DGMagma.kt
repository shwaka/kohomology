package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Field
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface DGMagmaContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGVectorContext<D, B, S, V, M>, GMagmaContext<D, B, S, V, M> {
    public val dgMagma: DGMagma<D, B, S, V, M>

    override operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@DGMagmaContext.run { this@times * other }
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
            matrixSpace: MatrixSpace<S, V, M>
        ): DGMagma<D, B, S, V, M> {
            return DGMagmaImpl(gMagma, differential, matrixSpace)
        }
    }
}

internal open class DGMagmaImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gMagma: GMagma<D, B, S, V, M>,
    differential: GLinearMap<D, B, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGMagma<D, B, S, V, M>,
    DGVectorSpace<D, B, S, V, M> by DGVectorSpace(gMagma, differential, matrixSpace) {
    override val context: DGMagmaContext<D, B, S, V, M> by lazy {
        DGMagmaContextImpl(this)
    }

    override fun multiply(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this.gMagma.multiply(a, b)
    }

    override fun getIdentity(): DGLinearMap<D, B, B, S, V, M> {
        return super<DGMagma>.getIdentity()
    }

    protected fun getCohomologyMultiplication(p: D, q: D): BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        val cohomOfDegP = this.cohomology[p]
        val cohomOfDegQ = this.cohomology[q]
        val cohomOfDegPPlusQ = this.cohomology[this.degreeGroup.context.run { p + q }]
        val basisLift1: List<Vector<B, S, V>> =
            cohomOfDegP.getBasis().map { vector1: Vector<SubQuotBasis<B, S, V>, S, V> ->
                cohomOfDegP.section(vector1)
            }
        val basisLift2: List<Vector<B, S, V>> =
            cohomOfDegQ.getBasis().map { vector2: Vector<SubQuotBasis<B, S, V>, S, V> ->
                cohomOfDegQ.section(vector2)
            }
        val valueList: List<List<Vector<SubQuotBasis<B, S, V>, S, V>>> =
            basisLift1.map { vector1: Vector<B, S, V> ->
                basisLift2.map { vector2: Vector<B, S, V> ->
                    cohomOfDegPPlusQ.projection(
                        // TODO: GVector を経由しているのは無駄
                        this.multiply(this.fromVector(vector1, p), this.fromVector(vector2, q)).vector
                    )
                }
            }
        return ValueBilinearMap(
            cohomOfDegP,
            cohomOfDegQ,
            cohomOfDegPPlusQ,
            this.matrixSpace,
            valueList
        )
    }

    override val cohomology: SubQuotGMagma<D, B, S, V, M> by lazy {
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.getInternalPrintConfig(printConfig))
        }
        SubQuotGMagma(
            matrixSpace,
            this.degreeGroup,
            this.cohomologyName,
            this.cohomology::get,
            this::getCohomologyMultiplication,
            listDegreesForAugmentedDegree = this.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }
}
