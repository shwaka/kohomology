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

public open class DGAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
    gAlgebraOperations: GAlgebraOperations<D, B, S, V, M>,
    dgVectorOperations: DGVectorOperations<D, B, S, V, M>
) : DGVectorContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, dgVectorOperations),
    GAlgebraOperations<D, B, S, V, M> by gAlgebraOperations {
    private val gAlgebraContext = GAlgebraContext(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations, gAlgebraOperations)

    public operator fun GVector<D, B, S, V>.times(other: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this@DGAlgebraContext.gAlgebraContext.run { this@times * other }
    }

    public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
        return this@DGAlgebraContext.gAlgebraContext.run { this@pow.pow(exponent) }
    }
}

public open class DGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public open val gAlgebra: GAlgebra<D, B, S, V, M>,
    differential: GLinearMap<D, B, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGVectorSpace<D, B, S, V, M>(gAlgebra, differential, matrixSpace) {
    override val context: DGAlgebraContext<D, B, S, V, M> by lazy {
        DGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this.gAlgebra, this)
    }

    private fun getCohomologyMultiplication(p: D, q: D): BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        val cohomOfDegP = this.getCohomologyVectorSpace(p)
        val cohomOfDegQ = this.getCohomologyVectorSpace(q)
        val cohomOfDegPPlusQ = this.getCohomologyVectorSpace(this.gAlgebra.degreeGroup.context.run { p + q })
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
                        this.gAlgebra.getMultiplication(p, q)(vector1, vector2)
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

    override val cohomology: GAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        val cohomOfDeg0: SubQuotVectorSpace<B, S, V, M> = this.getCohomologyVectorSpace(0)
        val cohomologyUnit = cohomOfDeg0.projection(this.gAlgebra.unit.vector)
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.gAlgebra.getInternalPrintConfig(printConfig))
        }
        GAlgebra(
            matrixSpace,
            this.gAlgebra.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            cohomologyUnit,
            listDegreesForAugmentedDegree = this.gAlgebra.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }

    public fun getId(): DGAlgebraMap<D, B, B, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getId()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }

    /**
     * Returns a [DGLinearMap] which multiplies [cocycle] from left.
     *
     * [cocycle] must be a cocycle.
     * Use [GAlgebra.leftMultiplication] to get a [GLinearMap]
     * which multiplies a cochain (not cocycle) from left.
     */
    public fun leftMultiplication(cocycle: GVector<D, B, S, V>): DGLinearMap<D, B, B, S, V, M> {
        this.context.run {
            if (d(cocycle).isNotZero())
                throw IllegalArgumentException("Not cocycle: $cocycle (Use GAlgebra.leftMultiplication to multiply a non-cocycle)")
        }
        val gLinearMap = this.gAlgebra.leftMultiplication(cocycle)
        return DGLinearMap(this, this, gLinearMap)
    }
}
