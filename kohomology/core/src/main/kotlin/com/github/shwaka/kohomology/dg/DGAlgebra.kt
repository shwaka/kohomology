package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

class DGAlgebraContext<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, S, V, M>,
    dgVectorOperations: DGVectorOperations<B, S, V, M>
) : GAlgebraContext<B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations),
    DGVectorOperations<B, S, V, M> by dgVectorOperations {
    fun d(gVector: GVector<B, S, V>): GVector<B, S, V> {
        return this.differential(gVector)
    }
}

open class DGAlgebra<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    open val gAlgebra: GAlgebra<B, S, V, M>,
    differential: GLinearMap<B, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGVectorSpace<B, S, V, M>(gAlgebra, differential, matrixSpace) {
    private val dgAlgebraContext by lazy {
        DGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this)
    }
    fun <T> withDGAlgebraContext(block: DGAlgebraContext<B, S, V, M>.() -> T): T = this.dgAlgebraContext.block()

    override fun cohomology(): GAlgebra<SubQuotBasis<B, S, V>, S, V, M> {
        TODO("not implemented")
    }
}
