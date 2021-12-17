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

public open class DGLieAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
    dgVectorOperations: DGVectorOperations<D, B, S, V, M>
) : DGMagmaContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations, dgVectorOperations)

public open class DGLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public open val gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
    differential: GLinearMap<D, B, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGMagma<D, B, S, V, M>(gLieAlgebra, differential, matrixSpace) {
    override val context: DGLieAlgebraContext<D, B, S, V, M> by lazy {
        DGLieAlgebraContext(this.gLieAlgebra.field, this.gLieAlgebra.numVectorSpace, this.gLieAlgebra, this.gLieAlgebra, this)
    }

    override val cohomology: GLieAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        // Just override the type
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.gMagma.getInternalPrintConfig(printConfig))
        }
        GLieAlgebra(
            matrixSpace,
            this.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            listDegreesForAugmentedDegree = this.gMagma.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }
}
