package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
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
import com.github.shwaka.kohomology.vectsp.VectorSpace

// no additional operations for Lie algebra (magma is enough)

public open class GLieAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
) : GMagmaContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations)

public open class GLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    degreeGroup: DegreeGroup<D>,
    name: String,
    getVectorSpace: (D) -> VectorSpace<B, S, V>,
    getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S> = { InternalPrintConfig.default(it) },
    listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
) : GMagma<D, B, S, V, M>(matrixSpace, degreeGroup, name, getVectorSpace, getMultiplication, getInternalPrintConfig, listDegreesForAugmentedDegree) {
    public override val context: GLieAlgebraContext<D, B, S, V, M> by lazy {
        // use 'lazy' to avoid the following warning:
        //   Leaking 'this' in constructor of non-final class GAlgebra
        GLieAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    }
}
