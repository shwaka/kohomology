package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorContext
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.GVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace

interface GAlgebraOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
}

class GAlgebraContext<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, S, V, M>,
) : GVectorContext<B, S, V>(scalarOperations, numVectorOperations, gVectorOperations), GAlgebraOperations<B, S, V, M> by gAlgebraOperations {
    operator fun GVector<B, S, V>.times(other: GVector<B, S, V>): GVector<B, S, V> {
        return this@GAlgebraContext.multiply(this, other)
    }
}

open class GAlgebra<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    getVectorSpace: (Degree) -> VectorSpace<B, S, V>,
    getMultiplication: (Degree, Degree) -> BilinearMap<B, B, B, S, V, M>
) : GVectorSpace<B, S, V>(matrixSpace.numVectorSpace, getVectorSpace), GAlgebraOperations<B, S, V, M> {
    private val gAlgebraContext = GAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    private val multiplication: GBilinearMap<B, B, B, S, V, M> by lazy {
        GBilinearMap(this, this, this, 0) { p, q -> getMultiplication(p, q) }
    }
    override fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        return this.multiplication(a, b)
    }
}
