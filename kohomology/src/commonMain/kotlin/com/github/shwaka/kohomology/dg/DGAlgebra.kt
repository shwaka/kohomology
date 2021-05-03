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
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.Vector

open class DGAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, D, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, D, S, V, M>,
    dgVectorOperations: DGVectorOperations<B, D, S, V, M>
) : DGVectorContext<B, D, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, dgVectorOperations),
    GAlgebraOperations<B, D, S, V, M> by gAlgebraOperations {
    private val gAlgebraContext = GAlgebraContext(scalarOperations, numVectorOperations, gVectorOperations, gAlgebraOperations)

    operator fun GVector<B, D, S, V>.times(other: GVector<B, D, S, V>): GVector<B, D, S, V> {
        return this@DGAlgebraContext.gAlgebraContext.run { this@times * other }
    }

    fun GVector<B, D, S, V>.pow(exponent: Int): GVector<B, D, S, V> {
        return this@DGAlgebraContext.gAlgebraContext.run { this@pow.pow(exponent) }
    }
}

open class DGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    open val gAlgebra: GAlgebra<B, D, S, V, M>,
    differential: GLinearMap<B, B, D, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGVectorSpace<B, D, S, V, M>(gAlgebra, differential, matrixSpace) {
    override val context by lazy {
        DGAlgebraContext(this.gAlgebra.field, this.gAlgebra.numVectorSpace, this.gAlgebra, this.gAlgebra, this)
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
        return BilinearMap(
            cohomOfDegP,
            cohomOfDegQ,
            cohomOfDegPPlusQ,
            this.matrixSpace,
            valueList
        )
    }

    override val cohomology: GAlgebra<SubQuotBasis<B, S, V>, D, S, V, M> by lazy {
        val cohomOfDeg0: SubQuotVectorSpace<B, S, V, M> = this.getCohomologyVectorSpace(0)
        val cohomologyUnit = cohomOfDeg0.projection(this.gAlgebra.unit.vector)
        GAlgebra(
            matrixSpace,
            this.gAlgebra.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace,
            this::getCohomologyMultiplication,
            cohomologyUnit,
        )
    }

    fun getId(): DGAlgebraMap<B, B, D, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getId()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }
}
