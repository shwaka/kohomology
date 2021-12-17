package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace

public open class DGAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
    gAlgebraOperations: GAlgebraOperations<D, B, S, V, M>,
    dgVectorOperations: DGVectorOperations<D, B, S, V, M>
) : DGMagmaContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations, dgVectorOperations),
    GAlgebraOperations<D, B, S, V, M> by gAlgebraOperations {
    private val gAlgebraContext = GAlgebraContext(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations, gAlgebraOperations)

    public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
        return this@DGAlgebraContext.gAlgebraContext.run { this@pow.pow(exponent) }
    }
}

public open class DGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public open val gAlgebra: GAlgebra<D, B, S, V, M>,
    differential: Derivation<D, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGMagma<D, B, S, V, M>(gAlgebra, differential, matrixSpace) {
    override val context: DGAlgebraContext<D, B, S, V, M> by lazy {
        DGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this.gAlgebra, this)
    }

    override val cohomology: GAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        val cohomOfDeg0: SubQuotVectorSpace<B, S, V, M> = this.getCohomologyVectorSpace(0)
        val cohomologyUnit = cohomOfDeg0.projection(this.gAlgebra.unit.vector)
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.gAlgebra.getInternalPrintConfig(printConfig))
        }
        GAlgebra(
            matrixSpace,
            this.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            cohomologyUnit,
            listDegreesForAugmentedDegree = this.gAlgebra.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }

    public override fun getId(): DGAlgebraMap<D, B, B, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getId()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }
}
