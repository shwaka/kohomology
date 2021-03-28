package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorContext
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.GVectorSpace

interface GAlgebraOperations<B, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> {
    fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
}

class GAlgebraContext<B, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, S, V, M>,
) : GVectorContext<B, S, V>(scalarOperations, numVectorOperations, gVectorOperations), GAlgebraOperations<B, S, V, M> by gAlgebraOperations {
    operator fun GVector<B, S, V>.times(other: GVector<B, S, V>): GVector<B, S, V> {
        return this@GAlgebraContext.multiply(this, other)
    }
}

class GAlgebra<B, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    matrixSpace: MatrixSpace<S, V, M>,
    getBasisNames: (Degree) -> List<B>,
    private val multiplication: GBilinearMap<B, B, B, S, V, M>
) : GVectorSpace<B, S, V>(matrixSpace.numVectorSpace, getBasisNames), GAlgebraOperations<B, S, V, M> {
    private val gAlgebraContext = GAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    override fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        return this.multiplication(a, b)
    }
}
