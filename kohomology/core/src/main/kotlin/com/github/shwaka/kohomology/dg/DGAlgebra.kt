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
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

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

    private fun getCohomologyMultiplication(p: Degree, q: Degree): BilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, S, V, M> {
        val cohomOfDegP = this.getCohomologyVectorSpace(p)
        val cohomOfDegQ = this.getCohomologyVectorSpace(q)
        val cohomOfDegPPlusQ = this.getCohomologyVectorSpace(p + q)
        val basisLift1: List<Vector<B, S, V>> =
            cohomOfDegP.getBasis().map { vector1: Vector<SubQuotBasis<B, S, V>, S, V> ->
                cohomOfDegP.section(vector1)
            }
        val basisLift2: List<Vector<B, S, V>> =
            cohomOfDegP.getBasis().map { vector2: Vector<SubQuotBasis<B, S, V>, S, V> ->
                cohomOfDegP.section(vector2)
            }
        val values: List<List<Vector<SubQuotBasis<B, S, V>, S, V>>> =
            basisLift1.map { vector1: Vector<B, S, V> ->
                basisLift2.map { vector2: Vector<B, S, V> ->
                    cohomOfDegPPlusQ.projection(
                        this.gAlgebra.getMultiplication(p, q)(vector1, vector2)
                    )
                }
            }
        return BilinearMap.fromVectors(
            cohomOfDegP,
            cohomOfDegQ,
            cohomOfDegPPlusQ,
            this.matrixSpace,
            values
        )
    }

    override fun cohomology(): GAlgebra<SubQuotBasis<B, S, V>, S, V, M> {
        val cohomOfDeg0: SubQuotVectorSpace<B, S, V, M> = this.getCohomologyVectorSpace(0)
        val cohomologyUnit = cohomOfDeg0.projection(this.gAlgebra.unit.vector)
        return GAlgebra(
            matrixSpace,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            cohomologyUnit,
        )
    }
}
