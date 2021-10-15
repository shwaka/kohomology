package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public open class DGMagmaContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
    dgVectorOperations: DGVectorOperations<D, B, S, V, M>
) : DGVectorContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, dgVectorOperations) {
    private val gMagmaContext = GMagmaContext(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations)

    public operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@DGMagmaContext.gMagmaContext.run { this@times * other }
    }
}

public open class DGMagma<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public open val gMagma: GMagma<D, B, S, V, M>,
    differential: GLinearMap<D, B, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGVectorSpace<D, B, S, V, M>(gMagma, differential, matrixSpace) {
    override val context: DGMagmaContext<D, B, S, V, M> by lazy {
        DGMagmaContext(this.gMagma.field, this.gMagma.numVectorSpace, this.gMagma, this.gMagma, this)
    }

    protected fun getCohomologyMultiplication(p: D, q: D): BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        val cohomOfDegP = this.getCohomologyVectorSpace(p)
        val cohomOfDegQ = this.getCohomologyVectorSpace(q)
        val cohomOfDegPPlusQ = this.getCohomologyVectorSpace(this.degreeGroup.context.run { p + q })
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
                        this.gMagma.getMultiplication(p, q)(vector1, vector2)
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

    override val cohomology: GMagma<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.gMagma.getInternalPrintConfig(printConfig))
        }
        GMagma(
            matrixSpace,
            this.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            listDegreesForAugmentedDegree = this.gMagma.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }

    /**
     * Returns a [DGLinearMap] which multiplies [cocycle] from left.
     *
     * [cocycle] must be a cocycle.
     * Use [GMagma.leftMultiplication] to get a [GLinearMap]
     * which multiplies a cochain (not cocycle) from left.
     */
    public fun leftMultiplication(cocycle: GVector<D, B, S, V>): DGLinearMap<D, B, B, S, V, M> {
        this.context.run {
            if (d(cocycle).isNotZero())
                throw IllegalArgumentException("Not cocycle: $cocycle (Use GMagma.leftMultiplication to multiply a non-cocycle)")
        }
        val gLinearMap = this.gMagma.leftMultiplication(cocycle)
        return DGLinearMap(this, this, gLinearMap)
    }

    public open fun getId(): DGLinearMap<D, B, B, S, V, M> {
        val gLinearMap = this.gVectorSpace.getId(this.matrixSpace)
        return DGLinearMap(this, this, gLinearMap)
    }
}
